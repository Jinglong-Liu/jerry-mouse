package com.github.ljl.wheel.jerrymouse.utils;

import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:11
 **/

public class HttpUtils {
    private static Map<Integer, String> lines;
    static {
        lines = new HashMap<>();
        lines.put(SC_OK, "HTTP/1.1 200 OK\r\n");
        lines.put(SC_NOT_FOUND, "HTTP/1.1 404 Not Found\r\n");
        lines.put(SC_INTERNAL_SERVER_ERROR, "HTTP/1.1 500 Internal Server Error\r\n");
    }

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

    public static String replaceCharacterEncoding(String charset, String oldContentType) {
        if (StringUtils.isEmptyTrim(charset)) {
            return oldContentType;
        }
        String contextType = null;
        int charsetIndex = oldContentType.indexOf("charset=");
        if (charsetIndex != -1) {
            // 替换已有的charset参数
            int endIndex = oldContentType.indexOf(';', charsetIndex);
            if (endIndex == -1) {
                endIndex = oldContentType.length();
            }
            StringBuilder sb = new StringBuilder(oldContentType);
            sb.replace(charsetIndex + "charset=".length(), endIndex, oldContentType);
            contextType = sb.toString();
        } else {
            // 没有找到charset参数，添加新的charset参数
            StringBuilder sb = new StringBuilder(oldContentType);
            sb.append("; charset=").append(charset);
            contextType = sb.toString();
        }
        return contextType;
    }
    public static String extractCharacterEncoding(String contentType) {
        if (StringUtils.isEmptyTrim(contentType)) {
            return null;
        }
        // 查找charset参数
        int charsetIndex = contentType.indexOf("charset=");
        if (charsetIndex == -1) {
            return null; // 如果没有找到charset参数，返回空
        }

        // 提取charset的值
        int startIndex = charsetIndex + "charset=".length();
        int endIndex = contentType.indexOf(';', startIndex);
        if (endIndex == -1) {
            endIndex = contentType.length();
        }

        String charset = contentType.substring(startIndex, endIndex).trim();
        return charset;
    }

    /**
     * 根据状态码，构建HTTP响应行
     * @param code 2xx, 4xx, 5xx
     * @return
     */
    public static StringBuilder createResponseLine(int code) {
        return new StringBuilder(lines.getOrDefault(code, lines.get(SC_OK)));
    }

}
