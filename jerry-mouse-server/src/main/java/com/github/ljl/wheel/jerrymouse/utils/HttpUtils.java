package com.github.ljl.wheel.jerrymouse.utils;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:11
 **/

public class HttpUtils {
    public static String http200Resp(String rawText) {
        String format = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "%s";

        return String.format(format, rawText);
    }
}
