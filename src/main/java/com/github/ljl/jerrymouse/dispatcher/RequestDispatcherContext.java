package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.dto.IRequest;
import com.github.ljl.jerrymouse.dto.IResponse;
import com.github.ljl.jerrymouse.support.servlet.DefaultServletManager;
import com.github.ljl.jerrymouse.support.filter.IFilterManager;
import com.github.ljl.jerrymouse.support.servlet.IServletManager;
import com.github.ljl.jerrymouse.support.filter.DefaultFilterManager;
import lombok.Data;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:05
 **/
@Data
public class RequestDispatcherContext {

    private IRequest request;

    private IResponse response;

    private final IServletManager servletManager = DefaultServletManager.get();

    private final IFilterManager filterManager = DefaultFilterManager.get();
}
