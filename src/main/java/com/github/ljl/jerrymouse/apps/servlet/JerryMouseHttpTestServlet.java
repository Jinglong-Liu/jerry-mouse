package com.github.ljl.jerrymouse.apps.servlet;

import com.github.ljl.jerrymouse.impl.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 18:32
 **/

public class JerryMouseHttpTestServlet extends AbstractJerryMouseServlet {

    private static Logger logger = LoggerFactory.getLogger(JerryMouseHttpTestServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String content = "JerryMouseHttpTestServlet-get";

        JerryMouseResponse response = (JerryMouseResponse) resp;
        response.write(JerryMouseHttpUtils.http200Resp(content));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String content = "JerryMouseHttpTestServlet-post";

        JerryMouseResponse response = (JerryMouseResponse) resp;
        response.write(JerryMouseHttpUtils.http200Resp(content));
    }
}
