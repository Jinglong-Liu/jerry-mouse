package com.github.ljl.jerrymouse.dto;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since: 0.5.1
 * @create: 2024-06-23 08:45
 **/

public class JerryMouseResponseHelper implements HttpServletResponse {

    private JerryMouseResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;
    /**
     * 采用ByteArrayServletOutputStream
     * 若采用OutputStreamHelper(response)，则复用JerryMouseRequest.write()
     * 两者均可正常运行
     */
    private final ServletOutputStream outputStream;

    public JerryMouseResponseHelper(JerryMouseResponse response) {
        this.response = response;
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.outputStream = new ByteArrayServletOutputStream();
        // this.outputStream = new OutputStreamHelper(response);
    }
    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
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

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {

    }

    @Override
    public void setStatus(int i, String s) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
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

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

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

        String writerContent = stringWriter.toString();
        if (!writerContent.isEmpty()) {
            response.write(writerContent, "UTF-8");
        }

        byte[] outputStreamContent = new byte[0];
        if (outputStream instanceof ByteArrayServletOutputStream) {
            outputStreamContent = ((ByteArrayServletOutputStream)outputStream).toByteArray();
        }

        if (outputStreamContent.length > 0) {
            response.write(new String(outputStreamContent, Charset.forName("UTF-8")), "UTF-8");
        }
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

    /**
     * 复用JerryMouseResponse
     */
    private static class OutputStreamHelper extends ServletOutputStream {

        private final JerryMouseResponse response;

        public OutputStreamHelper(JerryMouseResponse response) {
            this.response = response;
        }
        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {

        }

        @Override
        public void print(String s) throws IOException {
            response.write(s);
        }
    }
    private static class ByteArrayServletOutputStream extends ServletOutputStream {
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
        }
    }
}
