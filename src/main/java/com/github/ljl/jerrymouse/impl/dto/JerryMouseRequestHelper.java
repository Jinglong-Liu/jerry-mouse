package com.github.ljl.jerrymouse.impl.dto;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.support.context.JerryMouseAppContext;
import com.github.ljl.jerrymouse.utils.JerryMouseHttpUtils;
import com.github.ljl.jerrymouse.utils.JerryMouseRequestUtils;

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
 * @create: 2024-06-27 16:48
 **/

public class JerryMouseRequestHelper implements HttpServletRequest, IRequest{

    /**
     * https://datatracker.ietf.org/doc/html/rfc7231#section-8.3.2
     */
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CHARACTER_ENCODING_NAME = "charset";
    private static final String CONTENT_LENGTH_HEADER_NAME = "Context-Length";
    private static final String CONTENT_ENCODING_HEADER_NAME = "Content-Encoding";

    private String method;

    private String url;

    private ServletContext servletContext;

    private Map<String, String> headerMap = new HashMap<>();

    private Map<String, Object[]> attributesMap = new HashMap<>();

    private Map<String, String[]> parameterMap = new HashMap<>();

    private ServletInputStream inputStream;

    public JerryMouseRequestHelper(String method, String url) {
        this.method = method;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        headerMap.clear();
        headerMap.putAll(headers);
    }

    @Override
    public void setQueryParams(Map<String, String[]> queryParams) {
        parameterMap.clear();
        parameterMap.putAll(queryParams);
    }

    @Override
    public void setInputStream(ServletInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
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
        return Long.valueOf(headerMap.get(s));
    }

    @Override
    public String getHeader(String s) {
        return headerMap.get(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        if (headerMap.containsKey(s)) {
            return Collections.enumeration(Collections.singletonList(headerMap.get(s)));
        }
        return Collections.emptyEnumeration();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headerMap.keySet());
    }

    @Override
    public int getIntHeader(String s) {
        return Integer.valueOf(headerMap.getOrDefault(s, "0"));
    }

    @Override
    public String getMethod() {
        return method;
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
        return JerryMouseRequestUtils.extractPrefix(url);
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        throw new JerryMouseException("getRemoteUser not impl");
    }

    @Override
    public boolean isUserInRole(String s) {
        throw new JerryMouseException("isUserInRole not impl");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new JerryMouseException("getUserPrincipal not impl");
    }

    @Override
    public String getRequestedSessionId() {
        throw new JerryMouseException("getRequestedSessionId not impl");
    }

    @Override
    public String getRequestURI() {
        int endSlashIndex = url.indexOf('?');
        if (endSlashIndex == -1) {
            endSlashIndex = url.length();
        }
        return url.substring(0, endSlashIndex);
    }

    @Override
    public StringBuffer getRequestURL() {
        return JerryMouseRequestUtils.getRequestURL(this);
    }

    @Override
    public String getServletPath() {
        if (url == null || url.isEmpty()) {
            return url;
        }

        String result = new String(url);
        String prefix = ((JerryMouseAppContext)this.getServletContext()).getUrlPrefix();
//        if(result.startsWith(prefix)) {
//            result = result.substring(prefix.length());
//        }
//        if (!result.startsWith("/")) {
//            result = "/" + result;
//        }
//        int slashIndex = result.indexOf('/',1);
//        if (slashIndex != -1) {
//
//        }


        int firstSlashIndex = url.indexOf('/');
        int secondSlashIndex = url.indexOf('/', firstSlashIndex + 1);

        int endSlashIndex = url.indexOf('?', firstSlashIndex + 1);
        if (endSlashIndex == -1) {
            endSlashIndex = url.length();
        }
        if (secondSlashIndex != -1) {
            result =  url.substring(secondSlashIndex + 1, endSlashIndex);
        } else {
            result = url.substring(0, endSlashIndex);
        }
        if (!result.startsWith("/")) {
            result = "/" + result;
        }
        return result;
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
        if (attributesMap.containsKey(s)) {
            return attributesMap.get(s)[0];
        }
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        final String contentType = headerMap.getOrDefault(CONTENT_TYPE_HEADER_NAME, "");
        return JerryMouseHttpUtils.extractCharacterEncoding(contentType);
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        String oldContentType = headerMap.getOrDefault(CONTENT_TYPE_HEADER_NAME, "");
        String contextType = JerryMouseHttpUtils.replaceCharacterEncoding(oldContentType, s);
        headerMap.put(CONTENT_TYPE_HEADER_NAME, contextType);
    }

    @Override
    public int getContentLength() {
        return Integer.valueOf(headerMap.getOrDefault(CONTENT_LENGTH_HEADER_NAME, "0"));
    }

    @Override
    public long getContentLengthLong() {
        return Long.valueOf(headerMap.getOrDefault(CONTENT_LENGTH_HEADER_NAME, "0"));
    }

    // TODO: 自动推断
    @Override
    public String getContentType() {
        return headerMap.get(CONTENT_TYPE_HEADER_NAME);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public String getParameter(String s) {
        return parameterMap.getOrDefault(s, new String[1])[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        return parameterMap.getOrDefault(s, new String[0]);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    // TODO
    @Override
    public String getScheme() {
        return "127.0.0.1";
    }

    @Override
    public String getServerName() {
        return null;
    }

    // TODO
    @Override
    public int getServerPort() {
        return 8080;
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
        attributesMap.put(s, new Object[]{o});
    }

    @Override
    public void removeAttribute(String s) {
        if (attributesMap.containsKey(s)) {
            attributesMap.remove(s);
        }
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
        return DispatcherType.REQUEST;
    }
}
