package com.github.ljl.jerrymouse.utils;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:51
 **/

public class JerryMouseHttpUtils {
    public static String http200Resp(String rawText) {
        String format = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "%s";

        return String.format(format, rawText);
    }

    public static String http404Resp() {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "404 Not Found: The requested resource was not found on this server.";

        return response;
    }
    public static String http500Resp() {
        return "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "500 Internal Server Error: The server encountered an unexpected condition that prevented it from fulfilling the request.";
    }
    public static String http500Resp(Exception e) {
        return "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "500 Internal Server Error: \r\n" +
                e.getMessage();
    }
}
