package hudson.views.test;

import hudson.views.Clock;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FixedClock extends Clock {
    private long millis;

    public FixedClock(String date) throws ParseException {
        this.millis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
    }

    @Override
    public long currentTimeMillis() {
        return millis;
    }
}

