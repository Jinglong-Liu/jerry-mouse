package com.github.ljl.jerrymouse.support.xml;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.classloader.IClassLoader;
import com.github.ljl.jerrymouse.classloader.LocalClassloader;
import com.github.ljl.jerrymouse.classloader.WebAppClassLoader;
import com.github.ljl.jerrymouse.support.filter.DefaultFilterManager;
import com.github.ljl.jerrymouse.support.filter.IFilterManager;
import com.github.ljl.jerrymouse.support.listener.DefaultListenerManager;
import com.github.ljl.jerrymouse.support.listener.IListenerManager;
import com.github.ljl.jerrymouse.support.servlet.DefaultServletManager;
import com.github.ljl.jerrymouse.support.servlet.IServletManager;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-23 19:08
 **/

public class WebXmlManager implements IWebXmlManager {

    private static Logger logger = LoggerFactory.getLogger(WebXmlManager.class);

    private IServletManager servletManager = DefaultServletManager.get();

    private IFilterManager filterManager = DefaultFilterManager.get();

    private IListenerManager listenerManager = DefaultListenerManager.get();

    private String baseDirStr;

    private static WebXmlManager instance;
    private WebXmlManager() {

    }

    public static WebXmlManager get() {
        if (instance == null) {
            synchronized (WebXmlManager.class) {
                if (instance == null) {
                    instance = new WebXmlManager();
                }
            }
        }
        return instance;
    }

    private void loadFromWebXml(String urlPrefix, Document document, IClassLoader classLoader) {
        loadServletFromWebXml(urlPrefix, document, classLoader);
        loadFilterFromWebXml(urlPrefix, document, classLoader);
        loadListenerFromWebXml(urlPrefix, document, classLoader);
        // TODO: other tags
    }

    private void loadServletFromWebXml(String urlPrefix, Document document, IClassLoader servletClassLoader) {
        try {
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("servlet");
            Map<String, String> map = new HashMap<>();
            /**
             * 1, 找到所有的servlet标签，找到servlet-name和servlet-class
             */
            for (Element element : selectNodes) {
                String name = element.elementText("servlet-name");
                String className = element.elementText("servlet-class");
                map.put(name, className);
            }
            /**
             * 2, 根据servlet-name找到<servlet-mapping>中与其匹配的<url-pattern>
             */
            List<Element> mappingElements = rootElement.elements("servlet-mapping");
            for (Element mappingElement : mappingElements) {
                String name = mappingElement.elementText("servlet-name");
                String urlPattern = mappingElement.elementText("url-pattern");
                // 检查 <servlet-name> 是否存在于 <servlet> 元素中
                if (map.containsKey(name)) {
                    String className = map.get(name);
                    // 加载class
                    Class clazz = servletClassLoader.loadClass(className);
                    HttpServlet httpServlet = (HttpServlet) clazz.newInstance();
                    /**
                     * 3. 注册对应的 url + servlet
                     */
                    servletManager.register(urlPrefix + urlPattern, httpServlet);
                }
            }
        } catch (Exception e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            e.printStackTrace();
        }
    }

    private void loadFilterFromWebXml(String urlPrefix, Document document, IClassLoader servletClassLoader) {
        try {
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("filter");
            Map<String, String> map = new HashMap<>();
            /**
             * 1, 找到所有的filter标签，找到filter-name和filter-class
             */
            for (Element element : selectNodes) {
                String name = element.elementText("filter-name");
                String className = element.elementText("filter-class");
                map.put(name, className);
            }
            /**
             * 2, 根据filter-name找到<servlet-mapping>中与其匹配的<url-pattern>
             */
            List<Element> mappingElements = rootElement.elements("filter-mapping");
            for (Element mappingElement : mappingElements) {
                String name = mappingElement.elementText("filter-name");
                String urlPattern = mappingElement.elementText("url-pattern");
                // 检查 <servlet-name> 是否存在于 <servlet> 元素中
                if (map.containsKey(name)) {
                    String className = map.get(name);
                    // 加载class
                    Class clazz = servletClassLoader.loadClass(className);
                    Filter filter = (Filter) clazz.newInstance();
                    /**
                     * 3. 注册对应的 url + servlet
                     */
                    filterManager.register(urlPrefix + urlPattern, filter);
                }
            }
        } catch (Exception e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            e.printStackTrace();
        }
    }

    private void loadListenerFromWebXml(String urlPrefix, Document document, IClassLoader servletClassLoader) {
        try {
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("listener");
            /**
             * 1, 找到所有的listener标签，找到listener-class
             */
            for (Element element : selectNodes) {
                String className = element.elementText("listener-class");
                Class clazz = servletClassLoader.loadClass(className);
                EventListener listener = (EventListener) clazz.newInstance();
                /**
                 * 2. 注册对应的 urlPrefix + listener
                 */
                listenerManager.register(urlPrefix, listener);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            e.printStackTrace();
        }
    }

    @Override
    public void parseWebappXml(String baseDirStr) {
        logger.info("[JerryMouse] servlet init with baseDir={}", baseDirStr);

        this.baseDirStr = baseDirStr;

        // 分别解析 war 包
        File baseDir = new File(baseDirStr);

        File[] files = baseDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                handleWarPackage(file);
            }
        }
    }

    @Override
    public void parseLocalWebXml() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            loadFromWebXml("", document, new LocalClassloader());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    private void handleWarPackage(File warDir) {
        logger.info("JerryMouse handleWarPackage file={}", warDir);
        String prefix = "/" + warDir.getName();
        // 模拟 tomcat，如果在根目录下，war 的名字为 root。则认为是根路径项目。
        if ("ROOT".equalsIgnoreCase(prefix)) {
            prefix = "";
        }

        String webXmlPath = getWebXmlPath(warDir);
        File webXmlFile = new File(webXmlPath);
        if (!webXmlFile.exists()) {
            logger.warn("[JerryMouse] webXmlPath={} not found", webXmlPath);
            return;
        }
        loadAndRegisterWebapps(this.baseDirStr, prefix, webXmlFile, warDir);
    }
    private String getWebXmlPath(File file) {
        return file.getAbsolutePath() + "/WEB-INF/web.xml";
    }

    private void loadAndRegisterWebapps(String baseDir, String urlPrefix, File webXmlFile, File warDir) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(webXmlFile);
            // 自定义 class loader
            Path classesPath = buildClassesPath(baseDir, warDir);
            Path libPath = buildLibPath(baseDir, warDir);
            // IClassLoader classLoader = new WebAppClassLoader(classesPath, libPath);
            IClassLoader classLoader = new WebAppClassLoader(classesPath, libPath);
            loadFromWebXml(urlPrefix, document, classLoader);
        } catch (Exception e) {
            throw new JerryMouseException(e);
        }
    }

    private Path buildClassesPath(String baseDirStr, File warDir) {
        String path = JerryMouseResourceUtils.buildFullPath(baseDirStr, warDir.getName() + "/WEB-INF/classes/");
        return Paths.get(path);
    }

    private Path buildLibPath(String baseDirStr, File warDir) {
        String path = JerryMouseResourceUtils.buildFullPath(baseDirStr, warDir.getName() + "/WEB-INF/lib/");
        return Paths.get(path);
    }
}
