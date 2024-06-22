package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.dto.IRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import com.github.ljl.jerrymouse.utils.StringUtil;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:10
 **/

public class RequestDispatcherManager implements IRequestDispatcher {

    private final IRequestDispatcher emptyRequestDispatcher = new EmptyRequestDispatcher();
    private final IRequestDispatcher staticHtmlRequestDispatcher = new StaticHtmlRequestDispatcher();
    private final IRequestDispatcher servletRequestDispatcher = new ServletRequestDispatcher();

    @Override
    public void dispatch(RequestDispatcherContext context) {
        final IRequest request = context.getRequest();
        // 分发
        String requestUrl = request.getUrl();
        if (StringUtil.isEmpty(requestUrl)) {
            emptyRequestDispatcher.dispatch(context);
        } else {
            if (requestUrl.endsWith(".html")) {
                staticHtmlRequestDispatcher.dispatch(context);
            } else {
                servletRequestDispatcher.dispatch(context);
            }
        }
    }
}
