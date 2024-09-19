package com.github.ljl.test.web;

import com.github.ljl.wheel.jerrymouse.config.JerryMouseWebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-18 10:39
 **/

@SpringBootApplication
@Import(JerryMouseWebServerConfig.class)
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
