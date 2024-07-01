package com.github.ljl.jerrymouse.support.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 18:44
 **/

public class DefaultServletManager implements IServletManager {

    private static Logger logger = LoggerFactory.getLogger(DefaultServletManager.class);

    protected final Map<String, HttpServlet> servletMap = new HashMap<>();

    public DefaultServletManager() {}

    @Override
    public void init(String baseDir) {

    }

    @Override
    public void register(String url, HttpServlet servlet) {
        servletMap.put(url, servlet);
    }

    @Override
    public HttpServlet getServlet(String url) {
        String key = findTarget(servletMap.keySet(), url);
        if (Objects.isNull(key)) {
            if (servletMap.containsKey("/")) {
                return servletMap.get("/");
            }
            return servletMap.get("dispatcherServlet");
        }
        return servletMap.get(key);
    }

    @Override
    public Map<String, HttpServlet> getServlets() {
        return servletMap;
    }

    private String findTarget(Collection<String> list, String target) {
        String result = null;

        for (String s : list) {
            // Check for exact match
            if (s.equals(target)) {
                return s;
            }

            // Check for prefix match ending with "/"
            if (s.endsWith("/") && target.startsWith(s) && (result == null || s.length() > result.length())) {
                result = s;
            }
        }

        return result;
    }
}
