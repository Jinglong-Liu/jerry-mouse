package com.github.ljl.jerrymouse.bo;

import lombok.Data;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 09:17
 **/

@Data
public class RequestInfoBo {
    private String url;

    private String method;

    public RequestInfoBo(String url, String method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public String toString() {
        return "RequestInfoBo{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
