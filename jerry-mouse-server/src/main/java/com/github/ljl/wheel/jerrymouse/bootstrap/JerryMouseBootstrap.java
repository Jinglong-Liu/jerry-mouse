package com.github.ljl.wheel.jerrymouse.bootstrap;

// import com.github.ljl.wheel.jerrymouse.server.netty.NettyServer;
import com.github.ljl.wheel.jerrymouse.server.nio.ReactorServer;
import com.github.ljl.wheel.jerrymouse.utils.XmlUtils;

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
        XmlUtils.parseLocalWebXml();
        webServerBootStrap.start();
    }
}
