package com.github.ljl.wheel.jerrymouse.support.servlet.request;

import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import lombok.Data;
import lombok.Getter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 13:21
 **/

@Data
public class RequestData {

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CHARACTER_ENCODING_NAME = "charset";
    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private static final String CONTENT_ENCODING_HEADER_NAME = "Content-Encoding";

    private String requestMessage;

    private Map<String, String> headers;

    private Map<String, String[]> parameters;

    private Map<String, Object[]> attributes = new HashMap<>();

    private String body;

    private String uri;

    private String method;

    private String requestURI;

    @Getter
    private ServletInputStream inputStream;

    public RequestData(String requestMessage) {
        this.requestMessage = requestMessage;
        this.headers = RequestParser.parseHeaders(requestMessage);
        this.parameters = RequestParser.parseQueryParams(requestMessage);
        this.body = RequestParser.parseRequestBody(requestMessage);
        this.inputStream = new ServletInputStreamWrapper(this.body);
        RequestParser.parseRequestLine(requestMessage, this);
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public Enumeration<String> getHeaders(String s) {
        if (headers.containsKey(s)) {
            return Collections.enumeration(Collections.singletonList(headers.get(s)));
        }
        return Collections.emptyEnumeration();
    }

    public long getDateHeader(String s) {
        if (headers.containsKey(s)) {
            return Long.parseLong(headers.get(s));
        }
        return 0;
    }

    public String getHeader(String s) {
        return headers.get(s);
    }

    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    public int getIntHeader(String s) {
        return Integer.parseInt(headers.getOrDefault(s, "0"));
    }

    public void setQueryParams(Map<String, String[]> queryParams) {
        parameters.clear();
        parameters.putAll(queryParams);
    }
    public String getParameter(String name) {
        return parameters.getOrDefault(name, new String[1])[0];
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    public String[] getParameterValues(String name) {
        return parameters.getOrDefault(name, new String[0]);
    }

    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    public void removeAttribute(String name) {
        if (attributes.containsKey(name)) {
            attributes.remove(name);
        }
    }
    public void setAttribute(String name, Object o) {
        attributes.put(name, new Object[]{o});
    }

    public Object getAttribute(String name) {
        if (attributes.containsKey(name)) {
            return attributes.get(name)[0];
        }
        return null;
    }
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }
    public String getContentType() {
        return headers.get(CONTENT_TYPE_HEADER_NAME);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH_HEADER_NAME, "-1"));
    }

    public long getContentLengthLong() {
        return Long.parseLong(headers.getOrDefault(CONTENT_LENGTH_HEADER_NAME, "-1"));
    }

    public String getCharacterEncoding() {
        final String contentType = headers.getOrDefault(CONTENT_TYPE_HEADER_NAME, "");
        return HttpUtils.extractCharacterEncoding(contentType);
    }

    public void setCharacterEncoding(String charSet) {
        String oldContentType = headers.getOrDefault(CONTENT_TYPE_HEADER_NAME, "");
        String contextType = HttpUtils.replaceCharacterEncoding(charSet, oldContentType);
        headers.put(CONTENT_TYPE_HEADER_NAME, contextType);
    }

    class ServletInputStreamWrapper extends ServletInputStream {

        private ByteArrayInputStream byteArrayInputStream;

        public ServletInputStreamWrapper(String body, Charset charset) {
            byte bytes[] = body.getBytes(charset);
            this.byteArrayInputStream = new ByteArrayInputStream(bytes);
        }
        public ServletInputStreamWrapper(String body) {
            this(body, Charset.defaultCharset());
        }
        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("ReadListener is not supported");
        }

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }
    }
}

