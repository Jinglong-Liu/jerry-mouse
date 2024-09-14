package com.github.ljl.wheel.jerrymouse.server.nio.handler;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 13:34
 **/

public interface SocketWriter {
    void write(byte[] data);
    void write(String data);
}
