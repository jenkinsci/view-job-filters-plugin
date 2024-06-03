package hudson.views.test;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.BindInterceptor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebApp;
import org.kohsuke.stapler.bind.BoundObjectTable;
import org.kohsuke.stapler.lang.Klass;

public class MockStaplerRequest implements StaplerRequest {
    private final String contentType;
    private final Map<String, String> parameters;
    private final ServletInputStream is;

    public MockStaplerRequest(String contentType, Map<String, String> parameters, ServletInputStream is) {
        this.contentType = Objects.requireNonNull(contentType);
        this.parameters = Objects.requireNonNull(parameters);
        this.is = Objects.requireNonNull(is);
    }

    @Override
    public Stapler getStapler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebApp getWebApp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRestOfPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOriginalRestOfPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterEncoding(String env) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletInputStream getInputStream() {
        return is;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[] {parameters.get(name)};
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BufferedReader getReader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestURIWithQueryString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StringBuffer getRequestURLWithQueryString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getView(Object it, String viewName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getView(Class clazz, String viewName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getView(Klass<?> clazz, String viewName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRootPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getReferer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Ancestor> getAncestors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ancestor findAncestor(Class type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T findAncestorObject(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ancestor findAncestor(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasParameter(String name) {
        return parameters.containsKey(name);
    }

    @Override
    public String getOriginalRequestURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkIfModified(Date timestampOfResource, StaplerResponse rsp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkIfModified(Calendar timestampOfResource, StaplerResponse rsp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp, long expiration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bindParameters(Object bean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bindParameters(Object bean, String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> bindParametersToList(Class<T> type, String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T bindParameters(Class<T> type, String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T bindParameters(Class<T> type, String prefix, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T bindJSON(Class<T> type, JSONObject src) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T bindJSON(Type genericType, Class<T> erasure, Object json) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bindJSON(Object bean, JSONObject src) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> bindJSONToList(Class<T> type, Object src) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BindInterceptor getBindInterceptor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BindInterceptor setBindListener(BindInterceptor bindListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BindInterceptor setBindInterceptpr(BindInterceptor bindListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BindInterceptor setBindInterceptor(BindInterceptor bindListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject getSubmittedForm() {
        throw new UnsupportedOperationException();
    }

    @Override
    public org.apache.commons.fileupload2.core.FileItem getFileItem2(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileItem getFileItem(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isJavaScriptProxyCall() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BoundObjectTable getBoundObjectTable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createJavaScriptProxy(Object toBeExported) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RenderOnDemandParameters createJavaScriptProxyParameters(Object toBeExported) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void login(String username, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Part> getParts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Part getPart(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
        throw new UnsupportedOperationException();
    }
}
