package com.github.ljl.jerrymouse.support.context;

import com.github.ljl.jerrymouse.impl.dto.IRequest;

import javax.servlet.ServletContext;

public interface IContextManager {
    ServletContext getServletContext(IRequest request);

    ServletContext getServletContext(String name);

    void registerServletContext(String name, ServletContext context);
}
