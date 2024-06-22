package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.dto.IRequest;
import com.github.ljl.jerrymouse.dto.IResponse;
import com.github.ljl.jerrymouse.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
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

    private IServletManager servletManager;
}
