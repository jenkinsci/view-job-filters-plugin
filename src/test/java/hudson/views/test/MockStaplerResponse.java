package hudson.views.test;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.json.JsonConfig;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.export.Flavor;

public class MockStaplerResponse implements StaplerResponse2 {

    private int sc;

    @Override
    public void forward(Object it, String url, StaplerRequest2 request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forwardToPreviousPage(StaplerRequest2 request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRedirect2(@NonNull String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRedirect(int statusCore, @NonNull String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveFile(StaplerRequest2 request, URL res) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveFile(StaplerRequest2 request, URL res, long expiration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveLocalizedFile(StaplerRequest2 request, URL res) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveLocalizedFile(StaplerRequest2 request, URL res, long expiration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveFile(
            StaplerRequest2 req,
            InputStream data,
            long lastModified,
            long expiration,
            long contentLength,
            String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveFile(
            StaplerRequest2 req,
            InputStream data,
            long lastModified,
            long expiration,
            int contentLength,
            String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveFile(
            StaplerRequest2 req, InputStream data, long lastModified, long contentLength, String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveFile(StaplerRequest2 req, InputStream data, long lastModified, int contentLength, String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serveExposedBean(StaplerRequest2 req, Object exposedBean, Flavor flavor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream getCompressedOutputStream(HttpServletRequest req) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer getCompressedWriter(HttpServletRequest req) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int reverseProxyTo(URL url, StaplerRequest2 req) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setJsonConfig(JsonConfig config) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonConfig getJsonConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError(int sc, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError(int sc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRedirect(String location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDateHeader(String name, long date) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDateHeader(String name, long date) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHeader(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIntHeader(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntHeader(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus(int sc) {
        this.sc = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus() {
        return sc;
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentLength(int len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentLengthLong(long len) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentType(String type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }
}
