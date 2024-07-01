package com.github.ljl.jerrymouse.apps.servlet;

import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.7.0
 * @create: 2024-06-24 12:27
 **/

public class TestAttrListenerServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(TestAttrListenerServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        String key = "man";
        logger.info("[JerryMouse] before attr set");
        context.setAttribute(key, "What can I say");
        logger.info("[JerryMouse] after set man");
        context.setAttribute(key, "Tomcat out!");
        logger.info("[JerryMouse] after replace man");
        final String attribute = (String) context.getAttribute(key);
        logger.info("[JerryMouse] get key={}, value={}", key, attribute);
        context.removeAttribute(key);
        logger.info("[JerryMouse] after remove man");
        logger.info("[JerryMouse] get value = {}", context.getAttribute(key));
        resp.getWriter().print(JerryMouseHttpUtils.http200Resp(attribute));
        resp.flushBuffer();
    }
}
