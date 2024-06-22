package com.github.ljl.jerrymouse.bootstrap;

import com.github.ljl.jerrymouse.bo.RequestInfoBo;
import com.github.ljl.jerrymouse.dispatcher.IRequestDispatcher;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherContext;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherManager;
import com.github.ljl.jerrymouse.dto.IRequest;
import com.github.ljl.jerrymouse.dto.IResponse;
import com.github.ljl.jerrymouse.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import com.github.ljl.jerrymouse.servlet.manager.WebXmlServletManager;
import com.github.ljl.jerrymouse.utils.JerryMouseRequestUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.4.2
 * @create: 2024-06-22 09:11
 **/

public class JerryMouseServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequestUtils.class);

    /**
     * servlet 管理
     *
     * @since 0.3.0
     */
    private final IServletManager servletManager = new WebXmlServletManager();

    /**
     * 请求分发
     *
     * @since 0.3.0
     */
    private final IRequestDispatcher requestDispatcher = new RequestDispatcherManager();

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String requestString = new String(bytes, Charset.defaultCharset());
        logger.info("[JerryMouse] channelRead requestString={}", requestString);


        // 获取请求信息
        RequestInfoBo requestInfoBo = JerryMouseRequestUtils.buildRequestInfoBo(requestString);
        IRequest request = new JerryMouseRequest(requestInfoBo.getMethod(), requestInfoBo.getUrl());
        IResponse response = new JerryMouseResponse(context);

        // 分发调用
        final RequestDispatcherContext dispatcherContext = new RequestDispatcherContext();
        dispatcherContext.setRequest(request);
        dispatcherContext.setResponse(response);
        dispatcherContext.setServletManager(servletManager);
        requestDispatcher.dispatch(dispatcherContext);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        logger.error("JerryMouseServerHandler cause exception ", cause);
        context.close();
    }
}
