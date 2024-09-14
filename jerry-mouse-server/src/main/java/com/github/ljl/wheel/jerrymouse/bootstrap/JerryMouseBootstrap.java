package com.github.ljl.wheel.jerrymouse.bootstrap;

// import com.github.ljl.wheel.jerrymouse.server.netty.NettyServer;
import com.github.ljl.wheel.jerrymouse.server.nio.ReactorServer;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:28
 **/

public class JerryMouseBootstrap {
    // private final WebServerBootstrap webServerBootStrap = new NettyServer();
    private final WebServerBootstrap webServerBootStrap = new ReactorServer();
    public void start() {
        webServerBootStrap.start();
    }
}
