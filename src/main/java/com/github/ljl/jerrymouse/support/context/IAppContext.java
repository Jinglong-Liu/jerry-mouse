package com.github.ljl.jerrymouse.support.context;

import java.util.Enumeration;


public interface IAppContext {
    void setAttribute(String name, Object object);

    void removeAttribute(String name);

    Object getAttribute(String name);

    Enumeration<String> getAttributeNames();
}
