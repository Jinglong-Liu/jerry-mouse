package com.github.ljl.jerrymouse.servlet.manager;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.support.classloader.IClassLoader;
import com.github.ljl.jerrymouse.support.classloader.WebAppClassLoader;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 14:03
 * @since 0.5.0
 **/

public class WarServletManager implements IServletManager {

    private static Logger logger = LoggerFactory.getLogger(WarServletManager.class);

    private String baseDir;

    private IServletManager manager = DefaultServletManager.get();

    private WebXmlServletManager webXmlServletManager = new WebXmlServletManager();

    @Override
    public void init(String baseDir) {
        this.baseDir = baseDir;
        this.doInit(baseDir);
    }

    private void doInit(String baseDirStr) {
        logger.info("[JerryMouse] servlet init with baseDir={}", baseDirStr);
        this.baseDir = baseDirStr;

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
    public void register(String url, HttpServlet servlet) {
        manager.register(url, servlet);
    }

    @Override
    public HttpServlet getServlet(String url) {
        return manager.getServlet(url);
    }

    /**
     * 处理单个 war 包
     * @param warDir
     */
    private void handleWarPackage(File warDir) {
        logger.info("JerryMouse handleWarPackage baseDirStr={}, file={}", baseDir, warDir);
        String prefix = "/" + warDir.getName();
        // 模拟 tomcat，如果在根目录下，war 的名字为 root。则认为是根路径项目。
        if ("ROOT".equalsIgnoreCase(prefix)) {
            // 这里需要 / 吗？
            prefix = "";
        }

        String webXmlPath = getWebXmlPath(warDir);
        File webXmlFile = new File(webXmlPath);
        if (!webXmlFile.exists()) {
            logger.warn("[JerryMouse] webXmlPath={} not found", webXmlPath);
            return;
        }

        loadUrlAndServletClass(prefix, webXmlFile, warDir);
    }
    protected String getWebXmlPath(File file) {
        return file.getAbsolutePath() + "/WEB-INF/web.xml";
    }
    protected void loadUrlAndServletClass(String urlPrefix,
                                          File webXmlFile,
                                          File warDir) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(webXmlFile);
            // 自定义 class loader
            Path classesPath = buildClassesPath(baseDir, warDir);
            Path libPath = buildLibPath(baseDir, warDir);
            // IClassLoader classLoader = new WebAppClassLoader(classesPath, libPath);
            IClassLoader classLoader = new WebAppClassLoader(classesPath, libPath);
            webXmlServletManager.loadFromWebXml(urlPrefix, document, classLoader);
        } catch (Exception e) {
            throw new JerryMouseException(e);
        }
    }
    private void loadFromWebXml(String urlPrefix,
                                InputStream resourceAsStream,
                                IClassLoader servletClassLoader,
                                File webXmlFile,
                                File warDir) {
        // 自定义 class loader
        Path classesPath = buildClassesPath(baseDir, warDir);
        Path libPath = buildLibPath(baseDir, warDir);
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("servlet");
            Map<String, String> servletMap = new HashMap<>();
            /**
             * 1, 找到所有的servlet标签，找到servlet-name和servlet-class
             */
            for (Element servletElement : selectNodes) {
                String servletName = servletElement.elementText("servlet-name");
                String servletClassName = servletElement.elementText("servlet-class");
                servletMap.put(servletName, servletClassName);
            }
            /**
             * 2, 根据servlet-name找到<servlet-mapping>中与其匹配的<url-pattern>
             */
            List<Element> mappingElements = rootElement.elements("servlet-mapping");
            for (Element mappingElement : mappingElements) {
                String servletName = mappingElement.elementText("servlet-name");
                String urlPattern = mappingElement.elementText("url-pattern");
                // 检查 <servlet-name> 是否存在于 <servlet> 元素中
                if (servletMap.containsKey(servletName)) {
                    String servletClassName = servletMap.get(servletName);
                    // 加载class
                    Class servletClazz = servletClassLoader.loadClass(servletClassName);
                    HttpServlet httpServlet = (HttpServlet) servletClazz.newInstance();
                    /**
                     * 3. 注册对应的 url + servlet
                     */
                    this.register(urlPrefix + urlPattern, httpServlet);
                }
            }
        } catch (Exception e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            throw new JerryMouseException(e);
        }
    }

    protected Path buildClassesPath(String baseDirStr, File warDir) {
        String path = JerryMouseResourceUtils.buildFullPath(baseDirStr, warDir.getName() + "/WEB-INF/classes/");
        return Paths.get(path);
    }

    protected Path buildLibPath(String baseDirStr, File warDir) {
        String path = JerryMouseResourceUtils.buildFullPath(baseDirStr, warDir.getName() + "/WEB-INF/lib/");
        return Paths.get(path);
    }
}
