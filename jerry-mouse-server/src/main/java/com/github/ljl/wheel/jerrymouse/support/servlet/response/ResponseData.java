package com.github.ljl.wheel.jerrymouse.support.servlet.response;

import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import lombok.Data;

import java.util.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 13:21
 **/

@Data
public class ResponseData {

    private Map<String, String> headers = new HashMap<>();

    private Integer code = 0;

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";

    public void setContentType(String type) {

        String charset = null;

        String oldContentType = headers.get(CONTENT_TYPE_HEADER_NAME);
        if (Objects.isNull(oldContentType)) {
            headers.put(CONTENT_TYPE_HEADER_NAME, type);
            return;
        }
        // 提取旧的 charset
        if (oldContentType.contains("charset=")) {
            int charsetIndex = oldContentType.indexOf("charset=");
            charset = oldContentType.substring(charsetIndex);
        }

        // 新的 contentType 是否包含 charset
        if (type.contains("charset=")) {
            headers.put(CONTENT_TYPE_HEADER_NAME, type);
        } else {
            if (charset != null) {
                // text/html;
                String newContentType = null;
                if (type.contains(";")) {
                    newContentType = type + " " + charset;
                } else {
                    // text/html
                    newContentType = type + "; " + charset;
                }
                headers.put(CONTENT_TYPE_HEADER_NAME, newContentType);
            } else {
                headers.put(CONTENT_TYPE_HEADER_NAME, type);
            }
        }
    }

    public void setCharacterEncoding(String charset) {
        String oldContentType = headers.getOrDefault(CONTENT_TYPE_HEADER_NAME, "");
        String contextType = HttpUtils.replaceCharacterEncoding(charset, oldContentType);
        headers.put(CONTENT_TYPE_HEADER_NAME, contextType);
    }

    public void setContentLength(int len) {
        headers.put(CONTENT_LENGTH_HEADER_NAME, String.valueOf(len));
    }

    public void setContentLengthLong(long len) {
        headers.put(CONTENT_LENGTH_HEADER_NAME, String.valueOf(len));
    }

    public String getHeader(String s) {
        return headers.get(s);
    }

    public Collection<String> getHeaders(String s) {
        return headers.values();
    }

    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    public void setHeader(String name, String value) {
        if (name.equals(CONTENT_TYPE_HEADER_NAME)) {
            setContentType(value);
        }
        headers.put(name, value);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    public String getCharacterEncoding() {
        final String contentType = headers.getOrDefault(CONTENT_TYPE_HEADER_NAME, "");
        return HttpUtils.extractCharacterEncoding(contentType);
    }

    public String getContentType() {
        return headers.get(CONTENT_TYPE_HEADER_NAME);
    }
}