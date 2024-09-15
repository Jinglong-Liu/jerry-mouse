package com.github.ljl.wheel.jerrymouse.server.nio.handler;

import com.github.ljl.wheel.jerrymouse.exception.ReactorException;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContextManager;
import com.github.ljl.wheel.jerrymouse.support.dispatcher.RequestDispatcher;
import com.github.ljl.wheel.jerrymouse.support.dispatcher.RequestDispatcherContext;
import com.github.ljl.wheel.jerrymouse.support.servlet.request.RequestImpl;
import com.github.ljl.wheel.jerrymouse.support.servlet.response.ResponseImpl;
import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:05
 **/

public class ReadChannelHandler implements ChannelHandler, Closeable, SocketWriter {

    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final int BUF_SIZE = 1024;
    private final SelectionKey key;
    private final SocketChannel sc;

    private final List<Byte> msg = new LinkedList<>();
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUF_SIZE);

    public ReadChannelHandler(SelectionKey key) {
        this.key = key;
        this.sc = (SocketChannel) key.channel();
    }

    @Override
    public void run() {
        if (isValid() && readChannelComplete()) {
            byte[] bytes = new byte[msg.size()];
            for (int i = 0; i < msg.size(); i++) {
                bytes[i] = msg.get(i);
            }

            String message = new String(bytes, StandardCharsets.UTF_8);
            if (message.isEmpty()) {
                logger.error("empty message!");
                this.close();
                return;
            }
            logger.debug("receive request message:\n{}", message);
            // till now, there is only one application, one context
            // 通过URL查找到对应的context

            RequestImpl request = new RequestImpl(message);
            ServletContext servletContext = ApplicationContextManager.getApplicationContext(request);
            request.setServletContext(servletContext);

            // client can use response to write data, so the socketWrite is necessary
            HttpServletResponse response = new ResponseImpl(this, servletContext);
            RequestDispatcherContext dispatcherContext = new RequestDispatcherContext(request, response);
            // then, only focus on this
            RequestDispatcher.get().dispatch(dispatcherContext);

            this.close();
        }
    }

    private boolean readChannelComplete() {
        if (!isValid()) {
            return true;
        }
        try {
            synchronized (sc) {
                if (isValid()) {
                    buffer.clear();
                    int len = sc.read(buffer);
                    if (!isValid() || len <= 0) {
                        return true;
                    }
                    buffer.flip();
                    for (int i = 0, limit = buffer.limit(); i < limit; i++) {
                        msg.add(buffer.get(i));
                    }
                    return false;
                }
            }
        } catch (IOException e) {
            throw new ReactorException(e);
        }
        return true;
    }

    private void writeChannel(byte[] response) {
        if (!isValid()) {
            return;
        }
        try {
            synchronized (sc) {
                if (!isValid()) {
                    return;
                }
                sc.write(ByteBuffer.wrap(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid() {
        return key.isValid() && sc.isConnected();
    }

    @Override
    public void close() {
        try {
            synchronized (sc) {
                key.cancel();
                sc.socket().close();
                sc.close();
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void write(byte[] data) {
        writeChannel(data);
    }

    @Override
    public void write(String data) {
        write(data.getBytes(StandardCharsets.UTF_8));
    }
}
