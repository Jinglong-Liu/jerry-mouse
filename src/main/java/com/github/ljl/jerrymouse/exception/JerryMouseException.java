package com.github.ljl.jerrymouse.exception;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 14:38
 **/

public class JerryMouseException extends RuntimeException {
    public JerryMouseException() {
    }
    public JerryMouseException(String message) {
        super(message);
    }
    public JerryMouseException(String message, Throwable cause) {
        super(message, cause);
    }
    public JerryMouseException(Throwable cause) {
        super(cause);
    }
    public JerryMouseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
