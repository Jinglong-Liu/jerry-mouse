package com.github.ljl.jerrymouse.impl.dto;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IRequest extends HttpServletRequest {
    /**
     * 获取请求地址
     * @return url
     */
    String getUrl();

    void setHeaders(Map<String, String> headers);

    void setQueryParams(Map<String, String[]> queryParams);

    void setInputStream(ServletInputStream inputStream);

    void setServletContext(ServletContext servletContext);
}
