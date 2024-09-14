package com.github.ljl.wheel.jerrymouse.support.servlet.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 13:22
 **/

public class RequestParser {
    /**
     * @param msg whole request message
     * @return request body
     */
    public static String parseRequestBody(String msg) {
        String[] requestParts = msg.split("\r\n\r\n", 2);
        return requestParts.length == 2 ? requestParts[1]: "";
    }

    /**
     * @param msg whole request message
     * @return query params (in first line)
     */
    public static Map<String, String[]> parseQueryParams(String msg) {
        String[] lines = msg.split("\r\n");
        // GET /test/request-api?key1=value1&key2=value2 HTTP/1.1
        String queryString = lines[0];
        String[] parts = queryString.split("\\?");
        String params = parts.length > 1 ? parts[1] : queryString;  // 获取参数部分
        // key1=value1&key2=value2 HTTP/1.1
        int lastIndex = params.lastIndexOf(" ");
        if (lastIndex != -1) {
            params = params.substring(0, lastIndex);
        }

        Map<String, List<String>> paramMap = new HashMap<>();
        String[] queryParams = params.split("&");
        for (String queryParam : queryParams) {
            String[] keyValue = queryParam.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                if (!paramMap.containsKey(key)) {
                    paramMap.put(key, new ArrayList<>(1));
                }
                paramMap.get(key).add(value);
            }
        }

        return paramMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().toArray(new String[0])
                ));
    }

    /**
     * @param msg whole request message
     * @return request headers
     */
    public static Map<String, String> parseHeaders(String msg) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = msg.split("\r\n");

        // Headers start after the request line (first line)
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                break; // Headers end when an empty line is encountered
            }

            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        return headers;
    }

    /**
     * @param msg whole request message
     * @param data requestData to save method and uri
     */
    public static void parseRequestLine(String msg, RequestData data) {
        String[] requestLines = msg.split("\r\n");
        // 获取第一行请求行, 解析method和uri
        String firstLine = requestLines[0];
        String[] strings = firstLine.split(" ");
        String method = strings[0].toUpperCase();
        String uri = strings[1];
        int queryIndex = uri.indexOf('?');
        // 提取路径部分
        String requestPath = (queryIndex != -1) ? uri.substring(0, queryIndex) : uri;
        data.setUri(uri);
        data.setRequestURI(requestPath);
        data.setMethod(method);
    }
}
