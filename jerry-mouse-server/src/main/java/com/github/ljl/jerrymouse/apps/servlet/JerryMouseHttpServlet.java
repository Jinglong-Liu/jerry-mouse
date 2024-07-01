package com.github.ljl.jerrymouse.apps.servlet;

import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: jerry-mouse
 * @description: 只用 HttpServlet 接口
 * @author: ljl
 * @since 0.5.1
 * @create: 2024-06-23 09:29
 **/

public class JerryMouseHttpServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseHttpServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String content = "HttpServlet-get using stream";
        resp.setContentType("text/html");
        try {
            ServletOutputStream outputStream = resp.getOutputStream();
            outputStream.print(JerryMouseHttpUtils.http200Resp(content));
            resp.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        String content = "HttpServlet-post using writer";
        resp.setContentType("text/html");
        try {
            PrintWriter writer = resp.getWriter();
            writer.print(JerryMouseHttpUtils.http200Resp(content));
            resp.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
