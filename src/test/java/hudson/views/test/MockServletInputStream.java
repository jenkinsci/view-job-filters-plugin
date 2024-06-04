package hudson.views.test;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {

    private final InputStream in;

    public MockServletInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
