package com.github.ljl.jerrymouse.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-23 21:48
 **/

public class SecondFilter extends HttpFilter {
    private static Logger logger = LoggerFactory.getLogger(SecondFilter.class);

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.info("[JerryMouse] Request received SecondFilter");

        filterChain.doFilter(request, response);

        logger.info("[JerryMouse] Response send second SecondFilter");
    }
}
