package com.github.ljl.wheel.jerrymouse.config;

import com.github.ljl.wheel.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.support.servlet.config.ServletConfigWrapper;
import com.github.ljl.wheel.jerrymouse.utils.StringUtils;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-18 12:44
 **/

@Configuration
public class JerryMouseWebServerConfig {

    @Resource
    private Environment environment;

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new JerryMouseServletWebServerFactory();
    }

    class JerryMouseServletWebServerFactory extends AbstractServletWebServerFactory {
        // 默认
        private static final String applicationName = "/root";

        @Override
        public WebServer getWebServer(ServletContextInitializer... initializers) {
            return new JerryMouseWebServer(applicationName,  initializers);
        }

        class JerryMouseWebServer implements WebServer {

            private final Integer MAX_PORT = 65535;
            private final Integer MIN_PORT = 1024;
            private final Integer DEFAULT_PORT = 8080;

            private Integer port;

            public JerryMouseBootstrap server;
            private final ApplicationContext applicationContext;
            public JerryMouseWebServer(String applicationName, ServletContextInitializer... initializers) {
                if (Objects.nonNull(environment)) {
                    // application.yaml等配置文件中配置
                    this.port = checkPort(environment.getProperty("server.port"));
                }
                this.server = new JerryMouseBootstrap();
                this.applicationContext = server.getApplicationContext(applicationName);
                Arrays.stream(initializers).forEach(servletContextInitializer -> {
                    try {
                        servletContextInitializer.onStartup(applicationContext);
                    } catch (ServletException e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            public void start() throws WebServerException {
                // start 前，dispatcherServlet已经被springmvc创建并注册好，key = dispatcherServlet
                final String dispatcherServletName = "dispatcherServlet";
                applicationContext.log("jerry-mouse-servlet-web-server-factory start");
                try {
                    Servlet servlet = applicationContext.getServlet(dispatcherServletName);
                    if (servlet instanceof DispatcherServlet) {
                        DispatcherServlet dispatcherServlet = (DispatcherServlet) servlet;
                        applicationContext.registerServlet(applicationName + "/", dispatcherServlet);
                        ServletConfig servletConfig = new ServletConfigWrapper(applicationContext, dispatcherServletName, servlet.getClass().getName());
                        dispatcherServlet.init(servletConfig);
                    }
                } catch (ServletException e) {
                    e.printStackTrace();
                }
                server.startBootApplication(port);
            }

            @Override
            public void stop() throws WebServerException {
                server.stop();
            }

            @Override
            public int getPort() {
                return port;
            }

            private Integer checkPort(Object object) {
                String value = (String) object;
                if (StringUtils.isEmptyTrim(value)) {
                    return DEFAULT_PORT;
                }
                int number = Integer.parseInt((String) object);
                if (number < MIN_PORT || number > MAX_PORT) {
                    throw new IllegalStateException("Port out of range: " + number);
                }
                return number;
            }
        }
    }
}

