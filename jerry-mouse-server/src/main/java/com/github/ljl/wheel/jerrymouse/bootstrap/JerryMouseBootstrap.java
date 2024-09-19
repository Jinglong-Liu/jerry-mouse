package com.github.ljl.wheel.jerrymouse.bootstrap;

// import com.github.ljl.wheel.jerrymouse.server.netty.NettyServer;
import com.github.ljl.wheel.jerrymouse.server.nio.ReactorServer;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContextManager;
import com.github.ljl.wheel.jerrymouse.utils.WarUtils;
import com.github.ljl.wheel.jerrymouse.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:28
 **/

public class JerryMouseBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);
    private static final String baseWarDir = "E:\\repo\\jerry-mouse\\jerry-mouse-server\\src\\test\\webapps";
    // private final WebServerBootstrap webServerBootStrap = new NettyServer();
    private final WebServerBootstrap webServerBootStrap = new ReactorServer();
    public void start(Integer port) {
        XmlUtils.parseLocalWebXml();
        // 解压
        Set<String> appNameSet = WarUtils.extract(baseWarDir);
        // 扫描注册
        XmlUtils.loadApps(baseWarDir, appNameSet);

        webServerBootStrap.start(port);
    }
    public void stop() {
        logger.info("server stop");
    }

    public void startBootApplication(int port) {
        webServerBootStrap.start(port);
    }
    // 用于SpringBoot 嵌入式jerry-mouse
    public ApplicationContext getApplicationContext(String applicationName) {
        return ApplicationContextManager.getOrCreateApplication(applicationName);
    }
}
