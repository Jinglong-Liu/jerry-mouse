package com.github.ljl.jerrymouse.support.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-25 17:04
 **/

public class JerryMouseServletConfig implements ServletConfig {

    private final String servletName;

    private final String clazzName;

    private final ServletContext servletContext;

    private Map<String, String> initParams = new HashMap<>();

    public JerryMouseServletConfig(ServletContext servletContext, String servletName, String clazzName) {
        this.servletContext = servletContext;
        this.servletName = servletName;
        this.clazzName = clazzName;
    }
    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return initParams.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParams.keySet());
    }

    public void setInitParameter(String paramName, String paramValue) {
        initParams.put(paramName, paramValue);
    }

    public String getClazzName() {
        return clazzName;
    }
}
