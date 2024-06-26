package com.github.ljl.jerrymouse.support.context;

import com.github.ljl.jerrymouse.support.filter.DefaultFilterManager;
import com.github.ljl.jerrymouse.support.filter.IFilterManager;
import com.github.ljl.jerrymouse.support.listener.DefaultListenerManager;
import com.github.ljl.jerrymouse.support.listener.IListenerManager;
import com.github.ljl.jerrymouse.support.servlet.DefaultServletManager;
import com.github.ljl.jerrymouse.support.servlet.IServletManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-24 10:39
 **/

public class JerryMouseAppContext extends JerryMouseContextAdaptor implements IAppContext {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseAppContext.class);

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    private Map<String, String> initContextParams = new HashMap<>();

    @Getter
    private final String urlPrefix;

    @Getter
    private IListenerManager listenerManager = new DefaultListenerManager();

    @Getter
    private IFilterManager filterManager = new DefaultFilterManager();

    @Getter
    private IServletManager servletManager = new DefaultServletManager();

    public JerryMouseAppContext(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<>(attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public void registerServlet(String urlPattern, Servlet servlet) {
        logger.info("[JerryMouse] register servlet, key = {}, url={}, servlet={}", urlPrefix, urlPattern, servlet.getClass().getName());
        servletManager.register(urlPattern, (HttpServlet) servlet);
    }

    @Override
    public void registerFilter(String urlPattern, Filter filter) {
        logger.info("[JerryMouse] register filter, key = {}, url={}, servlet={}", urlPrefix, urlPattern, filter.getClass().getName());
        filterManager.register(urlPattern, filter);
    }

    @Override
    public void registerListener(EventListener listener) {
        logger.info("[JerryMouse] register listener, key = {}, listener={}", urlPrefix, listener.getClass().getName());
        listenerManager.register("", listener);
    }

    @Override
    public void initializeServletContextListeners() {
        getListenersBySubType(ServletContextListener.class).forEach(listener -> {
            listener.contextInitialized(new ServletContextEvent(this));
        });
    }

    @Override
    public void setAttribute(String s, Object o) {

        List<ServletContextAttributeListener> listeners = getListenersBySubType(ServletContextAttributeListener.class);
        ServletContextAttributeEvent contextEvent = new ServletContextAttributeEvent(this, s, o);

        if (!attributes.containsKey(s)) {
            attributes.put(s, o);
            // add
            listeners.forEach(listener -> listener.attributeAdded(contextEvent));
        } else if (!attributes.get(s).equals(o)){
            attributes.put(s, o);
            // replace
            listeners.forEach(listener -> listener.attributeReplaced(contextEvent));
        } else {
            // do nothing
        }
    }

    @Override
    public void removeAttribute(String s) {
        if(attributes.containsKey(s)) {
            Object value = attributes.get(s);
            attributes.remove(s);
            // remove
            List<ServletContextAttributeListener> listeners = getListenersBySubType(ServletContextAttributeListener.class);
            ServletContextAttributeEvent contextEvent = new ServletContextAttributeEvent(this, s, value);
            listeners.forEach(listener -> listener.attributeRemoved(contextEvent));
        }
    }

    private <T extends EventListener> List<T> getListenersBySubType(Class<T> eventType) {
        return listenerManager.getListeners()
                .stream()
                .filter(listener -> eventType.isInstance(listener))
                .map(listener -> eventType.cast(listener))
                .collect(Collectors.toList());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        if(initContextParams.containsKey(name)) {
            return false;
        }
        initContextParams.put(name, value);
        return true;
    }

    @Override
    public String getInitParameter(String s) {
        return initContextParams.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return  Collections.enumeration(initContextParams.keySet());
    }
}
