package com.github.ljl.jerrymouse.servlet;

import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-21 10:52
 **/

public class JerryMouseHttpTest2Servlet extends AbstractJerryMouseServlet {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseHttpTest2Servlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String content = "JerryMouseHttpTestServlet2-get";

        JerryMouseResponse response = (JerryMouseResponse) resp;
        response.write(JerryMouseHttpUtils.http200Resp(content));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String content = "JerryMouseHttpTestServlet2-post";

        JerryMouseResponse response = (JerryMouseResponse) resp;
        response.write(JerryMouseHttpUtils.http200Resp(content));
    }
}
