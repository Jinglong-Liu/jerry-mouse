package com.github.ljl.jerrymouse.support.classloader;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 14:12
 **/

public class LocalClassloader implements IClassLoader {
    @Override
    public Class loadClass(String className) {
        try {
            // 默认直接加载本地的 class
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
