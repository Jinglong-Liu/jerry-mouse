package com.github.ljl.wheel.jerrymouse.support.servlet.registration;

import com.github.ljl.wheel.jerrymouse.exception.MethodNotSupportException;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.servlet.filter.FilterConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.http.HttpFilter;
import java.util.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-18 14:53
 **/

public class ApplicationFilterRegistration implements FilterRegistration.Dynamic {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFilterRegistration.class);

    private final ApplicationContext applicationContext;

    private final String filterName;

    private final Filter filter;

    private FilterConfigImpl filterConfig;

    public ApplicationFilterRegistration(ApplicationContext applicationContext, String filterName, Filter filter) {
        this.applicationContext = applicationContext;
        this.filterName = filterName;
        this.filter = filter;
        if (filter instanceof HttpFilter) {
            this.filterConfig = (FilterConfigImpl) ((HttpFilter) filter).getFilterConfig();
        }
    }
    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        logger.error("addMappingForServletNames not support");
        throw new MethodNotSupportException("addMappingForServletNames not support");
    }

    /**
     * 返回和这个filter匹配的所有servletName
     * @return
     */
    @Override
    public Collection<String> getServletNameMappings() {
        logger.error("getServletNameMappings not support");
        throw new MethodNotSupportException("getServletNameMappings not support");
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        // characterEncodingFilter， formContentFilter, requestContextFilter
        // dispatcherTypes = FORWARD, INCLUDE, REQUEST, ASYNC, ERROR
        // urlPatterns = [/*]
        logger.error("addMappingForUrlPatterns not support");
        // throw new MethodNotSupportException("addMappingForUrlPatterns not support");
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        logger.error("getUrlPatternMappings not support");
        throw new MethodNotSupportException("getUrlPatternMappings not support");
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        logger.error("setAsyncSupported not support");
        // throw new MethodNotSupportException("setAsyncSupported not support");
    }

    @Override
    public String getName() {
        return filterName;
    }

    @Override
    public String getClassName() {
        return filter.getClass().getName();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return filterConfig.setInitParameter(name, value);
    }

    @Override
    public String getInitParameter(String name) {
        return filterConfig.getInitParameter(name);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        Set<String>set = Collections.emptySet();
        initParameters.entrySet().forEach(entry -> {
            // conflict
            if (!filterConfig.setInitParameter(entry.getKey(), entry.getValue())) {
                set.add(entry.getKey());
            }
        });
        return set;
    }

    @Override
    public Map<String, String> getInitParameters() {
        return filterConfig.getInitParameters();
    }
}
