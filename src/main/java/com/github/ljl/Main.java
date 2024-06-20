package com.github.ljl;

import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);
    public static void main(String[] args) throws InterruptedException {
        JerryMouseBootstrap bootstrap = new JerryMouseBootstrap();
        bootstrap.start();
        logger.info("main START sleep");
        TimeUnit.SECONDS.sleep(10);
        logger.info("main END sleep");
        bootstrap.stop();
    }
}
