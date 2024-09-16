package com.github.ljl.wheel.jerrymouse.support.servlet.session;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-16 10:09
 **/

public class SessionManager {
    private Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    public void addSession(HttpSession session) {
        sessionMap.put(session.getId(), session);
    }
    public void changeSessionId(HttpSession session, String oldId) {
        sessionMap.remove(oldId);
        sessionMap.put(session.getId(), session);
    }
    public HttpSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
}
