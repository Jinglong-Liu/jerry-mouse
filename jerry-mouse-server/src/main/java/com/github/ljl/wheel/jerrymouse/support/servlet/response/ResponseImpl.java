package com.github.ljl.wheel.jerrymouse.support.servlet.response;

import com.github.ljl.wheel.jerrymouse.exception.MethodNotSupportException;
import com.github.ljl.wheel.jerrymouse.server.nio.handler.SocketWriter;
import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import lombok.Getter;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 13:23
 **/

public class ResponseImpl implements HttpServletResponse {
    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";

    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";

    @Getter
    private final SocketWriter sockerWriter;

    private final ServletContext servletContext;

    private ResponseData responseData;

    private final PrintWriter printWriter;

    private final ServletOutputStream outputStream;

    public ResponseImpl(SocketWriter sockerWriter, ServletContext servletContext) {
        this.sockerWriter = sockerWriter;
        this.servletContext = servletContext;
        this.responseData = new ResponseData();
        this.printWriter = new HttpPrintWriter(new StringWriter());
        this.outputStream = new ByteArrayServletOutputStream();
        this.setContentType(CONTENT_TYPE_TEXT_HTML);
        this.setStatus(200);
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
        return responseData.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {
        responseData.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        responseData.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {

    }

    @Override
    public void addIntHeader(String name, int value) {

    }

    @Override
    public void setStatus(int sc) {
        responseData.setCode(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        throw new MethodNotSupportException("setStatus(int, String) is not supported");
    }

    @Override
    public int getStatus() {
        return responseData.getCode();
    }

    @Override
    public String getHeader(String name) {
        return responseData.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return responseData.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return responseData.getHeaderNames();
    }

    @Override
    public String getCharacterEncoding() {
        return responseData.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return responseData.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return this.printWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        responseData.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
        responseData.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        responseData.setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type) {
        responseData.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        outputStream.flush();
        printWriter.flush();;
    }

    @Override
    public void resetBuffer() {
        ((HttpPrintWriter)printWriter).resetBuffer();
        ((ByteArrayServletOutputStream)outputStream).resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    public class HttpPrintWriter extends PrintWriter {
        private StringWriter stringWriter;
        public HttpPrintWriter(Writer out) {
            super(out);
            this.stringWriter = (StringWriter) out;
        }

        @Override
        public void flush() {
            String writerContent = stringWriter.toString();
            if (!writerContent.isEmpty()) {
                // 构建http报文
                String out = checkAndBuildHttpResponseMessage(writerContent);
                // 真正写入
                getSockerWriter().write(out);
            }
        }
        public void resetBuffer() {
            stringWriter.getBuffer().setLength(0);
        }
    }

    private class ByteArrayServletOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        @Override
        public void write(int b) throws IOException {
            byteArrayOutputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {}

        public byte[] toByteArray() {
            return byteArrayOutputStream.toByteArray();
        }

        public void resetBuffer() {
            byteArrayOutputStream.reset();
        }
        @Override
        public void flush() throws IOException {
            byteArrayOutputStream.flush();

            byte[] outputStreamContent = new byte[0];
            if (outputStream instanceof ByteArrayServletOutputStream) {
                outputStreamContent = ((ByteArrayServletOutputStream)outputStream).toByteArray();
            }

            if (outputStreamContent.length > 0) {
                String msg = checkAndBuildHttpResponseMessage(new String(outputStreamContent, StandardCharsets.UTF_8));
                // 向socket写入
                getSockerWriter().write(msg);
            }
        }
    }

    /**
     * 如果只有body, 添加Line和headers, 构建成完整报文
     * @param body
     * @return
     */
    private String checkAndBuildHttpResponseMessage(String body) {
        if (isCompleteResponse(body)) {
            return body;
        } else {
            StringBuilder builder = HttpUtils.createResponseLine(getStatus());
            addHeaders(builder, responseData.getHeaders());
            // 自动生成contentLength
            if (!containsHeader(CONTENT_LENGTH_HEADER_NAME)) {
                addHeader(builder,CONTENT_LENGTH_HEADER_NAME, String.valueOf(body.length()));
            }
            builder.append("\r\n");
            builder.append(body);
            return builder.toString();
        }
    }

    private static boolean isCompleteResponse(String response) {
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

    private static StringBuilder addHeader(StringBuilder builder, String name, String value) {
        builder.append(name + ": " + value + "\r\n");
        return builder;
    }

    private static StringBuilder addHeaders(StringBuilder builder, Map<String, String> headers) {
        headers.entrySet().forEach(entry -> {
            addHeader(builder, entry.getKey(), entry.getValue());
        });
        return builder;
    }
}
