package com.github.ljl.jerrymouse.constant;

public enum JerryMouseHttpMethodType {
    GET("GET", "GET 请求"),
    POST("POST", "POST 请求"),
    HEAD("HEAD", "HEAD 请求"),
    PUT("PUT", "PUT 请求"),
    DELETE("DELETE", "DELETE 请求"),
    OPTIONS("OPTIONS", "OPTIONS 请求"),
    TRACE("TRACE", "TRACE 请求"),
    CONNECT("CONNECT", "CONNECT 请求"),
    ;
    private final String code;
    private final String desc;

    JerryMouseHttpMethodType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
