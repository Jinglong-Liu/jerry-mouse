package com.github.ljl.wheel.jerrymouse.support.servlet.session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-16 10:12
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionValidCheck {
}
