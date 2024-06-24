package com.github.ljl.jerrymouse.impl.dto;

import javax.servlet.http.HttpServletRequest;

public interface IRequest extends HttpServletRequest {
    /**
     * 获取请求地址
     * @return url
     */
    String getUrl();

    /**
     * 获取方法
     * @return method
     */
    String getMethod();
}
