package com.github.ljl.jerrymouse.dto;

import com.github.ljl.jerrymouse.adaptor.JerryMouseResponseAdaptor;
import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/

public class JerryMouseResponse extends JerryMouseResponseAdaptor {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private SocketChannel clientChannel;

    public JerryMouseResponse(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void write(String data){
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        while (buffer.hasRemaining()) {
            try {
                clientChannel.write(buffer);
                // 这句不能省略
                clientChannel.close();
            }
            catch (IOException e) {
                logger.error("[JerryMouse] meet exception");
                throw new JerryMouseException(e);
            }
        }
    }
}
