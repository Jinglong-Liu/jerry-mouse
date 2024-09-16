package com.github.ljl.wheel.jerrymouse.utils;

import java.security.SecureRandom;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-16 10:13
 **/

public class SessionUtils {
    private static final SecureRandom random = new SecureRandom();

    public static String generateSessionId() {
        long timestamp = System.currentTimeMillis();
        int rand = random.nextInt();
        String sessionId = Long.toString(timestamp) + Integer.toHexString(rand);
        return sessionId;
    }
}
