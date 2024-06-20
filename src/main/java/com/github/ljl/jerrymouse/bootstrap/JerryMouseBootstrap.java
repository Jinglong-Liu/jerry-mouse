package com.github.ljl.jerrymouse.bootstrap;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.threadpool.JerryMouseThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 14:33
 **/

public class JerryMouseBootstrap {
    Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private static final int DEFAULT_PORT = 8080;

    private static final String LOCAL_HOST = "127.0.0.1";

    private final int port;

    private ServerSocket serverSocket;

    private volatile boolean runningFlag;

    private JerryMouseThreadPoolUtil threadPool;

    public JerryMouseBootstrap(int port) {
        this.port = port;
        this.threadPool = JerryMouseThreadPoolUtil.get();
    }
    public JerryMouseBootstrap() {
        this(DEFAULT_PORT);
    }
    /**
     * 符合 http 标准的字符串
     * @param rawText 原始文本
     * @return 结果
     */
    private static String httpResp(String rawText) {
        String format = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "%s";

        return String.format(format, rawText);
    }
    public void start() {
        threadPool.execute(() -> {
            startService();
        });
    }
    private void startService() {
        if(runningFlag) {
            logger.warn("[Jerry-mouse] server is already start!");
            return;
        }

        logger.info("[Jerry-mouse] start listen on port {}", port);
        logger.info("[Jerry-mouse] visit url http://{}:{}", LOCAL_HOST, port);
        try {
            this.serverSocket = new ServerSocket(port);
            runningFlag = true;
            while(true) {
                Socket socket = serverSocket.accept();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(httpResp("Hello JerryMouse!").getBytes());
                socket.close();
            }

        } catch (IOException e) {
            logger.error("[JerryMouse] meet ex", e);
            throw new JerryMouseException(e);
        }
    }

    public void stop() {
        if(!runningFlag) {
            logger.warn("[MiniCat] server is not start!");
            return;
        }

        try {
            if(this.serverSocket != null) {
                serverSocket.close();
            }
            this.runningFlag = false;
            logger.info("[JerryMouse] stop listen on port {}", port);
        } catch (IOException e) {
            logger.error("[JerryMouse] stop meet ex", e);
            throw new JerryMouseException(e);
        }
    }
}
