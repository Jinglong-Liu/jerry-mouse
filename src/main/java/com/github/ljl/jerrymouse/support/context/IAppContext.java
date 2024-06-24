package com.github.ljl.jerrymouse.support.context;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.Enumeration;
import java.util.EventListener;

public interface IAppContext {
    void setAttribute(String name, Object object);

    void removeAttribute(String name);

    Object getAttribute(String name);

    Enumeration<String> getAttributeNames();

    void registerServlet(String urlPattern, Servlet servlet);

    void registerFilter(String urlPattern, Filter filter);

    void registerListener(EventListener listener);
}
