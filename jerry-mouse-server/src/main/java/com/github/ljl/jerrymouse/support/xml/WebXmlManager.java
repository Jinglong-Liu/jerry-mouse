package com.github.ljl.jerrymouse.support.xml;

import com.github.ljl.jerrymouse.annotation.Singleton;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.classloader.LocalClassloader;
import com.github.ljl.jerrymouse.classloader.WebAppClassLoader;
import com.github.ljl.jerrymouse.support.context.*;
import com.github.ljl.jerrymouse.support.servlet.JerryMouseServletConfig;
import com.github.ljl.jerrymouse.support.threadpool.JerryMouseThreadPoolUtil;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
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

@Singleton
public class WebXmlManager implements IWebXmlManager {

    private static Logger logger = LoggerFactory.getLogger(WebXmlManager.class);

    private IContextManager contextManager = JerryMouseContextManager.get();

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

    private void loadFromWebXml(String urlPrefix, Document document, ServletContext servletContext) {
        ClassLoader classLoader = servletContext.getClassLoader();
        loadListenerFromWebXml(urlPrefix, document, classLoader);
        loadFilterFromWebXml(urlPrefix, document, classLoader);
        loadContextParamFromWebXml(urlPrefix, document, classLoader);
        loadServletFromWebXml(urlPrefix, document, classLoader);
        // TODO: load other tags
        ((JerryMouseAppContext) servletContext).initializeServletContextListeners();
    }

    private ServletContext addAppContext(String baseDir, String urlPrefix, ClassLoader classLoader) {
        ServletContext context = new JerryMouseAppContext(baseDir, urlPrefix, classLoader);
        contextManager.registerServletContext(urlPrefix, context);
        return context;
    }

    private void loadServletFromWebXml(String urlPrefix, Document document, ClassLoader classLoader) {
        try {
            JerryMouseAppContext appContext = (JerryMouseAppContext) contextManager.getServletContext(urlPrefix);

            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("servlet");
            Map<String, ServletConfig> map = new HashMap<>();

            /**
             * 1, 找到所有的servlet标签，找到servlet-name和servlet-class, 创建对应的ServletConfig
             */
            for (Element element : selectNodes) {
                String name = element.elementText("servlet-name");
                String className = element.elementText("servlet-class");
                ServletConfig servletConfig = new JerryMouseServletConfig(appContext, name, className);
                map.put(name, servletConfig);

                List<Element> initParamElements = element.elements("init-param");
                initParamElements.stream().forEach(param -> {
                    String paramName = param.elementText("param-name");
                    String paramValue = param.elementText("param-value");
                    ((JerryMouseServletConfig) servletConfig).setInitParameter(paramName, paramValue);
                });
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
                    ServletConfig config = map.get(name);
                    String clazzName = ((JerryMouseServletConfig) config).getClazzName();
                    Class clazz = classLoader.loadClass(clazzName);
                    // 加载class;
                    HttpServlet httpServlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
                    // 对于springmvc 项目，DispatchServlet 的init，会根据param中的contextConfigLocation，设置location
                    /**
                     * 3. 注册对应的 url + servlet 到 context
                     */
                    appContext.registerServlet(urlPrefix + urlPattern, httpServlet);
                    // 设置当前webapp线程ClassLoader
                    // Thread.currentThread().setContextClassLoader(classLoader);
                    httpServlet.init(config);
                }
            }
        } catch (Exception e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            e.printStackTrace();
        }
    }

    private void loadFilterFromWebXml(String urlPrefix, Document document, ClassLoader classLoader) {
        try {
            JerryMouseAppContext appContext = (JerryMouseAppContext) contextManager.getServletContext(urlPrefix);

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
                    Class clazz = classLoader.loadClass(className);
                    Filter filter = (Filter) clazz.newInstance();
                    /**
                     * 3. 注册对应的 url + servlet
                     */
                    appContext.registerFilter(urlPrefix + urlPattern, filter);
                    // filterManager.register(urlPrefix + urlPattern, filter);
                }
            }
        } catch (Exception e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            e.printStackTrace();
        }
    }

    private void loadListenerFromWebXml(String urlPrefix, Document document, ClassLoader classLoader) {
        try {
            JerryMouseAppContext appContext = (JerryMouseAppContext) contextManager.getServletContext(urlPrefix);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("listener");
            /**
             * 1, 找到所有的listener标签，找到listener-class
             */
            for (Element element : selectNodes) {
                String className = element.elementText("listener-class");
                Class clazz = classLoader.loadClass(className);
                EventListener listener = (EventListener) clazz.newInstance();
                /**
                 * 2. 注册对应的 urlPrefix + listener
                 */
                appContext.registerListener(listener);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadContextParamFromWebXml(String urlPrefix, Document document, ClassLoader classLoader) {
        try {
            JerryMouseAppContext appContext = (JerryMouseAppContext) contextManager.getServletContext(urlPrefix);
            Element rootElement = document.getRootElement();
            /**
             * 根据<context-param>标签设置全局的初始化表
             */
            List<Element> contextParamElements = rootElement.elements("context-param");
            contextParamElements.forEach(element ->  {
                String paramName = element.elementText("param-name");
                String paramValue = element.elementText("param-value");
                appContext.setInitParameter(paramName, paramValue);
            });
        } catch (Exception e) {
            logger.error("[JerryMouse] load-ContextParam from web.xml failed", e);
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
            final ServletContext context = addAppContext("", "/", new LocalClassloader());
            // 本地
            JerryMouseThreadPoolUtil.get().execute(() -> {
                loadFromWebXml("", document, context);
            });
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
        final String urlPrefix = prefix;
        // 每个webapp一个thread, 绑定各自的classLoader
        JerryMouseThreadPoolUtil.get().execute(() -> {
            loadAndRegisterWebapps(this.baseDirStr, urlPrefix, webXmlFile, warDir);
        });
    }
    private String getWebXmlPath(File file) {
        return file.getAbsolutePath() + "/WEB-INF/web.xml";
    }

    private void loadAndRegisterWebapps(String baseDir, String urlPrefix, File webXmlFile, File warDir) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(webXmlFile);
            String basePath = JerryMouseResourceUtils.buildFullPath(baseDir, warDir.getName());
            URL baseDirURL = new File(basePath).toURI().toURL();
            // set classLoader
            ClassLoader classLoader = new WebAppClassLoader(new URL[]{baseDirURL}, new LocalClassloader());
            Thread.currentThread().setContextClassLoader(classLoader);
            // 一个ServletContext对应一个app, 对应一个classLoader
            ServletContext context = addAppContext(baseDirStr, urlPrefix, classLoader);
            //
            loadFromWebXml(urlPrefix, document, context);
        } catch (Exception e) {
            throw new JerryMouseException(e);
        }
    }
}
