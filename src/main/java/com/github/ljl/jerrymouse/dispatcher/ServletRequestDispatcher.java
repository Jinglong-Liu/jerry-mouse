package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        JerryMouseRequest request = context.getRequest();
        JerryMouseResponse response = context.getResponse();
        IServletManager servletManager = context.getServletManager();

        // 直接和 servlet 映射
        String requestUrl = request.getUrl();
        HttpServlet httpServlet = servletManager.getServlet(requestUrl);
        if(Objects.isNull(httpServlet)) {
            logger.warn("[JerryMouse] requestUrl={} mapping not found", requestUrl);
            response.write(JerryMouseHttpUtils.http404Resp());
        } else {
            // 正常的逻辑处理
            try {
                httpServlet.service(request, response);
            } catch (IOException | ServletException e) {
                logger.error("[JerryMouse] http servlet handle meet exception");
                throw new JerryMouseException(e);
            }
        }
    }
}
