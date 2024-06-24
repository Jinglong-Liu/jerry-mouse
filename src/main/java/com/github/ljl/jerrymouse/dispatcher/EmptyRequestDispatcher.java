package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.support.context.RequestDispatcherContext;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:11
 **/

public class EmptyRequestDispatcher implements IRequestDispatcher{

    private static Logger logger = LoggerFactory.getLogger(EmptyRequestDispatcher.class);

    @Override
    public void dispatch(RequestDispatcherContext context) {
        logger.warn("[JerryMouse] empty request url");

        //也可以返回默认页面之类的
        context.getResponse().write(JerryMouseHttpUtils.http404Resp());
    }
}
