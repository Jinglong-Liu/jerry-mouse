package com.github.ljl.wheel.jerrymouse.support.context;

import com.github.ljl.wheel.jerrymouse.exception.MethodNotSupportException;
import com.github.ljl.wheel.jerrymouse.support.manager.ServletManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.filter.FilterManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.listener.ListenerManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.registration.ApplicationFilterRegistration;
import com.github.ljl.wheel.jerrymouse.support.servlet.registration.ApplicationServletRegistration;
import com.github.ljl.wheel.jerrymouse.support.servlet.session.HttpSessionImpl;
import com.github.ljl.wheel.jerrymouse.support.servlet.session.SessionManager;
import com.github.ljl.wheel.jerrymouse.support.servlet.session.SessionTaskManager;
import com.github.ljl.wheel.jerrymouse.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.*;
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

    @Setter
    @Getter
    private String sessionIdFieldName = "JSESSIONID";

    private int sessionTimeoutMinute = -1;

    /**
     * 暂时只支持COOKIE方式
     */
    private Set<SessionTrackingMode> sessionTrackingModes = EnumSet.of(SessionTrackingMode.COOKIE);

    private final FilterManager filterManager = new FilterManager();

    private final ServletManager servletManager = new ServletManager();

    private final ListenerManager listenerManager = new ListenerManager();

    private final SessionManager sessionManager = new SessionManager();

    private final SessionTaskManager sessionTaskManager = SessionTaskManager.get();

    private final Map<String, String> initParameterMap = new HashMap<>();

    private final Map<String, Object> attributesMap = new ConcurrentHashMap<>();

    public ApplicationContext(String appName, ClassLoader classLoader) {
        this.appName = appName;
        this.classLoader = classLoader;
    }

    @Override
    public String getContextPath() {
        return StringUtils.isEmpty(appName) ? null: appName;
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
        return classLoader.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return classLoader.getResourceAsStream(path);
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

    public String getServletPath(String uri) {
        Object[] pair = servletManager.getServletAndPath(uri);
        return (String) pair[1];
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

    public Collection<String> getServletPatterns(Servlet servlet) {
        return Collections.list(servletManager.getServletPatterns(servlet));
    }
    public Collection<String> getServletPatterns() {
        return Collections.list(servletManager.getServletPatterns());
    }
    @Override
    public void log(String msg) {
        logger.info(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        logger.error(msg, exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public String getRealPath(String path) {
        throw new MethodNotSupportException("getRealPath not support in servletContext");
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
        throw new MethodNotSupportException("addServlet(servletName, className) Method Not Support");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        // servletName = "dispatchServlet"
        // servlet = "DispatchServlet"
        servletManager.register(servletName, (HttpServlet) servlet);
        ApplicationServletRegistration registration = new ApplicationServletRegistration(this, servletName, servlet);
        return registration;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        throw new MethodNotSupportException("addServlet(servletName, className) Method Not Support");
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
        throw new MethodNotSupportException("addFilter(filterName, className) Method Not Support");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        filterManager.register(filterName, filter);
        return new ApplicationFilterRegistration(this, filterName, filter);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        throw new MethodNotSupportException("addFilter(filterName, filterClass) Method Not Support");
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
        if (Objects.isNull(sessionTrackingModes) || sessionTrackingModes.isEmpty()) {
            this.sessionTrackingModes.clear();
            return;
        }
        if (sessionTrackingModes.size() > 1 || !sessionTrackingModes.contains(SessionTrackingMode.COOKIE)) {
            logger.error("Not support other sessionTrackMode: only COOKIE in this version");
            throw new MethodNotSupportException("Not support other sessionTrackMode: only COOKIE in this version");
        }
        this.sessionTrackingModes = sessionTrackingModes;
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return EnumSet.of(SessionTrackingMode.COOKIE);
    }

    /**
     * web.xml配置 或者 set进去
     * @return
     */
    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return sessionTrackingModes;
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
        return sessionTimeoutMinute;
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeoutMinute = sessionTimeout;
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
        HttpSessionImpl session = (HttpSessionImpl) ((HttpServletRequest)request).getSession(false);
        if (Objects.nonNull(session)) {
            session.updateLastAccessedTime();
        }
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

    // HttpSessionListener
    public void sessionCreated(HttpSessionImpl session) {
        // 加入
        sessionManager.addSession(session);
        sessionTaskManager.sessionCreated(session);
        this.getListenersByType(HttpSessionListener.class).forEach(listener -> {
            listener.sessionCreated(new HttpSessionBindingEvent(session, session.getId()));
        });
    }

    public void sessionDestroyed(HttpSessionImpl session) {
        sessionManager.removeSession(session.getId());
        this.getListenersByType(HttpSessionListener.class).forEach(listener -> {
            listener.sessionDestroyed(new HttpSessionBindingEvent(session, session.getId()));
        });
    }

    // HttpSessionAttributeListener
    public void sessionAttributeAdded(HttpSession session, String name, Object value) {
        this.getListenersByType(HttpSessionAttributeListener.class).forEach(listener -> {
            listener.attributeAdded(new HttpSessionBindingEvent(session, name, value));
        });
    }

    public void sessionAttributeRemoved(HttpSession session, String name, Object value) {
        this.getListenersByType(HttpSessionAttributeListener.class).forEach(listener -> {
            listener.attributeRemoved(new HttpSessionBindingEvent(session, name, value));
        });
    }

    public void sessionAttributeReplaced(HttpSession session, String name, Object value) {
        this.getListenersByType(HttpSessionAttributeListener.class).forEach(listener -> {
            listener.attributeReplaced(new HttpSessionBindingEvent(session, name, value));
        });
    }

    public HttpSession findSession(String sessionId) {
        return sessionManager.getSession(sessionId);
    }

    public void changeSessionId(HttpSession session, String oldId) {
        sessionManager.changeSessionId(session, oldId);
    }
}
