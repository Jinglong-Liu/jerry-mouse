package com.github.ljl.jerrymouse.impl;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-23 21:15
 **/

public class JerryMouseFilterChain implements FilterChain {
    private final Servlet servlet;

    private final List<Filter> filters;

    private int pos;

    public JerryMouseFilterChain(Servlet servlet, List<Filter> filters) {
        this.pos = 0;
        this.servlet = servlet;
        this.filters = filters;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if(pos < filters.size()) {
            Filter filter = filters.get(pos);
            pos++;
            filter.doFilter(request, response, this);
        }
        else if (pos == filters.size()) {
            if (Objects.nonNull(servlet)) {
                servlet.service(request, response);
            }
        }
    }
}
