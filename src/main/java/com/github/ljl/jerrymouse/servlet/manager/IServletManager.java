package com.github.ljl.jerrymouse.servlet.manager;

import javax.servlet.http.HttpServlet;

public interface IServletManager {
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
