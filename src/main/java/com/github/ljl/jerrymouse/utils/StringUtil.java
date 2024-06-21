package com.github.ljl.jerrymouse.utils;

import java.util.Objects;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 19:18
 **/

public class StringUtil {
    public static boolean isEmpty(String s) {
        return Objects.isNull(s) || s.length() == 0;
    }
}
