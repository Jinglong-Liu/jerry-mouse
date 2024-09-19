package com.github.ljl.wheel.jerrymouse.support.servlet.config;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 13:42
 **/

public class ServletConfigWrapper implements ServletConfig {
    private final String servletName;

    private final String clazzName;

    private final ServletContext servletContext;

    private Map<String, String> initParams = new HashMap<>();

    public ServletConfigWrapper(ServletContext servletContext, String servletName, String clazzName) {
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

    public boolean setInitParameter(String paramName, String paramValue) {
        if (initParams.containsKey(paramName)) {
            return false;
        }

        initParams.put(paramName, paramValue);
        return true;
    }

    public Map<String, String> getInitParameters() {
        return Objects.nonNull(initParams) ? initParams: Collections.emptyMap();
    }

    public String getClazzName() {
        return clazzName;
    }
}