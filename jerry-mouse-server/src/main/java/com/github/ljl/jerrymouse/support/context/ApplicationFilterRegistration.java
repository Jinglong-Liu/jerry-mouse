package com.github.ljl.jerrymouse.support.context;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-07-01 19:11
 **/

public class ApplicationFilterRegistration implements FilterRegistration.Dynamic {

    private JerryMouseAppContext servletContext;
    public ApplicationFilterRegistration(ServletContext servletContext) {
        this.servletContext = (JerryMouseAppContext) servletContext;
    }
    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {

    }

    @Override
    public Collection<String> getServletNameMappings() {
        return servletContext.getServletManager().getServlets().keySet();
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {

    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return null;
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return false;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        return null;
    }

    @Override
    public Map<String, String> getInitParameters() {
        return null;
    }
}
