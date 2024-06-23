package com.github.ljl.jerrymouse.bootstrap;

import com.github.ljl.jerrymouse.dispatcher.IRequestDispatcher;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherManager;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.servlet.manager.IServletManager;
import com.github.ljl.jerrymouse.servlet.manager.LocalClassloader;
import com.github.ljl.jerrymouse.servlet.manager.WarServletManager;
import com.github.ljl.jerrymouse.servlet.manager.WebXmlServletManager;
import com.github.ljl.jerrymouse.support.war.IWarExtractor;
import com.github.ljl.jerrymouse.support.war.WarExtractor;
import com.github.ljl.jerrymouse.threadpool.JerryMouseThreadPoolUtil;
import com.github.ljl.jerrymouse.utils.JerryMouseResourceUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @version v4.2
 * @create: 2024-06-20 14:33
 **/

public class JerryMouseBootstrap {
    Logger logger = LoggerFactory.getLogger(JerryMouseBootstrap.class);

    private static final String LOCAL_HOST = "127.0.0.1";

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private JerryMouseThreadPoolUtil threadPool;

    /**
     * 默认文件夹
     * @since 0.5.0
     */
    private String baseDir = JerryMouseResourceUtils.getClassRootResource(JerryMouseBootstrap.class);

    /**
     * war 解压管理
     *
     * @since 0.5.0
     */
    private IWarExtractor warExtractor = new WarExtractor();

    @Getter
    @Setter
    private IRequestDispatcher requestDispatcher = new RequestDispatcherManager();


    // private IServletManager servletManager = new WebXmlServletManager();

    /**
     * servlet 管理
     *
     * @since 0.5.0
     */
    @Getter
    @Setter
    private IServletManager servletManager = new WarServletManager();

    private WebXmlServletManager localWebXmlServletManager = new WebXmlServletManager();

    public JerryMouseBootstrap(int port) {
        this.port = port;
        this.threadPool = JerryMouseThreadPoolUtil.get();
    }
    public JerryMouseBootstrap(String baseDir) {
        this(DEFAULT_PORT);
        this.baseDir = baseDir;
    }
    public JerryMouseBootstrap() {
        this(DEFAULT_PORT);
    }

    public void start() {
        threadPool.execute(() -> {
            startService();
        });
    }

    public void startService() {
        before();
        logger.info("[JerryMouse] start listen on port {}", port);
        logger.info("[JerryMouse] visit url http://{}:{}", LOCAL_HOST, port);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker 线程池的数量默认为 CPU 核心数的两倍
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new JerryMouseServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture future = serverBootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            future.channel().closeFuture().sync();
            logger.info("DONE");
        } catch (InterruptedException e) {
            logger.error("[JerryMouse] start meet exception");
            throw new JerryMouseException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    private void before() {
        logger.info("[MiniCat] beforeStart baseDir={}", baseDir);

        //1. 加载解析所有的 war 包
        //2. 解压 war 包
        //3. 解析对应的 servlet 映射关系
        warExtractor.extract(baseDir);

        // 初始化 servlet 映射关系
        servletManager.init(baseDir);

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            localWebXmlServletManager.loadFromWebXml("", document, new LocalClassloader());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
