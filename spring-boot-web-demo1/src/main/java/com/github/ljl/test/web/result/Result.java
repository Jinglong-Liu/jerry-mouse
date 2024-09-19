package com.github.ljl.test.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-19 09:07
 **/

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 0 成功，其他失败
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;

    public Result<T> msg(String msg) {
        this.msg = msg;
        return this;
    }


    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 0;
        result.msg = "success";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 0;
        result.msg = "success";
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 1;
        return result;
    }
}

