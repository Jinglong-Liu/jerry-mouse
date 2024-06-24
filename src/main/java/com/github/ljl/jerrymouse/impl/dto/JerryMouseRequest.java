package com.github.ljl.jerrymouse.impl.dto;

import com.github.ljl.jerrymouse.impl.dto.adaptor.JerryMouseRequestAdaptor;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/


public class JerryMouseRequest extends JerryMouseRequestAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequest.class);

    public JerryMouseRequest(String method, String url) {
        this.method = method;
        this.url = url;
    }
    private String method;

    private String url;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public ServletContext getServletContext() {
        return JerryMouseAppContext.get();
    }
}
