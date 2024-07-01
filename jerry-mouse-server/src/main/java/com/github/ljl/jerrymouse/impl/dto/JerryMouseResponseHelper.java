package com.github.ljl.jerrymouse.impl.dto;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import com.sun.corba.se.impl.interceptors.SlotTable;
import org.omg.CORBA.Object;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since: 0.5.1
 * @create: 2024-06-23 08:45
 **/

public class JerryMouseResponseHelper implements HttpServletResponse {

    /**
     * https://datatracker.ietf.org/doc/html/rfc7231#section-8.3.2
     */
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CHARACTER_ENCODING_NAME = "charset";
    private static final String CONTENT_LENGTH_HEADER_NAME = "Context-Length";
    private static final String CONTENT_ENCODING_HEADER_NAME = "Content-Encoding";

    private static final String CONTENT_TYPE_TEXT_HTML = "text/html";

    private JerryMouseResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;

    // 暂时不允许同名字段
    private Map<String, String> headers = new HashMap<>();

    private int status = 200;
    /**s
     * 若采用OutputStreamHelper(response)，则复用JerryMouseRequest.write()
     * 两者均可正常运行
     */
    private final ServletOutputStream outputStream;

    public JerryMouseResponseHelper(JerryMouseResponse response) {
        this.response = response;
        this.stringWriter = new StringWriter();
        // 重写PrintWriter
        this.writer = new HttpPrintWriter(stringWriter);
        this.outputStream = new ByteArrayServletOutputStream();
        // this.outputStream = new OutputStreamHelper(response);
        // 暂时不支持自动推导类型
        this.setContentType(CONTENT_TYPE_TEXT_HTML);
        this.setStatus(200);
    }
    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return headers.containsKey(s);
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {
        if (s.equals(CONTENT_TYPE_HEADER_NAME)) {
            setContentType(s1);
        }
        headers.put(s, s1);
    }

    @Override
    public void addHeader(String s, String s1) {
        headers.put(s, s1);
    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {
        this.status = i;
    }

    @Deprecated
    @Override
    public void setStatus(int i, String s) {
        throw new JerryMouseException("Deprecated setStatus(" + i + ", " + s + ")");
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String s) {
        return headers.get(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return headers.values();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        final String contentType = headers.getOrDefault(CONTENT_TYPE_HEADER_NAME, "");
        return JerryMouseHttpUtils.extractCharacterEncoding(contentType);
    }

    @Override
    public String getContentType() {
        return headers.get(CONTENT_TYPE_HEADER_NAME);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    /**
     * @return
     * @throws IOException
     * @since 0.5.1
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public void setCharacterEncoding(String s) {
        String oldContentType = headers.get(CONTENT_TYPE_HEADER_NAME);
        String contextType = JerryMouseHttpUtils.replaceCharacterEncoding(oldContentType, s);
        headers.put(CONTENT_TYPE_HEADER_NAME, contextType);
    }

    @Override
    public void setContentLength(int i) {
        headers.put(CONTENT_LENGTH_HEADER_NAME, String.valueOf(i));
    }

    @Override
    public void setContentLengthLong(long l) {
        headers.put(CONTENT_LENGTH_HEADER_NAME, String.valueOf(l));
    }

    @Override
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

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        writer.flush();
        outputStream.flush();
    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
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

        @Override
        public void flush() throws IOException {
            byteArrayOutputStream.flush();

            byte[] outputStreamContent = new byte[0];
            if (outputStream instanceof ByteArrayServletOutputStream) {
                outputStreamContent = ((ByteArrayServletOutputStream)outputStream).toByteArray();
            }

            if (outputStreamContent.length > 0) {
                String out = buildResponse(new String(outputStreamContent, Charset.forName("UTF-8")));
                // 真正写入
                response.write(out, "UTF-8");
            }
        }
    }

    public class HttpPrintWriter extends PrintWriter {
        public HttpPrintWriter(Writer out) {
            super(out);
        }

        @Override
        public void flush() {
            String writerContent = stringWriter.toString();
            if (!writerContent.isEmpty()) {
                // 构建http报文
                String out = buildResponse(writerContent);
                // 真正写入
                response.write(out, "UTF-8");
            }
        }
    }

    private String buildResponse(String body) {
        if (JerryMouseHttpUtils.isCompleteResponse(body)) {
            return body;
        } else {
            StringBuilder builder = JerryMouseHttpUtils.createResponse(Integer.valueOf(getStatus()));
            JerryMouseHttpUtils.addHeaders(builder, headers);
            // 自动生成contentLength
            if (!containsHeader(CONTENT_LENGTH_HEADER_NAME)) {
                JerryMouseHttpUtils.addHeader(builder,CONTENT_LENGTH_HEADER_NAME, String.valueOf(body.length()));
            }
            builder.append("\r\n");
            builder.append(body);
            return builder.toString();
        }
    }
}
