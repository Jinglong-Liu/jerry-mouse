package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.support.context.RequestDispatcherContext;

public interface IRequestDispatcher {
    /**
     * 请求分发
     * @param context 上下文
     */
    void dispatch(RequestDispatcherContext context);
}
