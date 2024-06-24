package com.github.ljl.jerrymouse.apps.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpFilter;
import java.io.IOException;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-23 19:59
 **/

public class HelloFilter extends HttpFilter {
    private static Logger logger = LoggerFactory.getLogger(HelloFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.info("[JerryMouse] Request received HelloFilter");

        filterChain.doFilter(request, response);

        logger.info("[JerryMouse] Response sent HelloFilter");
    }
}
