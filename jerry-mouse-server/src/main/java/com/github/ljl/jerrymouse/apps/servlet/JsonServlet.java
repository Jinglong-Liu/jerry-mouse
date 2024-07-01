package com.github.ljl.jerrymouse.apps.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-26 10:03
 **/

public class JsonServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 要求最终返回报文中，Content-Type字段值application/json; charset=UTF-8
        resp.setContentType("text/html");
        // 要求允许添加(更改)CharacterEncoding，即为text/html; charset=UTF-8
        resp.setCharacterEncoding("UTF-8");
        System.out.println(resp.getCharacterEncoding());
        System.out.println(req.getContentType());
        // 要求允许更改ContentType，而CharacterEncoding不变，即为application/json; charset=UTF-8
        resp.setContentType("application/json");
        // 先不去管怎么生成json串的(有工具可以快速生成)
        String jsonPart1 = "{ \"message\": \"GET, JSON!\", ";
        String jsonPart2 = "\"code\":\"0\" }";
        // 该值与浏览器/postman解析出的Context-Length应当一致
        System.out.println("doGet: predicted content length = " + (jsonPart1 + jsonPart2).length());
        // body返回json串, 要求允许分段write，并被浏览器解析
        PrintWriter writer = resp.getWriter();
        writer.write(jsonPart1);
        writer.write(jsonPart2);
        // 自动生成正确的context-length

        // 要求writer.flush代替resp.flushBuffer()
        writer.flush();
        //resp.flushBuffer();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 要求setHeader 可以设置字段值
        resp.setHeader("Content-Type", "application/json; charset=UTF-8");
        String jsonStr = "{ \"message\": \"POST, JSON!\", \"code\":\"0\" }";
        // 要求自动生成contentLength
        // resp.setContentLength(jsonStr.length());
        // json串, 要求可以用print/println
        ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.println(jsonStr);

        // 要求writer.flush代替resp.flushBuffer()
        outputStream.flush();
        // resp.flushBuffer();
    }
}
