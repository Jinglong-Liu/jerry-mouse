package com.github.ljl.jerrymouse.support.context;

import com.github.ljl.jerrymouse.support.listener.DefaultListenerManager;
import com.github.ljl.jerrymouse.support.listener.IListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
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

    private IListenerManager listenerManager = DefaultListenerManager.get();

    private JerryMouseAppContext() {

    }
    private static JerryMouseAppContext instance;

    public static JerryMouseAppContext get() {
        if (Objects.isNull(instance)) {
            synchronized (JerryMouseAppContext.class) {
                if (Objects.isNull(instance)) {
                    instance = new JerryMouseAppContext();
                }
            }
        }
        return instance;
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
    public void setAttribute(String s, Object o) {

        List<ServletContextAttributeListener> listeners = getAttributeListeners();
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
            List<ServletContextAttributeListener> listeners = getAttributeListeners();
            ServletContextAttributeEvent contextEvent = new ServletContextAttributeEvent(this, s, value);
            listeners.forEach(listener -> listener.attributeRemoved(contextEvent));
        }
    }

    private List<ServletContextAttributeListener> getAttributeListeners() {
        return listenerManager.getListeners()
                .stream()
                .filter(listener -> listener instanceof ServletContextAttributeListener)
                .map(listener -> (ServletContextAttributeListener) listener)
                .collect(Collectors.toList());
    }
}
