package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:05
 **/
@Data
public class RequestDispatcherContext {

    private JerryMouseRequest request;

    private JerryMouseResponse response;

    private IServletManager servletManager;
}
