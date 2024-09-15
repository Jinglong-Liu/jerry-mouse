package com.github.ljl.wheel.jerrymouse.bootstrap;

// import com.github.ljl.wheel.jerrymouse.server.netty.NettyServer;
import com.github.ljl.wheel.jerrymouse.server.nio.ReactorServer;
import com.github.ljl.wheel.jerrymouse.utils.WarUtils;
import com.github.ljl.wheel.jerrymouse.utils.XmlUtils;

import java.util.Set;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:28
 **/

public class JerryMouseBootstrap {
    private static final String baseWarDir = "E:\\repo\\jerry-mouse\\jerry-mouse-server\\src\\test\\webapps";
    // private final WebServerBootstrap webServerBootStrap = new NettyServer();
    private final WebServerBootstrap webServerBootStrap = new ReactorServer();
    public void start() {
        XmlUtils.parseLocalWebXml();
        // 解压
        Set<String> appNameSet = WarUtils.extract(baseWarDir);
        // 扫描注册
        XmlUtils.loadApps(baseWarDir, appNameSet);

        webServerBootStrap.start();
    }
}
