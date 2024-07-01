package com.github.ljl.jerrymouse.apps.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.7.0
 * @create: 2024-06-24 10:12
 **/

public class JerryMouseContextAttributeListener implements ServletContextAttributeListener {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseContextAttributeListener.class);

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        logger.info("[JerryMouse] ContextAttribute added name={}, value={}", event.getName(), event.getValue());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        logger.info("[JerryMouse] ContextAttribute removed name={}, value={}", event.getName(), event.getValue());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        logger.info("[JerryMouse] ContextAttribute replaced name={}, value={}", event.getName(), event.getValue());
    }
}
