package com.github.ljl.wheel.jerrymouse.support.servlet.filter;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-15 13:36
 **/

public class FilterChainImpl implements FilterChain {
    private final List<Filter> filterList;

    private final Servlet servlet;

    private int index;

    public FilterChainImpl(List<Filter> filterList, Servlet servlet) {
        this.filterList = filterList;
        this.servlet = servlet;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (index < filterList.size()) {
            Filter filter = filterList.get(index);
            index++;
            filter.doFilter(request, response, this);
        } else if (index == filterList.size()){
            if (Objects.nonNull(servlet)) {
                servlet.service(request, response);
            }
        }
    }
}
