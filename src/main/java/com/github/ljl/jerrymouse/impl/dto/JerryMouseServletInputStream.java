package com.github.ljl.jerrymouse.impl.dto;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-28 11:35
 **/

public class JerryMouseServletInputStream extends ServletInputStream {

    private ByteArrayInputStream byteArrayInputStream;

    public JerryMouseServletInputStream(String body, Charset charset) {
        byte bytes[] = body.getBytes(charset);
        this.byteArrayInputStream = new ByteArrayInputStream(bytes);
    }
    public JerryMouseServletInputStream(String body) {
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
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }
}
