package com.github.ljl.jerrymouse.bootstrap;

import com.github.ljl.jerrymouse.dispatcher.IRequestDispatcher;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherContext;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherManager;
import com.github.ljl.jerrymouse.dto.JerryMouseRequest;
import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import com.github.ljl.jerrymouse.servlet.manager.WebXmlServletManager;
import com.github.ljl.jerrymouse.threadpool.JerryMouseThreadPoolUtil;
import com.github.ljl.jerrymouse.utils.JerryMouseFileUtils;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 14:33
 **/

public class JerryMouseBootstrap {
    Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private JerryMouseThreadPoolUtil threadPool;

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;
    /*
     * 请求分发
     */
    @Getter
    @Setter
    private IRequestDispatcher requestDispatcher = new RequestDispatcherManager();

    @Getter
    @Setter
    private IServletManager servletManager = new WebXmlServletManager();

    public JerryMouseBootstrap(int port) {
        this.port = port;
        this.threadPool = JerryMouseThreadPoolUtil.get();
    }
    public JerryMouseBootstrap() {
        this(DEFAULT_PORT);
    }

    public void start() {
        threadPool.execute(() -> {
            startService();
        });
    }

    private void startService() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("[JerryMouse] start meet exception {}", e);
            throw new JerryMouseException(e);
        }
    }
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey key) {
        // TODO: 异步执行会有问题
        // threadPool.execute(() -> {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            if (!clientChannel.isOpen()) {
                return;
            }
            try {
                JerryMouseRequest request = new JerryMouseRequest(clientChannel);
                JerryMouseResponse response = new JerryMouseResponse(clientChannel);
                RequestDispatcherContext dispatcherContext = new RequestDispatcherContext();
                dispatcherContext.setRequest(request);
                dispatcherContext.setResponse(response);
                dispatcherContext.setServletManager(servletManager);
                requestDispatcher.dispatch(dispatcherContext);
            } finally {
                try {
                    if (clientChannel.isOpen()) {
                        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                        key.selector().wakeup(); // 确保selector从阻塞状态返回
                    } else {
                        key.cancel();
                    }
                } catch (CancelledKeyException e) {
                    logger.error("Key has been cancelled", e);
                }
            }
        // });
    }

    public void shutdown() {
        try {
            threadPool.shutdown();
        } finally {
            try {
                selector.close();
                serverSocketChannel.close();
            } catch (IOException e) {
                logger.error("[JerryMouse] error closing server socket", e);
            }
        }
    }
}
