package com.github.ljl.jerrymouse.impl.dto;

import com.github.ljl.jerrymouse.impl.dto.adaptor.JerryMouseRequestAdaptor;
import com.github.ljl.jerrymouse.support.context.IContextManager;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
import com.github.ljl.jerrymouse.support.context.JerryMouseContextManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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

    private IContextManager contextManager = JerryMouseContextManager.get();

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
        return contextManager.getServletContext(this);
    }
}
