package com.github.ljl.jerrymouse.support.listener;

import javax.servlet.http.HttpServlet;
import java.util.EventListener;
import java.util.List;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.7.0
 * @create: 2024-06-24 11:59
 **/

public interface IListenerManager {
    /**
     * 初始化
     * @param baseDir 基础文件夹
     * @since 0.7.0
     */
    void init(String baseDir);
    /**
     * 注册 listener
     *
     * @param UrlPrefix
     * @param listener
     * @since 0.7.0
     */
    void register(String urlPrefix, EventListener listener);

    /**
     * 获取 listener
     * @return listeners
     * @since 0.7.0
     */
    List<EventListener> getListeners();
}
