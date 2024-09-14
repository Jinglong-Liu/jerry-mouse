package com.github.ljl.wheel.jerrymouse.support.dispatcher;

import com.github.ljl.wheel.jerrymouse.support.servlet.response.ResponseImpl;
import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:03
 **/

public class EmptyDispatcher implements IDispatcher {

    @Override
    public void dispatch(RequestDispatcherContext context) {
        ResponseImpl response = (ResponseImpl) context.getResponse();
        response.getSockerWriter().write(HttpUtils.http404Resp());
    }
}
