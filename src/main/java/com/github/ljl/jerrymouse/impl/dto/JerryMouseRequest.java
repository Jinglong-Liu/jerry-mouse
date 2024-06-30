package com.github.ljl.jerrymouse.impl.dto;

import com.github.ljl.jerrymouse.impl.dto.adaptor.JerryMouseRequestAdaptor;
import com.github.ljl.jerrymouse.support.context.IContextManager;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
import com.github.ljl.jerrymouse.support.context.JerryMouseContextManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:27
 **/


public class JerryMouseRequest implements HttpServletRequest, IRequest{
    private static Logger logger = LoggerFactory.getLogger(JerryMouseRequest.class);

    private IContextManager contextManager = JerryMouseContextManager.get();

    private JerryMouseRequestHelper helper;

    public JerryMouseRequest(String method, String url) {
        this.helper = new JerryMouseRequestHelper(method, url);
    }
    @Override
    public String getUrl() {
        return helper.getUrl();
    }

    @Override
    public void setHeaders(Map<String, String>headers) {
        helper.setHeaders(headers);
    }

    @Override
    public void setQueryParams(Map<String, String[]> queryParams) {
        helper.setQueryParams(queryParams);
    }

    @Override
    public void setInputStream(ServletInputStream inputStream) {
        helper.setInputStream(inputStream);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        helper.setServletContext(servletContext);
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return helper.getDateHeader(s);
    }

    @Override
    public String getHeader(String s) {
        return helper.getHeader(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return helper.getHeaders(s);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return helper.getHeaderNames();
    }

    @Override
    public int getIntHeader(String s) {
        return helper.getIntHeader(s);
    }

    @Override
    public String getMethod() {
        return helper.getMethod();
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
        return helper.getContextPath();
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
    public boolean isUserInRole(String s) {
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
        return helper.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return helper.getRequestURL();
    }

    @Override
    public String getServletPath() {
        return helper.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
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
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }


    @Override
    public Object getAttribute(String s) {
        return helper.getAttribute(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return helper.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return helper.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        helper.setCharacterEncoding(s);
    }

    @Override
    public int getContentLength() {
        return helper.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return helper.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return helper.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return helper.getInputStream();
    }

    @Override
    public String getParameter(String s) {
        return helper.getParameter(s);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return helper.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String s) {
        return helper.getParameterValues(s);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return helper.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return helper.getScheme();
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return helper.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
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
    public void setAttribute(String s, Object o) {
        helper.setAttribute(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        helper.removeAttribute(s);
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
    // s: 路径
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
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
        return contextManager.getServletContext(this);
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
        return helper.getDispatcherType();
    }
}
