package com.github.ljl.wheel.jerrymouse.support.context;

import com.github.ljl.wheel.jerrymouse.servlet.HelloServlet;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:18
 **/

public class ApplicationContextManager {
    private static ApplicationContext applicationContext;

    static {
        applicationContext = new ApplicationContext();
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
