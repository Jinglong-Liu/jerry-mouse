package com.github.ljl.jerrymouse.support.context;

import com.github.ljl.jerrymouse.impl.dto.IRequest;
import com.github.ljl.jerrymouse.impl.dto.IResponse;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
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

    private JerryMouseAppContext appContext;

    public IServletManager getServletManager() {
        return appContext.getServletManager();
    }

    public IFilterManager getFilterManager() {
        return appContext.getFilterManager();
    }
}
