package com.github.ljl.wheel.jerrymouse.server.netty;

import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 12:22
 **/

public class NettyHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(NettyHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String requestString = new String(bytes, Charset.defaultCharset());
        logger.info("netty receive request message:\n" + requestString);

        String result = HttpUtils.http200Resp("Hello, Netty!");

        ByteBuf responseBuf = Unpooled.copiedBuffer(result, Charset.defaultCharset());
        context.writeAndFlush(responseBuf)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        logger.error("NettyHandler cause exception");
        context.close();
        throw new NettyException(cause);
    }
}
