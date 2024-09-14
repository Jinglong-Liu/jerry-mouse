package com.github.ljl.wheel.jerrymouse.support.manager;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:09
 **/

public class ServletManager {
    private Map<String, HttpServlet> servletMap = new HashMap<>();

    public HttpServlet getServlet(String urlPattern) {
        return servletMap.get(urlPattern);
    }

    public void register(String urlPattern, HttpServlet servlet) {
        servletMap.put(urlPattern, servlet);
    }
}
