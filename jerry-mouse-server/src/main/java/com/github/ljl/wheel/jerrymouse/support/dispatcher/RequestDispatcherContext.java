package com.github.ljl.wheel.jerrymouse.support.dispatcher;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:02
 **/

@Data
public class RequestDispatcherContext {

    private HttpServletRequest request;

    private HttpServletResponse response;

    public RequestDispatcherContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
}
