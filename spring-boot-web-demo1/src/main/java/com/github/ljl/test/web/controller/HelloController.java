package com.github.ljl.test.web.controller;

import com.github.ljl.test.web.pojo.vo.HelloVO;
import com.github.ljl.test.web.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-18 11:32
 **/

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping
    public Result<HelloVO> hello() {
        HelloVO helloVO = new HelloVO("value1", "value2");
        Result<HelloVO> result = Result.success(helloVO).msg("hello");
        return result;
    }
}
