package com.github.ljl.wheel.jerrymouse.support.context;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:18
 **/

public class ApplicationContextManager {
    private static final Map<String, ApplicationContext> applicationContextMap;

    static {
        applicationContextMap = new ConcurrentHashMap<>();
    }

    public static ApplicationContext applyApplicationContext(String appName) {
        ApplicationContext context = new ApplicationContext(appName);
        applicationContextMap.put(appName, context);
        return context;
    }

    public static ApplicationContext getApplicationContext(String appName) {
        return applicationContextMap.get(appName);
    }

    public static ApplicationContext getApplicationContext(HttpServletRequest request) {
        // 根据request，解析出appName
        String appName = request.getRequestURI();
        int index = appName.indexOf('/', 1); // start with "/", find next
        if (index != -1) {
            appName = appName.substring(0, index);
        }
        if (applicationContextMap.containsKey(appName)) {
            return applicationContextMap.get(appName);
        }
        // 找不到，走本地
        return applicationContextMap.get("/root");
    }
}
