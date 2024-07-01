package com.github.ljl.jerrymouse.bootstrap;

import com.github.ljl.jerrymouse.classloader.LocalClassloader;
import com.github.ljl.jerrymouse.dispatcher.IRequestDispatcher;
import com.github.ljl.jerrymouse.dispatcher.RequestDispatcherManager;
import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.support.context.IContextManager;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
import com.github.ljl.jerrymouse.support.context.JerryMouseContextManager;
import com.github.ljl.jerrymouse.support.war.IWarExtractor;
import com.github.ljl.jerrymouse.support.war.WarExtractor;
import com.github.ljl.jerrymouse.support.xml.IWebXmlManager;
import com.github.ljl.jerrymouse.support.xml.WebXmlManager;
import com.github.ljl.jerrymouse.support.threadpool.JerryMouseThreadPoolUtil;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.Objects;


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

    private IContextManager contextManager = JerryMouseContextManager.get();


    @Getter
    @Setter
    private IRequestDispatcher requestDispatcher = new RequestDispatcherManager();

    /**
     * servlet 管理
     *
     * @since 0.5.0
     */

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
    private final IWebXmlManager webXmlManager = WebXmlManager.get();

    private ServletContext appContext;

    private void before() {
        logger.info("[JerryMouse] beforeStart baseDir={}", baseDir);
        if (baseDir.startsWith("spring-boot")) {
            registerApplicationServletContext();
        } else {
            //1. 加载解析并解压所有的 war 包
            warExtractor.extract(baseDir);

            //2. 解析webapps对应的元素映射关系
            webXmlManager.parseWebappXml(baseDir);

            //3. 加载本地web.xml
            webXmlManager.parseLocalWebXml();
        }
    }
    private void registerApplicationServletContext() {
        if (Objects.isNull(this.appContext)) {
            synchronized (JerryMouseBootstrap.class) {
                if (Objects.isNull(this.appContext)) {
                    this.appContext = new JerryMouseAppContext("", "/", new LocalClassloader());
                    contextManager.registerServletContext("/", appContext);
                }
            }
        }
    }
    public ServletContext getApplicationServletContext() {
        if (Objects.isNull(this.appContext)) {
            registerApplicationServletContext();
        }
        return appContext;
    }
}
