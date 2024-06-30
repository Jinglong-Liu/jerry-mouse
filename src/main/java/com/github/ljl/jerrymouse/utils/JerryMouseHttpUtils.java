package com.github.ljl.jerrymouse.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:51
 **/

public class JerryMouseHttpUtils {
    private static Map<Integer, String> lines;
    static {
        // TODO: 补充完整
        lines = new HashMap<>();
        lines.put(200, "HTTP/1.1 200 OK\r\n");
        lines.put(404, "HTTP/1.1 404 Not Found\r\n");
        lines.put(500, "HTTP/1.1 500 Internal Server Error\r\n");
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

    public static StringBuilder addHeader(StringBuilder builder, String name, String value) {
        builder.append(name + ": " + value + "\r\n");
        return builder;
    }
    public static StringBuilder addHeaders(StringBuilder builder, Map<String, String> headers) {
        headers.entrySet().forEach(entry -> {
            addHeader(builder, entry.getKey(), entry.getValue());
        });
        return builder;
    }
    public static StringBuilder createResponse(int httpStatus) {
        return new StringBuilder(lines.getOrDefault(httpStatus, lines.get(200)));
    }
    public static boolean isCompleteResponse(String response) {
        if (Objects.isNull(response)) {
            return false;
        }
        // 判断有没有完整响应行
        String pattern = "^HTTP/\\d\\.\\d \\d{3} .+";
        String[] responseLines = response.split("\r\n");
        if (responseLines.length < 2) {
            return false;
        }
        return responseLines[0].matches(pattern);
    }

    public static String replaceCharacterEncoding(String oldContentType, String charset) {
        if (StringUtil.isEmptyTrim(charset)) {
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
        if (StringUtil.isEmptyTrim(contentType)) {
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
}
