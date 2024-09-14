package com.github.ljl.wheel.jerrymouse.support.dispatcher;

import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.servlet.response.ResponseImpl;
import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
        //TODO: use uriPattern
        ApplicationContext applicationContext = (ApplicationContext) request.getServletContext();
        HttpServlet httpServlet = applicationContext.getServletByURI(request.getRequestURI());
        try {
            httpServlet.service(request, response);
        } catch (NullPointerException e) {
            logger.error("Servlet not found!");
            response.getSockerWriter().write(HttpUtils.http404Resp());
        } catch (ServletException | IOException e) {
            logger.error("Server meet error when do servlet Service");
            e.printStackTrace();
            response.getSockerWriter().write(HttpUtils.http500Resp(e));
        }
    }
}
