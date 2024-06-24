package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.impl.dto.IRequest;
import com.github.ljl.jerrymouse.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:10
 **/

public class RequestDispatcherManager implements IRequestDispatcher {

    private static Logger logger = LoggerFactory.getLogger(RequestDispatcherManager.class);

    private final IRequestDispatcher emptyRequestDispatcher = new EmptyRequestDispatcher();
    private final IRequestDispatcher staticHtmlRequestDispatcher = new StaticHtmlRequestDispatcher();
    private final IRequestDispatcher servletRequestDispatcher = new ServletRequestDispatcher();

    @Override
    public void dispatch(RequestDispatcherContext context) {
        final IRequest request = context.getRequest();
        String requestUrl = request.getUrl();
        IRequestDispatcher requestDispatcher = getRequestDispatcher(requestUrl);

        // 分发到不同类型的RequestDispatcher
        requestDispatcher.dispatch(context);
    }
    private IRequestDispatcher getRequestDispatcher(String requestUrl) {
        if (StringUtil.isEmpty(requestUrl)) {
            return emptyRequestDispatcher;
        } else {
            if (requestUrl.endsWith(".html")) {
                return staticHtmlRequestDispatcher;
            } else {
                return servletRequestDispatcher;
            }
        }
    }
}
