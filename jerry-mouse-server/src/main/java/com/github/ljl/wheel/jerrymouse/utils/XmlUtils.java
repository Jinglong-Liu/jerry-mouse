package com.github.ljl.wheel.jerrymouse.utils;

import com.github.ljl.wheel.jerrymouse.support.classloader.WebApplicationClassLoader;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContextManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.config.ServletConfigWrapper;
import com.github.ljl.wheel.jerrymouse.support.servlet.filter.FilterConfigImpl;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 16:27
 **/

public class XmlUtils {
    private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);
    public static void parseLocalWebXml() {
        // 指定类加载器，用以加载资源以及反射加载类，创建对象
        ClassLoader classLoader = XmlUtils.class.getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("web.xml");
        ApplicationContextManager.applyApplicationContext("/root");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            // 本地前缀设置为/root
            loadFromWebXml("/root", document, classLoader);
        } catch (DocumentException e) {
            logger.error("read web.xml error");
            e.printStackTrace();
        }
    }

    public static void loadApps(String baseDir, Set<String> appNameSet) {
        File dir = new File(baseDir);

        // filter subDirs
        final List<File> subDirs = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isDirectory)
                .filter(subdir -> appNameSet.contains(subdir.getName()))
                .collect(Collectors.toList());

        CountDownLatch latch = new CountDownLatch(subDirs.size());

        subDirs.forEach(subDir -> {
            ThreadPoolUtils.execute(() -> {
                try {
                    parseAppWebXml(subDir);
                    logger.debug("Load app " + subDir.getName() + " end");
                } catch (MalformedURLException e) {
                    logger.error("Load app " + subDir.getName() + " failed");
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        });
        try {
            latch.await();
            logger.info("Finish load {} webapps", subDirs.size());
        } catch (InterruptedException e) {
            logger.error("count down latch await error when finish load webapps");
            e.printStackTrace();
        }
    }

    public static void parseAppWebXml(File appDir) throws MalformedURLException {

        URL url = appDir.toURI().toURL();
        // 构造对应app的
        ClassLoader classLoader = new WebApplicationClassLoader(new URL[]{url}, XmlUtils.class.getClassLoader());
        File webXml = new File(appDir, "WEB-INF/web.xml");

        InputStream resourceAsStream;
        try {
            resourceAsStream = new FileInputStream(webXml);
        } catch (FileNotFoundException e) {
            logger.warn("web.xml not found in {}/WEB-INF", appDir.getAbsolutePath());
            return;
        }

        SAXReader saxReader = new SAXReader();
        String appName = "/" + appDir.getName(); // lastname, 并以/开头
        ApplicationContextManager.applyApplicationContext(appName);// apply servlet context
        try {
            Document document = saxReader.read(resourceAsStream);
            loadFromWebXml(appName, document, classLoader);
        } catch (DocumentException e) {
            logger.error("read web.xml error");
            e.printStackTrace();
        }
    }
    private static void loadFromWebXml(String appName, Document document, ClassLoader classLoader) {
        loadContextFromWebXml(appName, document);
        loadServletFromWebXml(appName, document, classLoader);
        loadFilterFromWebXml(appName, document, classLoader);
    }
    private static void loadContextFromWebXml(String appName, Document document) {
        /**
         * <context-param>
         *    <param-name>context-param1</param-name>
         *    <param-value>100</param-value>
         *  </context-param>
         */
        ServletContext applicationContext = ApplicationContextManager.getApplicationContext(appName);
        Element rootElement = document.getRootElement();
        List<Element> selectNodes = rootElement.elements("context-param");
        for (Element element : selectNodes) {
            String name = element.elementText("param-name");
            String value = element.elementText("param-value");
            applicationContext.setInitParameter(name, value);
        }
    }
    private static void loadServletFromWebXml(String appName, Document document, ClassLoader classLoader) {
        try {
            // 类加载器，用于反射创建servlet对象
            ApplicationContext applicationContext = ApplicationContextManager.getApplicationContext(appName);

            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("servlet");
            Map<String, ServletConfig> map = new HashMap<>();

            /**
             * 1, 找到所有的servlet标签，找到servlet-name和servlet-class, 创建对应的ServletConfig
             */
            for (Element element : selectNodes) {
                String name = element.elementText("servlet-name");
                String className = element.elementText("servlet-class");
                ServletConfig servletConfig = new ServletConfigWrapper(applicationContext,name, className);
                map.put(name, servletConfig);

                List<Element> initParamElements = element.elements("init-param");
                initParamElements.stream().forEach(param -> {
                    String paramName = param.elementText("param-name");
                    String paramValue = param.elementText("param-value");
                    ((ServletConfigWrapper) servletConfig).setInitParameter(paramName, paramValue);
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
                    String clazzName = ((ServletConfigWrapper) config).getClazzName();
                    Class<?> clazz = classLoader.loadClass(clazzName);
                    // 加载class;
                    HttpServlet httpServlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
                    /**
                     * 3. 注册对应的 url + servlet 到 context
                     */
                    applicationContext.registerServlet(appName + urlPattern, httpServlet);
                    /**
                     * 4. 绑定servlet和对应的config, 并初始化
                     */
                    httpServlet.init(config);
                }
            }
        } catch (Exception e) {
            logger.error("load server from web.xml failed", e);
            e.printStackTrace();
        }
    }
    private static void loadFilterFromWebXml(String appName, Document document, ClassLoader classLoader) {
        try {
            ApplicationContext applicationContext = ApplicationContextManager.getApplicationContext(appName);

            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.elements("filter");
            Map<String, FilterConfigImpl> map = new HashMap<>();

            /**
             * 格式类似servlet
             */
            for (Element element : selectNodes) {
                String name = element.elementText("filter-name");
                String className = element.elementText("filter-class");
                FilterConfigImpl filterConfig = new FilterConfigImpl(name, className, applicationContext);
                map.put(name, filterConfig);

                List<Element> initParamElements = element.elements("init-param");
                initParamElements.stream().forEach(param -> {
                    String paramName = param.elementText("param-name");
                    String paramValue = param.elementText("param-value");
                    // set init parameter
                    filterConfig.setInitParameter(paramName, paramValue);
                });
            }
            /**
             * 2, 根据filter-name找到<filter-mapping>中与其匹配的<url-pattern>
             */
            List<Element> mappingElements = rootElement.elements("filter-mapping");
            for (Element mappingElement : mappingElements) {
                String name = mappingElement.elementText("filter-name");
                String urlPattern = mappingElement.elementText("url-pattern");
                // 检查 <servlet-name> 是否存在于 <servlet> 元素中
                if (map.containsKey(name)) {
                    FilterConfigImpl config = map.get(name);
                    String clazzName = config.getClassName();
                    Class<?> clazz = classLoader.loadClass(clazzName);
                    // 加载class;
                    Filter filter = (Filter) clazz.getDeclaredConstructor().newInstance();
                    /**
                     * 3. 注册对应的 url + filter 到 context
                     */
                    applicationContext.registerFilter(appName + urlPattern, filter);
                    /**
                     * 4. 绑定filter和对应的config, 并初始化
                     */
                    filter.init(config);
                }
            }
        } catch (Exception e) {
            logger.error("load server from web.xml failed", e);
            e.printStackTrace();
        }
    }
}
