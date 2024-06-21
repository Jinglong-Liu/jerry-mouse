package com.github.ljl.jerrymouse.bootstrap;

import com.github.ljl.jerrymouse.dispatcher.IRequestDispatcher;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherContext;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherContextBio;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherManager;
import com.github.ljl.jerrymouse.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseRequestBio;
import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.dto.JerryMouseResponseBio;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import com.github.ljl.jerrymouse.servlet.manager.WebXmlServletManager;
import com.github.ljl.jerrymouse.threadpool.JerryMouseThreadPoolUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: jerry-mouse
 * @description: 用于 v3.0 之前
 * @author: ljl
 * @create: 2024-06-21 15:38
 **/

@Deprecated
public class JerryMouseBootstrapBio {
    Logger logger = LoggerFactory.getLogger(JerryMouseBootstrapBio.class);

    private static final int DEFAULT_PORT = 8080;

    private static final String LOCAL_HOST = "127.0.0.1";

    private final int port;

    private ServerSocket serverSocket;

    private volatile boolean runningFlag;

    private JerryMouseThreadPoolUtil threadPool;

    /*
     * 请求分发
     */
    @Getter
    @Setter
    private IRequestDispatcher requestDispatcher = new RequestDispatcherManager();

    @Getter
    @Setter
    private IServletManager servletManager = new WebXmlServletManager();

    public JerryMouseBootstrapBio(int port) {
        this.port = port;
        this.threadPool = JerryMouseThreadPoolUtil.get();
    }
    public JerryMouseBootstrapBio() {
        this(DEFAULT_PORT);
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
            while(runningFlag && !serverSocket.isClosed()) {
                try (Socket socket = serverSocket.accept();){
                    JerryMouseRequestBio request = new JerryMouseRequestBio(socket.getInputStream());
                    JerryMouseResponseBio response = new JerryMouseResponseBio(socket.getOutputStream());

                    // 分发处理
                    final RequestDispatcherContextBio dispatcherContext = new RequestDispatcherContextBio();
                    // dispatcherContext.setRequestBio(request);
                    // dispatcherContext.setResponseBio(response);
                    // 需要get接口, 用于之后根据url获取servlet
                    dispatcherContext.setServletManager(servletManager);
                    // this.requestDispatcher.dispatch(dispatcherContext);
                    // socket.close();
                } catch (IOException e) {
                    logger.error("[JerryMouse] meet exception {}", e);
                }
            }

        } catch (IOException e) {
            logger.error("[JerryMouse] meet exception {}", e);
            throw new JerryMouseException(e);
        }
    }

    public void stop() {
        if(!runningFlag) {
            logger.warn("[JerryMouse] server is not start!");
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
