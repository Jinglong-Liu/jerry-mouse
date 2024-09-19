package com.github.ljl.wheel.jerrymouse.support.servlet.registration;

import com.github.ljl.wheel.jerrymouse.exception.MethodNotSupportException;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.servlet.config.ServletConfigWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import javax.servlet.http.HttpServlet;
import java.util.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-18 14:55
 **/

public class ApplicationServletRegistration implements ServletRegistration.Dynamic {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServletRegistration.class);

    private final HttpServlet servlet;

    private final ServletConfigWrapper servletConfig;

    private final ApplicationContext applicationContext;

    private final String servletName;

    public ApplicationServletRegistration(ApplicationContext applicationContext, String servletName, Servlet servlet) {
        this.applicationContext = applicationContext;
        this.servletName = servletName;
        this.servlet = (HttpServlet) servlet;
        this.servletConfig = (ServletConfigWrapper) servlet.getServletConfig();
    }

    /**
     * 目前注册在web.xml的servlet都是直接全部加载的，暂不支持oadOnStartup设置
     * @param loadOnStartup
     */
    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        logger.error("setLoadOnStartup not support");
        // throw new MethodNotSupportException("setLoadOnStartup not support");
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        logger.error("setServletSecurity not support");
        throw new MethodNotSupportException("setServletSecurity not support");
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        if (Objects.isNull(multipartConfig)) {
            logger.error("MultipartConfigElement cannot be null");
            throw new IllegalArgumentException("MultipartConfigElement cannot be null");
        }
        applicationContext.setAttribute("multipartConfig", multipartConfig);
    }

    @Override
    public void setRunAsRole(String roleName) {
        logger.error("addMappingForServletNames not support");
        throw new MethodNotSupportException("addMappingForServletNames not support");
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        logger.error("setAsyncSupported not support");
        // throw new MethodNotSupportException("setAsyncSupported not support");
    }

    /**
     * 可以多个urlPattern对应一个servlet
     * @param urlPatterns
     * @return
     */
    @Override
    public Set<String> addMapping(String... urlPatterns) {
        Set<String> patternSet = new HashSet<>(applicationContext.getServletPatterns());
        Set<String> conflictSet = new HashSet<>();
        Arrays.stream(urlPatterns)
                .forEach(urlPattern -> {
                    if (patternSet.contains(urlPattern)) {
                        conflictSet.add(urlPattern);
                    } else {
                        applicationContext.registerServlet(urlPattern, servlet);
                    }
                });
        return conflictSet;
    }

    @Override
    public Collection<String> getMappings() {
        return applicationContext.getServletPatterns(servlet);
    }

    @Override
    public String getRunAsRole() {
        logger.error("getRunAsRole not support");
        throw new MethodNotSupportException("getRunAsRole not support");
    }

    @Override
    public String getName() {
        return servletName;
    }

    @Override
    public String getClassName() {
        return Objects.nonNull(servlet) ? servlet.getClass().getName() : null;
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return servletConfig.setInitParameter(name, value);
    }

    @Override
    public String getInitParameter(String name) {
        return servlet.getInitParameter(name);
    }

    /**
     *
     * @param initParameters
     * @return Set of initialization parameter names that are in conflict
     */
    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        Set<String>set = Collections.emptySet();
        initParameters.entrySet().forEach(entry -> {
            // conflict
            if (!servletConfig.setInitParameter(entry.getKey(), entry.getValue())) {
                set.add(entry.getKey());
            }
        });
        return set;
    }

    @Override
    public Map<String, String> getInitParameters() {
        return servletConfig.getInitParameters();
    }
}
