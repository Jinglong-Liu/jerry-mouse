package com.github.ljl.jerrymouse.bootstrap;

import com.github.ljl.jerrymouse.dispatcher.IRequestDispatcher;
import com.github.ljl.jerrymouse.support.context.RequestDispatcherContext;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherManager;
import com.github.ljl.jerrymouse.impl.dto.IRequest;
import com.github.ljl.jerrymouse.impl.dto.IResponse;
import com.github.ljl.jerrymouse.impl.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.support.context.IContextManager;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
import com.github.ljl.jerrymouse.support.context.JerryMouseContextManager;
import com.github.ljl.jerrymouse.utils.JerryMouseRequestUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
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
     * 请求分发
     * @since 0.3.0
     */
    private final IRequestDispatcher requestDispatcher = new RequestDispatcherManager();

    private final IContextManager contextManager = JerryMouseContextManager.get();

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String requestString = new String(bytes, Charset.defaultCharset());
        logger.info("[JerryMouse] channelRead requestString={}", requestString);

        // 获取请求信息
        IRequest request = JerryMouseRequestUtils.buildRequest(requestString);
        IResponse response = new JerryMouseResponse(context);

        ServletContext appContext = contextManager.getServletContext(request);
        // TODO: 重构
        request.setServletContext(appContext);
        // 解析url， 匹配对应的ApplicationContext
        // 分发调用
        final RequestDispatcherContext dispatcherContext = new RequestDispatcherContext();
        dispatcherContext.setAppContext((JerryMouseAppContext) appContext);
        dispatcherContext.setRequest(request);
        dispatcherContext.setResponse(response);
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
