package com.github.ljl.wheel.jerrymouse.support.servlet.session;

import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.wheel.jerrymouse.utils.SessionUtils;
import com.github.ljl.wheel.jerrymouse.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-16 10:07
 **/

public class HttpSessionImpl implements HttpSession {
    private final Logger logger = LoggerFactory.getLogger(HttpSessionImpl.class);

    private int maxInactiveIntervalSeconds;

    private long createTimeMillis;

    private long lastAccessedTime;

    private long expireTime;

    private volatile boolean isValid = true;

    private boolean isNew;

    private HttpServletRequest request;

    private String sessionId;

    private final SessionTaskManager sessionTaskManager = SessionTaskManager.get();

    private ApplicationContext applicationContext;

    private HttpSessionData sessionData;

    public HttpSessionImpl() {
    }

    public void init(HttpServletRequest request) {
        this.isNew = true;
        this.request = request;
        this.createTimeMillis = System.currentTimeMillis();
        this.lastAccessedTime = System.currentTimeMillis();
        this.expireTime = -1;
        this.sessionId = SessionUtils.generateSessionId();
        this.applicationContext = (ApplicationContext) request.getServletContext();
        this.sessionData = new HttpSessionData(this, applicationContext);
        int defaultTimeout = applicationContext.getSessionTimeout();
        if (defaultTimeout > 0) {
            this.expireTime = createTimeMillis + defaultTimeout * 60 * 1000L;
        }
        // 放最后执行
        this.applicationContext.sessionCreated(this);
    }

    @SessionValidCheck
    @Override
    public long getCreationTime() {
        return createTimeMillis;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    /**
     * HttpSession Returns the last time the client sent a request
     * associated with this session,
     * marked by the time the container received the request.
     * Actions that your application takes, such as getting or setting a value
     * associated with the session, do not affect the access time.
     *
     * @return lastAccessedTime
     */
    @SessionValidCheck
    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return applicationContext;
    }

    /**
     * @param interval seconds
     */
    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveIntervalSeconds = interval;
        this.expireTime = this.createTimeMillis + interval * 1000L;
        sessionTaskManager.sessionTimeReset(this);
    }

    /**
     *  <= 0: never timeout
     * @return
     */
    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveIntervalSeconds;
    }

    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @SessionValidCheck
    @Override
    public Object getAttribute(String name) {
        return sessionData.getAttribute(name);
    }

    @SessionValidCheck
    @Override
    public Object getValue(String name) {
        return sessionData.getAttribute(name);
    }

    @SessionValidCheck
    @Override
    public Enumeration<String> getAttributeNames() {
        return sessionData.getAttributeNames();
    }

    @SessionValidCheck
    @Deprecated
    @Override
    public String[] getValueNames() {
        return sessionData.getValueNames();
    }

    @SessionValidCheck
    @Override
    public void setAttribute(String name, Object value) {
        sessionData.setAttribute(name, value);
    }

    @SessionValidCheck
    @Deprecated
    @Override
    public void putValue(String name, Object value) {
        sessionData.setAttribute(name, value);
    }

    @SessionValidCheck
    @Override
    public void removeAttribute(String name) {
        sessionData.removeAttribute(name);
    }

    @SessionValidCheck
    @Deprecated
    @Override
    public void removeValue(String name) {
        sessionData.removeAttribute(name);
    }

    @Override
    public void invalidate() {
        if (isValid) {
            isValid = false;
            sessionTaskManager.cancelTask(this);
            final ApplicationContext applicationContext = (ApplicationContext) request.getServletContext();
            applicationContext.sessionDestroyed(this);
        } else {
            throw new IllegalStateException("Session has invalid");
        }
    }

    @SessionValidCheck
    @Override
    public boolean isNew() {
        return isNew;
    }

    public void updateLastAccessedTime() {
        this.lastAccessedTime = System.currentTimeMillis();
        this.isNew = false;
        logger.info("session {} update last accessed time", getId());
        logger.info("currentTime = {}, expireTime = {}", TimeUtils.format(this.lastAccessedTime), this.expireTime > 0 ? TimeUtils.format(this.expireTime) : -1);
    }

    public void setId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isValid() {
        return isValid;
    }

    /**
     * 计算剩余时间，给manager使用, 启动Timer任务
     *  = 0: 超时
     *  < 0: 无时间限制
     *  > 0: 剩余时间
     * @return
     */
    public long calculateTimeout() {
        if (expireTime <= 0) {
            return -1;
        }
        long currentTime = System.currentTimeMillis();
        if (expireTime > currentTime) {
            // > 0，剩余时间
            return expireTime - currentTime;
        }
        // 超时
        return 0;
    }
}
