package com.github.ljl.jerrymouse.utils;

import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:56
 **/

public class JerryMouseFileUtils {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    public static String getFileContent(String absolutePath) {
        try {
            // 读取文件内容并返回为字符串
            return new String(Files.readAllBytes(Paths.get(absolutePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("read file from path: {} error", absolutePath);
            throw new JerryMouseException(e);
        }
    }
}
