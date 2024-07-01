package com.github.ljl.jerrymouse.support.context;

import com.github.ljl.jerrymouse.exception.JerryMouseException;
import com.github.ljl.jerrymouse.support.filter.DefaultFilterManager;
import com.github.ljl.jerrymouse.support.filter.IFilterManager;
import com.github.ljl.jerrymouse.support.listener.DefaultListenerManager;
import com.github.ljl.jerrymouse.support.listener.IListenerManager;
import com.github.ljl.jerrymouse.support.servlet.DefaultServletManager;
import com.github.ljl.jerrymouse.support.servlet.IServletManager;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-24 10:39
 **/

public class JerryMouseAppContext implements ServletContext, IAppContext {
    private static Logger logger = LoggerFactory.getLogger(JerryMouseAppContext.class);

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    private Map<String, String> initContextParams = new HashMap<>();


    @Setter
    @Getter
    private String baseDir;

    @Getter
    private final String urlPrefix;

    @Getter
    private IListenerManager listenerManager = new DefaultListenerManager();

    @Getter
    private IFilterManager filterManager = new DefaultFilterManager();

    @Getter
    private IServletManager servletManager = new DefaultServletManager();

    private final ClassLoader classLoader;
    public JerryMouseAppContext(String baseDir, String urlPrefix, ClassLoader classLoader) {
        this.baseDir = baseDir;
        this.urlPrefix =  urlPrefix;
        this.classLoader = classLoader;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<>(attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public void registerServlet(String urlPattern, Servlet servlet) {
        logger.info("[JerryMouse] register servlet, key = {}, url={}, servlet={}", urlPrefix, urlPattern, servlet.getClass().getName());
        servletManager.register(urlPattern, (HttpServlet) servlet);
    }

    @Override
    public void registerFilter(String urlPattern, Filter filter) {
        logger.info("[JerryMouse] register filter, key = {}, url={}, servlet={}", urlPrefix, urlPattern, filter.getClass().getName());
        filterManager.register(urlPattern, filter);
    }

    @Override
    public void registerListener(EventListener listener) {
        logger.info("[JerryMouse] register listener, key = {}, listener={}", urlPrefix, listener.getClass().getName());
        listenerManager.register("", listener);
    }

    @Override
    public void initializeServletContextListeners() {
        getListenersBySubType(ServletContextListener.class).forEach(listener -> {
            listener.contextInitialized(new ServletContextEvent(this));
        });
    }

    @Override
    public void setAttribute(String s, Object o) {

        List<ServletContextAttributeListener> listeners = getListenersBySubType(ServletContextAttributeListener.class);
        ServletContextAttributeEvent contextEvent = new ServletContextAttributeEvent(this, s, o);

        if (!attributes.containsKey(s)) {
            attributes.put(s, o);
            // add
            listeners.forEach(listener -> listener.attributeAdded(contextEvent));
        } else if (!attributes.get(s).equals(o)){
            attributes.put(s, o);
            // replace
            listeners.forEach(listener -> listener.attributeReplaced(contextEvent));
        } else {
            // do nothing
        }
    }

    @Override
    public void removeAttribute(String s) {
        if(attributes.containsKey(s)) {
            Object value = attributes.get(s);
            attributes.remove(s);
            // remove
            List<ServletContextAttributeListener> listeners = getListenersBySubType(ServletContextAttributeListener.class);
            ServletContextAttributeEvent contextEvent = new ServletContextAttributeEvent(this, s, value);
            listeners.forEach(listener -> listener.attributeRemoved(contextEvent));
        }
    }

    @Override
    public String getServletContextName() {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, String s1) {
        throw new JerryMouseException("[JerryMouse] addServlet(s,s1) Method Not Support");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
        // s = "dispatchServlet"
        // servlet = "DispatchServlet"
        servletManager.register(s, (HttpServlet) servlet);
        ApplicationServletRegistration registration = new ApplicationServletRegistration(s,this, servlet);
        return registration;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
        throw new JerryMouseException("[JerryMouse] Method addServlet(s, aClass) Not Support");
    }

    @Override
    public ServletRegistration.Dynamic addJspFile(String s, String s1) {
        throw new JerryMouseException("[JerryMouse] addJspFile(s, s1) Method Not Support");
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
        throw new JerryMouseException("[JerryMouse] Method createServlet(aClass) Not Support");
    }

    @Override
    public ServletRegistration getServletRegistration(String s) {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, String s1) {
        throw new JerryMouseException("[JerryMouse] Method addFilter(s,s1) Not Support");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
        filterManager.register(s, filter);
        return new ApplicationFilterRegistration(this);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
        throw new JerryMouseException("[JerryMouse] Method add Filter(s, aClass) Not Support");
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
        throw new JerryMouseException("[JerryMouse] Method createFilter(aClass) Not Support");
    }

    @Override
    public FilterRegistration getFilterRegistration(String s) {
        throw new JerryMouseException("[JerryMouse] getFilterRegistration(s) Method Not Support");
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw new JerryMouseException("[JerryMouse] Method () getFilterRegistrationsNot Support");
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        throw new JerryMouseException("[JerryMouse] Method getSessionCookieConfig() Not Support");
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> set) {
        throw new JerryMouseException("[JerryMouse] Method setSessionTrackingModes(set) Not Support");
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        throw new JerryMouseException("[JerryMouse] Method getDefaultSessionTrackingModes() Not Support");
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        throw new JerryMouseException("[JerryMouse] Method getEffectiveSessionTrackingModes() Not Support");
    }

    @Override
    public void addListener(String s) {
        throw new JerryMouseException("[JerryMouse] Method addListener() Not Support");
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        listenerManager.register("", t);
    }

    @Override
    public void addListener(Class<? extends EventListener> aClass) {
        throw new JerryMouseException("[JerryMouse] Method addListener(aClass) Not Support");
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
        throw new JerryMouseException("[JerryMouse] Method (aClass) createListenerNot Support");
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new JerryMouseException("[JerryMouse] Method getJspConfigDescriptor() Not Support");
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public void declareRoles(String... strings) {
        throw new JerryMouseException("[JerryMouse] Method declareRoles(...strings) Not Support");
    }

    @Override
    public String getVirtualServerName() {
        throw new JerryMouseException("[JerryMouse] Method getVirtualServerName() Not Support");
    }

    @Override
    public int getSessionTimeout() {
        throw new JerryMouseException("[JerryMouse] Method getSessionTimeout() Not Support");
    }

    @Override
    public void setSessionTimeout(int i) {
        throw new JerryMouseException("[JerryMouse] Method setSessionTimeout(i) Not Support");
    }

    @Override
    public String getRequestCharacterEncoding() {
        throw new JerryMouseException("[JerryMouse] Method getRequestCharacterEncoding() Not Support");
    }

    @Override
    public void setRequestCharacterEncoding(String s) {
        throw new JerryMouseException("[JerryMouse] Method setRequestCharacterEncoding(s) Not Support");
    }

    @Override
    public String getResponseCharacterEncoding() {
        throw new JerryMouseException("[JerryMouse] Method getResponseCharacterEncoding() Not Support");
    }

    @Override
    public void setResponseCharacterEncoding(String s) {
        throw new JerryMouseException("[JerryMouse] Method setResponseCharacterEncoding(s) Not Support");
    }

    private <T extends EventListener> List<T> getListenersBySubType(Class<T> eventType) {
        return listenerManager.getListeners()
                .stream()
                .filter(listener -> eventType.isInstance(listener))
                .map(listener -> eventType.cast(listener))
                .collect(Collectors.toList());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        if(initContextParams.containsKey(name)) {
            return false;
        }
        initContextParams.put(name, value);
        return true;
    }

    @Override
    public String getInitParameter(String s) {
        return initContextParams.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return  Collections.enumeration(initContextParams.keySet());
    }

    @Override
    public String getContextPath() {
        return urlPrefix;
    }

    @Override
    public ServletContext getContext(String s) {
        return this;
    }

    @Override
    public int getMajorVersion() {
        throw new JerryMouseException("[JerryMouse] Method getMajorVersion() Not Support");
    }

    @Override
    public int getMinorVersion() {
        throw new JerryMouseException("[JerryMouse] Method getMinorVersion() Not Support");
    }

    @Override
    public int getEffectiveMajorVersion() {
        throw new JerryMouseException("[JerryMouse] Method getEffectiveMajorVersion() Not Support");
    }

    @Override
    public int getEffectiveMinorVersion() {
        throw new JerryMouseException("[JerryMouse] Method getEffectiveMinorVersion() Not Support");
    }

    @Override
    public String getMimeType(String s) {
        throw new JerryMouseException("[JerryMouse] Method getMimeType(s) Not Support:" + s);
    }

    @Override
    public Set<String> getResourcePaths(String s) {
        throw new JerryMouseException("[JerryMouse] Method getResourcePaths(s) Not Support:" + s);
    }

    @Override
    public URL getResource(String s) throws MalformedURLException {
        // 生成绝对路径
        String absolutePath = getRealPath(s);

        // 创建文件对象
        File file = new File(absolutePath);

        // 检查文件是否存在
        if (!file.exists()) {
            throw new MalformedURLException("File not found: " + absolutePath);
        }

        // 返回文件的 URL
        return file.toURI().toURL();
    }

    @Override
    public InputStream getResourceAsStream(String s) {
        // 生成绝对路径
        String absolutePath = getRealPath(s);

        // 创建文件对象
        File file = new File(absolutePath);

        // 检查文件是否存在
        if (!file.exists()) {
            logger.error("[JerryMouse] file " + absolutePath + " Not Found !!!");
            throw new JerryMouseException("File not found: " + absolutePath);
        } else {
            logger.info("[JerryMouse] load file " + absolutePath + " as stream!!!");
        }

        // 返回文件输入流
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String s) {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }

    @Override
    public Servlet getServlet(String s) throws ServletException {
        return servletManager.getServlet(s);
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }

    @Override
    public Enumeration<String> getServletNames() {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }

    @Override
    public void log(String s) {
        logger.info("[JerryMouse] " + s);
    }

    @Override
    public void log(Exception e, String s) {
        this.log(s);
        throw new JerryMouseException(e);
    }

    @Override
    public void log(String s, Throwable throwable) {
        this.log(s);
        throw new JerryMouseException(throwable);
    }

    @Override
    public String getRealPath(String s) {
        if (s.startsWith("\\") || s.startsWith("/")) {
            s = s.substring(1);
        }
        if (baseDir.endsWith("\\") || baseDir.endsWith("/")) {
            baseDir = baseDir.substring(0, baseDir.length() - 1);
        }
        String path = baseDir + File.separator + urlPrefix.substring(1) + File.separator + s;
        return path;
    }

    @Override
    public String getServerInfo() {
        throw new JerryMouseException("[JerryMouse] Method Not Support");
    }
}
