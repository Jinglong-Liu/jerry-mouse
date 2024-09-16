package com.github.ljl.wheel.jerrymouse.support.servlet.session;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-16 10:29
 **/

public class HttpSessionFactory {
    private static final Logger logger = LoggerFactory.getLogger(HttpSessionFactory.class);

    public static HttpSession createHttpSession(HttpServletRequest request) {
        logger.info("session create!");
        // 这里的Constructor会构造两次，因此把初始化，监听器等操作放到init()
        HttpSessionImpl session = new HttpSessionImpl();
        HttpSessionImpl proxy =  createProxy(session, new HttpSessionProxy(session), new Object[0], new Class[0]);
        proxy.init(request);
        return proxy;
    }

    /**
     * 生成代理对象
     * 使用ByteBuddy技术 https://bytebuddy.net/#/
     * 可以代理具体类
     * @param bean 需要代理的对象
     * @param handler 拦截器
     * @param parameters bean的构造函数参数列表
     * @param parameterTypes bean的构造函数参数类型列表 (要求准确)
     * @param <T> bean的类型
     * @return 代理对象
     */
    private static  <T> T createProxy(T bean, InvocationHandler handler, Object[] parameters, Class<?>[] parameterTypes) {
        ByteBuddy byteBuddy = new ByteBuddy();
        // 目标Bean的Class类型:
        Class<?> targetClass = bean.getClass();

        // 动态创建Proxy的Class:
        Class<?> proxyClass = byteBuddy
                // 模仿super class的构造方法
                .subclass(targetClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                // 拦截所有方法:
                .method(ElementMatchers.any())
                // 传入拦截器
                .intercept(InvocationHandlerAdapter.of(handler))
                // 生成字节码:
                .make()
                // 加载字节码:
                .load(targetClass.getClassLoader())
                .getLoaded();

        // 用构造方法创建Proxy实例:
        Object proxy;
        try {
            if (parameterTypes.length > 0) {
                // 类型需要严格匹配，因此参数需要传递进来
                Constructor<?> constructor = proxyClass.getConstructor(parameterTypes);
                proxy = constructor.newInstance(parameters);
            } else {
                Constructor<?> constructor = proxyClass.getConstructor();
                proxy = constructor.newInstance();
            }
        } catch (NoSuchMethodException e) {
            logger.error("found no matched Constructor for proxy");
            throw new IllegalArgumentException("Constructor not found for given arguments", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("create proxy error");
            throw new RuntimeException("Failed to create proxy instance", e);
        }

        return (T) proxy;
    }
}
