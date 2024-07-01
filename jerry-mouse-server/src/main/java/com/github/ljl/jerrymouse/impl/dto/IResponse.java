package com.github.ljl.jerrymouse.impl.dto;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 09:22
 **/

public interface IResponse extends HttpServletResponse {
    /**
     * 写入结果
     * @param text 文本
     * @param charset 编码
     */
    void write(String text, String charset);


    /**
     * 写入结果
     * @param text 文本
     */
    default void write(String text) {
        this.write(text, "UTF-8");
    }
}
