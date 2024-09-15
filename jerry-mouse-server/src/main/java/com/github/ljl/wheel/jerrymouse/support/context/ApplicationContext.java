package com.github.ljl.wheel.jerrymouse.support.context;

import com.github.ljl.wheel.jerrymouse.support.manager.ServletManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.filter.FilterManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.listener.ListenerManager;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * @create: 2024-09-14 13:41
 **/

public class ApplicationContext implements ServletContext {

    private static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    private ClassLoader classLoader;

    private String appName;

    private Set<Servlet> servlets = new HashSet<>();

    @Setter
    private HttpServletRequest request;

    @Setter
    private HttpServletResponse response;

    private final FilterManager filterManager = new FilterManager();

    private final ServletManager servletManager = new ServletManager();

    private final ListenerManager listenerManager = new ListenerManager();

    private final Map<String, String> initParameterMap = new HashMap<>();

    private final Map<String, Object> attributesMap = new ConcurrentHashMap<>();

    public ApplicationContext(String appName, ClassLoader classLoader) {
        this.appName = appName;
        this.classLoader = classLoader;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public ServletContext getContext(String uripath) {
        return null;
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        return null;
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        return null;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        return servletManager.getServlet(name);
    }

    @Deprecated
    @Override
    public Enumeration<Servlet> getServlets() {
        logger.error("getServlets() is Deprecated in this version");
        return Collections.emptyEnumeration();
    }

    @Deprecated
    @Override
    public Enumeration<String> getServletNames() {
        logger.error("getServletNames() is Deprecated in this version");
        return Collections.emptyEnumeration();
    }

    @Override
    public void log(String msg) {

    }

    @Override
    public void log(Exception exception, String msg) {

    }

    @Override
    public void log(String message, Throwable throwable) {

    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public String getServerInfo() {
        return null;
    }

    @Override
    public String getInitParameter(String name) {
        return initParameterMap.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameterMap.keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        synchronized (initParameterMap) {
            if (initParameterMap.containsKey(name)) {
                return false;
            }
            initParameterMap.put(name, value);
            return true;
        }
    }

    @Override
    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributesMap.keySet());
    }

    @Override
    public void setAttribute(String name, Object object) {
        List<ServletContextAttributeListener> listeners = getListenersByType(ServletContextAttributeListener.class);
        ServletContextAttributeEvent contextEvent = new ServletContextAttributeEvent(this, name, object);

        synchronized (attributesMap) {
            if (!attributesMap.containsKey(name)) {
                attributesMap.put(name, object);
                // add
                listeners.forEach(listener -> listener.attributeAdded(contextEvent));
            } else if (!attributesMap.get(name).equals(object)){
                attributesMap.put(name, object);
                // replace
                listeners.forEach(listener -> listener.attributeReplaced(contextEvent));
            } else {
                // do nothing
            }
        }
    }

    @Override
    public void removeAttribute(String name) {
        if(attributesMap.containsKey(name)) {
            synchronized (attributesMap) {
                if (attributesMap.containsKey(name)) {
                    Object value = attributesMap.get(name);
                    attributesMap.remove(name);
                    // remove
                    List<ServletContextAttributeListener> listeners = getListenersByType(ServletContextAttributeListener.class);
                    ServletContextAttributeEvent contextEvent = new ServletContextAttributeEvent(this, name, value);
                    listeners.forEach(listener -> listener.attributeRemoved(contextEvent));
                }
            }
        }
    }

    @Override
    public String getServletContextName() {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String className) {
        try {
            Class<? extends EventListener> listenerClass = (Class<? extends EventListener>) classLoader.loadClass(className);
            addListener(listenerClass);
        } catch (ClassNotFoundException | ClassCastException e) {
            logger.error("load listener class error, className = {}", className);
            e.printStackTrace();
        }
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        listenerManager.register(t);
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        try {
            EventListener listener = listenerClass.newInstance();
            addListener(listener);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("create listener object error, className = {}", listenerClass.getName());
            e.printStackTrace();
        }
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        try {
            EventListener listener = clazz.newInstance();
            addListener(listener);
            return (T) listener;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void declareRoles(String... roleNames) {

    }

    @Override
    public String getVirtualServerName() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {

    }

    @Override
    public String getRequestCharacterEncoding() {
        return null;
    }

    @Override
    public void setRequestCharacterEncoding(String encoding) {

    }

    @Override
    public String getResponseCharacterEncoding() {
        return null;
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {

    }

    public HttpServlet getServletByURI(String uri) {
        return servletManager.getServlet(uri);
    }
    public void registerServlet(String uri, HttpServlet httpServlet) {
        servletManager.register(uri, httpServlet);
    }

    public List<Filter> getMatchFilters(String urlPattern) {
        return filterManager.getMatchFilters(urlPattern);
    }
    public void registerFilter(String urlPattren, Filter filter) {
        filterManager.register(urlPattren, filter);
    }

    public void initializeServletContextListeners() {
        this.getListenersByType(ServletContextListener.class).forEach(listener -> {
            listener.contextInitialized(new ServletContextEvent(this));
        });
    }

    public void initializeRequest(ServletRequest request) {
        this.getListenersByType(ServletRequestListener.class).forEach(listener -> {
            listener.requestInitialized(new ServletRequestEvent(this, request));
        });
    }

    public void destroyRequest(ServletRequest request) {
        this.getListenersByType(ServletRequestListener.class).forEach(listener -> {
            listener.requestDestroyed(new ServletRequestEvent(this, request));
        });
    }
    // ServletRequestAttributeListener
    public void requestAttributeAdded(ServletRequest request, String name, Object object) {
        this.getListenersByType(ServletRequestAttributeListener.class).forEach(listener -> {
            listener.attributeAdded(new ServletRequestAttributeEvent(this, request, name, object));
        });
    }
    public void requestAttributeRemoved(ServletRequest request, String name, Object value) {
        this.getListenersByType(ServletRequestAttributeListener.class).forEach(listener -> {
            listener.attributeRemoved(new ServletRequestAttributeEvent(this, request, name, value));
        });
    }
    public void requestAttributeReplaced(ServletRequest request, String name, Object value) {
        this.getListenersByType(ServletRequestAttributeListener.class).forEach(listener -> {
            listener.attributeReplaced(new ServletRequestAttributeEvent(this, request, name, value));
        });
    }
    private <T extends EventListener> List<T> getListenersByType(Class<T> eventType) {
        return listenerManager.getListeners()
                .stream()
                .filter(eventType::isInstance)
                .map(eventType::cast)
                .collect(Collectors.toList());
    }
}
