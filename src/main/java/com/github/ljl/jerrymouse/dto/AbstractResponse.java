package com.github.ljl.jerrymouse.dto;

import com.github.ljl.jerrymouse.dto.adaptor.JerryMouseResponseAdaptor;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-22 09:31
 **/

public abstract class AbstractResponse extends JerryMouseResponseAdaptor {
    public abstract void write(String text, String charset);
}
