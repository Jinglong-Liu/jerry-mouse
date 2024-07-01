package com.github.ljl.jerrymouse.support.context;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.support.servlet.JerryMouseServletConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-07-01 18:38
 **/

public class ApplicationServletRegistration implements ServletRegistration.Dynamic {

    private final JerryMouseAppContext context;

    private final String name;

    private final Servlet servlet;

    public ApplicationServletRegistration(String name, JerryMouseAppContext context, Servlet servlet) {
        this.context = context;
        this.name = name;
        this.servlet = servlet;
    }
    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        System.out.println("setLoadOnStartup:" + loadOnStartup);
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        return null;
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {

    }

    @Override
    public void setRunAsRole(String roleName) {

    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {

    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        // url -> this servlet
        Arrays.stream(urlPatterns).forEach(url -> {
            context.getServletManager().register(url, (HttpServlet) servlet);
        });
        return new HashSet<>();
    }

    @Override
    public Collection<String> getMappings() {
        return context.getServletManager()
                .getServlets()
                .keySet()
                .stream()
                .filter(key -> {
                    try {
                        return context.getServlet(key) == servlet;
                    } catch (ServletException e) {
                        return false;
                    }
                }).collect(Collectors.toSet());
    }

    @Override
    public String getRunAsRole() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassName() {
        return servlet.getClass().getName();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        ((JerryMouseServletConfig) servlet).setInitParameter(name, value);
        return true;
    }

    @Override
    public String getInitParameter(String name) {
        return ((JerryMouseServletConfig) servlet).getInitParameter(name);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        if (Objects.nonNull(initParameters)) {
            initParameters.entrySet().forEach(entry -> {
                ((JerryMouseServletConfig) servlet).setInitParameter(entry.getKey(), entry.getValue());
            });
        }
        return new HashSet<>();
    }

    @Override
    public Map<String, String> getInitParameters() {
        Map<String, String>map = new HashMap<>();
        Collections.list(((JerryMouseServletConfig) servlet).getInitParameterNames()).forEach(name -> {
            map.put(name, ((JerryMouseServletConfig) servlet).getInitParameter(name));
        });
        return map;
    }
}
