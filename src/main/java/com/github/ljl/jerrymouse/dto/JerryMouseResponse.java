package com.github.ljl.jerrymouse.dto;

import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/

public class JerryMouseResponse extends AbstractResponse {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private final ChannelHandlerContext context;
    public JerryMouseResponse(ChannelHandlerContext context) {
        this.context = context;
    }
    @Override
    public void write(String text, String charsetStr) {
        Charset charset = Charset.forName(charsetStr);
        ByteBuf responseBuf = Unpooled.copiedBuffer(text, charset);
        context.writeAndFlush(responseBuf)
                .addListener(ChannelFutureListener.CLOSE); // Close the channel after sending the response
        logger.info("[JerryMouse] channelRead writeAndFlush DONE");
    }
}
