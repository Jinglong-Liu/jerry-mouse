package com.github.ljl.wheel.jerrymouse.support.servlet.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-16 10:29
 **/

public class HttpSessionProxy implements InvocationHandler {
    private final Logger logger = LoggerFactory.getLogger(HttpSessionProxy.class);

    private final HttpSessionImpl sessionImpl;

    public HttpSessionProxy(HttpSessionImpl sessionImpl) {
        this.sessionImpl = sessionImpl;
    }

    /**
     * 所有方法都被拦截，那么invoke实际上全部是交给sessionWrapper这个类执行即可
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpSessionImpl session = sessionImpl;

        if (method.isAnnotationPresent(SessionValidCheck.class)) {
            // 合法检查
            if (!session.isValid()) {
                logger.warn("Session is invalid, maybe expired. id={}", sessionImpl.getId());
                throw new IllegalStateException("Session is invalid.");
            }
        }
        // 调用实际传入的 HttpSession 对象的方法
        Object result = method.invoke(session, args);
        return result;
    }
}
