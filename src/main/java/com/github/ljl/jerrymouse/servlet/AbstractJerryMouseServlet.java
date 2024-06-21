package com.github.ljl.jerrymouse.servlet;

import com.github.ljl.jerrymouse.constant.JerryMouseHttpMethodType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 17:58
 **/

public abstract class AbstractJerryMouseServlet extends HttpServlet {

    protected abstract void doGet(HttpServletRequest req, HttpServletResponse resp);

    protected  abstract void doPost(HttpServletRequest req, HttpServletResponse resp);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp){
        if(JerryMouseHttpMethodType.GET.getCode().equalsIgnoreCase(req.getMethod())) {
            this.doGet(req, resp);
            return;
        }

        this.doPost(req, resp);
    }
}
