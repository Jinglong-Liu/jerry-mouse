package com.github.ljl.jerrymouse.support.servlet;

import com.github.ljl.jerrymouse.support.threadpool.JerryMouseThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

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
        logger.info("[JerryMouse] register servlet, url={}, servlet={}", url, servlet.getClass().getName());
        servletMap.put(url, servlet);
    }

    @Override
    public HttpServlet getServlet(String url) {
        return servletMap.get(url);
    }
}
