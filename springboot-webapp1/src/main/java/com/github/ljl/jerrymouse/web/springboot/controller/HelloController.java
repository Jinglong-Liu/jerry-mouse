package com.github.ljl.jerrymouse.web.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-07-01 09:43
 **/

@RestController("/")
public class HelloController {
    @GetMapping(value = "hello")
    String hello() {
        return "hello springboot";
    }
}
