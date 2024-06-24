package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.impl.dto.IRequest;
import com.github.ljl.jerrymouse.impl.dto.IResponse;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.impl.JerryMouseFilterChain;
import com.github.ljl.jerrymouse.support.servlet.IServletManager;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:12
 **/

public class ServletRequestDispatcher implements IRequestDispatcher {
    private static Logger logger = LoggerFactory.getLogger(ServletRequestDispatcher.class);

    @Override
    public void dispatch(RequestDispatcherContext context) {
        IRequest request = context.getRequest();
        IResponse response = context.getResponse();
        IServletManager servletManager = context.getServletManager();

        // 找到注册好的相应的 servlet 和 List<Filter>
        String requestUrl = request.getUrl();
        List<Filter> filters = context.getFilterManager().getMatchFilters(requestUrl);
        HttpServlet httpServlet = servletManager.getServlet(requestUrl);
        if(Objects.isNull(httpServlet)) {
            logger.warn("[JerryMouse] requestUrl={} mapping not found", requestUrl);
            response.write(JerryMouseHttpUtils.http404Resp());
        } else {
            try {
                // httpServlet.service(request, response);
                filter(httpServlet, filters, request, response);
            } catch (IOException | ServletException e) {
                logger.error("[JerryMouse] http servlet handle meet exception");
                throw new JerryMouseException(e);
            }
        }
    }
    void filter(Servlet servlet, List<Filter> filters, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FilterChain filterChain = new JerryMouseFilterChain(servlet, filters);
        filterChain.doFilter(request, response);
    }
}
