package com.github.ljl.jerrymouse.utils;

import com.github.ljl.jerrymouse.impl.dto.IRequest;
import com.github.ljl.jerrymouse.impl.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.impl.dto.JerryMouseServletInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 09:16
 **/

public class JerryMouseRequestUtils {

    public static IRequest buildRequest(String text) {
        // 使用正则表达式按行分割请求字符串
        String[] requestLines = text.split("\r\n");

        // 获取第一行请求行
        String firstLine = requestLines[0];

        String[] strings = firstLine.split(" ");
        String method = strings[0];
        String url = strings[1];

        JerryMouseRequest request = new JerryMouseRequest(method, url);

        // queryParams
        request.setQueryParams(parseQueryParams(text));

        // header
        request.setHeaders(parseHeaders(text));

        // body
        String body = parseBody(text);

        // inputStream
        request.setInputStream(parseBodyToStream(body));

        // System.out.println("http body:\r\n" + body);

        return request;
    }
    private static Map<String, String[]> parseQueryParams(String queryString) {
        String[] parts = queryString.split("\\?");
        String params = parts.length > 1 ? parts[1] : queryString;  // 获取参数部分
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
    private static Map<String, String> parseHeaders(String text) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = text.split("\r\n");

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
    private static JerryMouseServletInputStream parseBodyToStream(String body) {
        return new JerryMouseServletInputStream(body);
    }

    private static String parseBody(String text) {
        // 2 表示最多只分割1次，body中的\r\n不会被分割
        String[] parts = text.split("\r\n\r\n", 2);
        if (parts.length == 2) {
            // 返回报文体
            return parts[1];
        } else {
            // 如果没有找到分隔符，返回空字符串
            return "";
        }
    }
    /**
     * 实现：https://github.com/apache/tomcat/blob/main/java/org/apache/catalina/util/RequestUtil.java#L37
     *
     * @param request The request object for which the URL should be built
     * @since v0.8
     * @return The request URL for the given request object
     */
    public static StringBuffer getRequestURL(HttpServletRequest request) {
        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme(); // TODO
        int port = request.getServerPort();
        if (port < 0) {
            // Work around java.net.URL bug
            port = 80;
        }

        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getRequestURI());

        return url;
    }

    public static String extractPrefix(String uri) {
        // 找到第一个和第二个斜杠的位置
        int firstSlash = uri.indexOf('/');
        int secondSlash = uri.indexOf('/', firstSlash + 1);

        // 如果没有第二个斜杠，返回整个字符串
        if (secondSlash == -1) {
            return uri;
        }

        // 返回第二个斜杠之前的部分
        return uri.substring(0, secondSlash);
    }
}
