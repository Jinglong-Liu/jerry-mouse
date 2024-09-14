package com.github.ljl.wheel.jerrymouse.server.nio.bootstrap;

import com.github.ljl.wheel.jerrymouse.server.nio.reactor.MasterReactor;

import com.github.ljl.wheel.jerrymouse.server.nio.reactor.SubReactorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:23
 **/

public class ReactorBootstrap {

    private final Logger logger = LoggerFactory.getLogger(ReactorBootstrap.class);

    public void bind(InetSocketAddress address) {
        try {
            MasterReactor masterReactor = new MasterReactor();
            masterReactor.bindAndRegister(address);

            String ipAddress = address.getAddress().getHostAddress();
            int port = address.getPort();

            logger.info("start listen on port {}", port);
            logger.info("visit url http://{}:{}", ipAddress, port);

            masterReactor.start();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("reactor server start failed!");
        }
    }

    private ReactorBootstrap() {

    }

    public static ReactorBootstrap build() {
        return new ReactorBootstrap();
    }
}
