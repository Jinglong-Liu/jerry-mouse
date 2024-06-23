package com.github.ljl.jerrymouse.servlet;

import com.github.ljl.jerrymouse.dto.JerryMouseResponse;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.6.0
 * @create: 2024-06-23 20:33
 **/

public class JerryMouseFilterTestServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseFilterTestServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String content = "test filter get";
        logger.info("[JerryMouse] servlet doGet is called");
        PrintWriter printWriter = null;
        try {
            printWriter = resp.getWriter();
            printWriter.print(JerryMouseHttpUtils.http200Resp(content));
            resp.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
