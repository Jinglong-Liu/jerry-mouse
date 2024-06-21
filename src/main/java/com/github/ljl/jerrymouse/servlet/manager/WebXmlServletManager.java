package com.github.ljl.jerrymouse.servlet.manager;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.servlet.JerryMouseHttpTestServlet;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:33
 **/

public class WebXmlServletManager implements IServletManager {

    private static Logger logger = LoggerFactory.getLogger(JerryMouseHttpTestServlet.class);

    private IServletManager manager = new DefaultServletManager();

    public WebXmlServletManager() {
        // 仅执行一次
        this.loadFromWebXml();
    }
    /**
     * 1. 解析 web.xml
     * 2. 读取对应的 servlet mapping
     * 3. 注册对应的 url + servlet
     */
    private synchronized void loadFromWebXml() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
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
                    HttpServlet httpServlet = (HttpServlet) Class.forName(servletClassName).newInstance();
                    /**
                     * 3. 注册对应的 url + servlet
                     */
                    this.register(urlPattern, httpServlet);
                }
            }
        } catch (Exception e) {
            logger.error("[JerryMouse] read web.xml failed", e);
            throw new JerryMouseException(e);
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
}
