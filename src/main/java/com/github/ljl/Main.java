package com.github.ljl;

import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private static final String baseWarDir = "D:\\java-learning\\jerry-mouse\\src\\test\\webapps";
    public static void main(String[] args) throws InterruptedException {

        JerryMouseBootstrap bootstrap = new JerryMouseBootstrap(baseWarDir);
        bootstrap.start();
        TimeUnit.SECONDS.sleep(1000);
    }
}
