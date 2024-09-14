package com.github.ljl.wheel.jerrymouse.utils;

import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContextManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.config.ServletConfigWrapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            // 本地servlet不用前缀
            loadFromWebXml("", document, classLoader);
        } catch (DocumentException e) {
            logger.error("read web.xml error");
            e.printStackTrace();
        }
    }
    private static void loadFromWebXml(String urlPrefix, Document document, ClassLoader classLoader) {
        loadServletFromWebXml(urlPrefix, document, classLoader);
    }
    private static void loadServletFromWebXml(String urlPrefix, Document document, ClassLoader classLoader) {
        try {
            // 类加载器，用于反射创建servlet对象
            ApplicationContext applicationContext = ApplicationContextManager.getApplicationContext();

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
                    applicationContext.registerServlet(urlPrefix + urlPattern, httpServlet);
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
}
