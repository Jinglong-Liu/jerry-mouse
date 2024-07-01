package com.github.ljl.jerrymouse.classloader;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 14:12
 **/

public class LocalClassloader extends ClassLoader {
    @Override
    public Class loadClass(String className) {
        try {
            // 默认直接加载本地的 class
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
