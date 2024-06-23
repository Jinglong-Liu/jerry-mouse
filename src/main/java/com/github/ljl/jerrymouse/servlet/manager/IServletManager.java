package com.github.ljl.jerrymouse.servlet.manager;

import javax.servlet.http.HttpServlet;

public interface IServletManager {
    /**
     * 初始化
     * @param baseDir 基础文件夹
     * @since 0.5.0
     */
    void init(String baseDir);
    /**
     * 注册 servlet
     *
     * @param url     url
     * @param servlet servlet
     */
    void register(String url, HttpServlet servlet);

    /**
     * 获取 servlet
     *
     * @param url url
     * @return servlet
     */
    HttpServlet getServlet(String url);
}
