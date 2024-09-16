package com.github.ljl.wheel.jerrymouse.support.servlet.request;

import com.github.ljl.wheel.jerrymouse.support.context.ApplicationContext;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 13:23
 **/

public class RequestImpl implements HttpServletRequest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private RequestData requestData;

    @Setter
    private ServletContext servletContext;

    public RequestImpl(String message) {
        this.requestData = new  RequestData(this, message);
    }
    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return requestData.getCookies();
    }

    @Override
    public long getDateHeader(String name) {
        return requestData.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return requestData.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return requestData.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return requestData.getHeaderNames();
    }

    @Override
    public int getIntHeader(String name) {
        return requestData.getIntHeader(name);
    }

    @Override
    public String getMethod() {
        return requestData.getMethod();
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return servletContext.getContextPath();
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return requestData.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return requestData.getSession(create);
    }

    @Override
    public HttpSession getSession() {
        return requestData.getSession();
    }

    @Override
    public String changeSessionId() {
        return requestData.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return requestData.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return requestData.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return requestData.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return requestData.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        requestData.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {
        return requestData.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return requestData.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return requestData.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() {
        return requestData.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        return requestData.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return requestData.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return requestData.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return requestData.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return requestData.getProtocol();
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {
        requestData.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        requestData.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        init();
    }
    public void init() {
        ApplicationContext applicationContext = (ApplicationContext) servletContext;
        applicationContext.initializeRequest(this);
    }

    public void destroy() {
        ApplicationContext applicationContext = (ApplicationContext) this.servletContext;
        applicationContext.destroyRequest(this);
    }
}
