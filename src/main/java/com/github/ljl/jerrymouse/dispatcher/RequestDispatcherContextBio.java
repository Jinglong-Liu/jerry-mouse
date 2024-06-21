package com.github.ljl.jerrymouse.dispatcher;

import com.github.ljl.jerrymouse.dto.JerryMouseRequestBio;
import com.github.ljl.jerrymouse.dto.JerryMouseResponseBio;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import lombok.Data;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-21 16:27
 **/

@Deprecated
@Data
public class RequestDispatcherContextBio {
    private JerryMouseRequestBio request;

    private JerryMouseResponseBio response;

    private IServletManager servletManager;
}
