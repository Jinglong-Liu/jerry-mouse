package com.github.ljl.wheel.jerrymouse.support.dispatcher;

import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.servlet.filter.FilterChainImpl;
import com.github.ljl.wheel.jerrymouse.support.servlet.response.ResponseImpl;
import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:06
 **/

public class ServletDispatcher implements IDispatcher {

    private Logger logger = LoggerFactory.getLogger(ServletDispatcher.class);

    @Override
    public void dispatch(RequestDispatcherContext context) {
        HttpServletRequest request = context.getRequest();
        ResponseImpl response = (ResponseImpl) context.getResponse();
        ApplicationContext applicationContext = (ApplicationContext) request.getServletContext();
        HttpServlet httpServlet = applicationContext.getServletByURI(request.getRequestURI());
        List<Filter> filterList = applicationContext.getMatchFilters(request.getRequestURI());
        if (Objects.isNull(httpServlet)) {
            logger.error("Servlet not found! uri = {}", request.getRequestURI());
            response.getSockerWriter().write(HttpUtils.http404Resp());
            return;
        }
        try {
            // httpServlet.service(request, response);
            filter(httpServlet, request, response, filterList);
        } catch (ServletException | IOException e) {
            logger.error("Server meet error when do servlet Service");
            e.printStackTrace();
            response.getSockerWriter().write(HttpUtils.http500Resp(e));
        }
    }

    private void filter(HttpServlet httpServlet,
                        ServletRequest request,
                        ServletResponse response,
                        List<Filter> filterList)
            throws ServletException, IOException {
        FilterChain chain = new FilterChainImpl(filterList, httpServlet);
        chain.doFilter(request, response);
    }
}
