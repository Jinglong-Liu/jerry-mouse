package com.github.ljl.wheel.jerrymouse.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 12:59
 **/

public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Writer writer = resp.getWriter();
        writer.write("Hello Get\r\n");
        writer.write("Hello writer\r\n");
        writer.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        OutputStream outputStream = resp.getOutputStream();
        outputStream.write("Hello Post\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write("Hello outputStream\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
