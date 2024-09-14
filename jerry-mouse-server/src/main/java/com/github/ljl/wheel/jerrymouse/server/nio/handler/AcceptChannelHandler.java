package com.github.ljl.wheel.jerrymouse.server.nio.handler;

import com.github.ljl.wheel.jerrymouse.server.nio.reactor.SubReactorManager;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 09:59
 **/

public class AcceptChannelHandler implements ChannelHandler {
    private final ServerSocketChannel ssc;

    private SubReactorManager subReactorManager = SubReactorManager.get();

    public AcceptChannelHandler(ServerSocketChannel ssc) {
        this.ssc = ssc;
    }

    @Override
    public void run() {
        try {
            SocketChannel sc = ssc.accept();
            if (Objects.isNull(sc)) {
                return;
            }
            sc.configureBlocking(false);
            subReactorManager.nextReactor().addRegisterChannel(sc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
