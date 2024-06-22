package com.github.ljl.jerrymouse.utils;

import com.github.ljl.jerrymouse.bo.RequestInfoBo;
import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 09:16
 **/

public class JerryMouseRequestUtils {

    public static RequestInfoBo buildRequestInfoBo(String text) {
        // 使用正则表达式按行分割请求字符串
        String[] requestLines = text.split("\r\n");

        // 获取第一行请求行
        String firstLine = requestLines[0];

        String[] strings = firstLine.split(" ");
        String method = strings[0];
        String url = strings[1];

        return new RequestInfoBo(url, method);
    }
}
