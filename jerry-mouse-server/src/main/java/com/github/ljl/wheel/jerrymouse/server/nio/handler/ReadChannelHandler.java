package com.github.ljl.wheel.jerrymouse.server.nio.handler;

import com.github.ljl.wheel.jerrymouse.exception.ReactorException;
import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;

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

public class ReadChannelHandler implements ChannelHandler, Closeable {

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

            // receive message
            String message = new String(bytes, StandardCharsets.UTF_8);

            System.out.println("receive message:\n" + message);

            String result = HttpUtils.http200Resp("Hello Reactor!");
            byte[] returnBytes = result.getBytes(StandardCharsets.UTF_8);

            // write back
            writeChannel(returnBytes);

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
}
