package com.github.ljl.wheel.jerrymouse.utils;

import java.io.File;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:05
 **/

public class FileUtils {
    private static String os = System.getProperty("os.name").toLowerCase();

    public static String buildFullPath(String prefix, String path) {
        // 检查前缀是否已以斜杠结尾，如果是，则直接拼接路径；如果不是，则在前缀后添加斜杠再拼接路径
        if(path.startsWith("/")) {
            path = path.substring(1);
        }
        String result = path;
        if(prefix.endsWith(File.separator)) {
            result = prefix + path;
        } else {
            result = prefix + File.separator + path;
        }
        // window系统
        if(os.contains("win") && (result.startsWith("\\") || result.startsWith("/"))) {
            result = result.substring(1);
        }
        return result;
    }
}
