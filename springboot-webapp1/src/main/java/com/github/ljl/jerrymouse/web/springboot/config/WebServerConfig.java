package com.github.ljl.jerrymouse.web.springboot.config;

import com.github.ljl.jerrymouse.apps.servlet.JerryMouseHttpServlet;
import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
import com.github.ljl.jerrymouse.support.servlet.JerryMouseServletConfig;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Arrays;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-07-01 10:16
 **/

@Configuration
public class WebServerConfig {
    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new JerryMouseServletWebServerFactory();
    }

    static class JerryMouseServletWebServerFactory extends AbstractServletWebServerFactory {
        @Override
        public WebServer getWebServer(ServletContextInitializer... initializers) {
            return new JerryMouseWebServer(initializers);
        }

        static class JerryMouseWebServer implements WebServer {
            public JerryMouseBootstrap server;
            private ServletContext servletContext;
            public JerryMouseWebServer(ServletContextInitializer... initializers) {
                this.server = new JerryMouseBootstrap("spring-boot-app");
                this.servletContext = server.getApplicationServletContext();
                Arrays.stream(initializers).forEach(servletContextInitializer -> {
                    try {
                        servletContextInitializer.onStartup(servletContext);
                    } catch (ServletException e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            public void start() throws WebServerException {
                System.out.println("server start");
                try {
                    Servlet servlet = ((JerryMouseAppContext) servletContext).getServlet("dispatcherServlet");
                    if (servlet instanceof DispatcherServlet) {
                        DispatcherServlet dispatcherServlet = (DispatcherServlet) servlet;
                        ServletConfig servletConfig = new JerryMouseServletConfig(servletContext, "dispatcherServlet", servlet.getClass().getName());
                        dispatcherServlet.init(servletConfig);
                    }
                } catch (ServletException e) {
                    e.printStackTrace();
                }
                server.start();
            }

            @Override
            public void stop() throws WebServerException {
                System.out.println("server stop");
            }

            @Override
            public int getPort() {
                return 8080;
            }
        }
    }
}
