package com.github.ljl.jerrymouse.impl.dto;

import com.github.ljl.jerrymouse.impl.dto.adaptor.JerryMouseResponseAdaptor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/

public class JerryMouseResponse extends JerryMouseResponseAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseResponse.class);

    private final ChannelHandlerContext context;

    private final HttpServletResponse helper;

    public JerryMouseResponse(ChannelHandlerContext context) {
        this.context = context;
        helper = new JerryMouseResponseHelper(this);
    }
    @Override
    public void write(String text, String charsetStr) {
        Charset charset = Charset.forName(charsetStr);
        ByteBuf responseBuf = Unpooled.copiedBuffer(text, charset);
        context.writeAndFlush(responseBuf)
                .addListener(ChannelFutureListener.CLOSE); // Close the channel after sending the response
        logger.info("[JerryMouse] channelRead writeAndFlush DONE");
    }

    /**
     * @return PrintWriter
     * @throws IOException
     * @since 0.5.1
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        return helper.getWriter();
    }

    /**
     * @return PrintWriter
     * @throws IOException
     * @since 0.5.1
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return helper.getOutputStream();
    }

    /**
     * @return PrintWriter
     * @throws IOException
     * @since 0.5.1
     */
    @Override
    public void flushBuffer() throws IOException {
        helper.flushBuffer();
    }

    /**
     * @param s header name
     * @return header value
     * @since 0.7.4
     */
    @Override
    public String getHeader(String s) {
        return helper.getHeader(s);
    }

    @Override
    public void setHeader(String s, String s1) {
        helper.setHeader(s, s1);
    }

    @Override
    public void setContentType(String s) {
        helper.setContentType(s);
    }

    @Override
    public String getContentType() {
        return helper.getContentType();
    }

    @Override
    public void setCharacterEncoding(String s) {
        helper.setCharacterEncoding(s);
    }

    @Override
    public String getCharacterEncoding() {
        return helper.getCharacterEncoding();
    }

    @Override
    public void setStatus(int i) {
        helper.setStatus(i);
    }

    @Override
    public int getStatus() {
        return helper.getStatus();
    }
}
