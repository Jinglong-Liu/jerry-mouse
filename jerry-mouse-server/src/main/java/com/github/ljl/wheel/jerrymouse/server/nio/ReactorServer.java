package com.github.ljl.wheel.jerrymouse.server.nio;

import com.github.ljl.wheel.jerrymouse.bootstrap.WebServerBootstrap;
import com.github.ljl.wheel.jerrymouse.server.nio.bootstrap.ReactorBootstrap;

import java.net.InetSocketAddress;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:22
 **/

public class ReactorServer implements WebServerBootstrap {
    public static final String HOST = "127.0.0.1";

    @Override
    public void start(Integer port) {
        ReactorBootstrap reactorBootStrap = ReactorBootstrap.build();
        reactorBootStrap.bind(new InetSocketAddress(HOST, port));
    }
}
