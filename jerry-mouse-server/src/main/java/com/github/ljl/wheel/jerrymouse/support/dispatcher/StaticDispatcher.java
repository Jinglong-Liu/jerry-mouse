package com.github.ljl.wheel.jerrymouse.support.dispatcher;

import com.github.ljl.wheel.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.wheel.jerrymouse.server.nio.handler.SocketWriter;
import com.github.ljl.wheel.jerrymouse.support.servlet.response.ResponseImpl;
import com.github.ljl.wheel.jerrymouse.utils.FileUtils;
import com.github.ljl.wheel.jerrymouse.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 14:04
 **/

public class StaticDispatcher implements IDispatcher {
    private Logger logger = LoggerFactory.getLogger(StaticDispatcher.class);
    @Override
    public void dispatch(RequestDispatcherContext context) {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final SocketWriter socketWriter = ((ResponseImpl)response).getSockerWriter();

        String absolutePath = FileUtils.buildFullPath(
                JerryMouseBootstrap.class.getResource("/").getPath(), request.getRequestURI());
        try {
            String content = new String(Files.readAllBytes(Paths.get(absolutePath)), StandardCharsets.UTF_8);
            logger.info("Static html found, path: {}, content={}", absolutePath, content);
            socketWriter.write(HttpUtils.http200Resp(content));
        } catch (IOException e) {
            logger.error("File not found: {}", absolutePath);
            socketWriter.write(HttpUtils.http404Resp());
        }
    }
}
