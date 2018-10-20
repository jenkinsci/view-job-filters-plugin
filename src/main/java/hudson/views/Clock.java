package hudson.views;

import java.text.SimpleDateFormat;

public abstract class Clock {

    public static final Clock SYSTEM_CLOCK = new Clock() {
        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    };

    private static Clock instance = SYSTEM_CLOCK;

    public static Clock getInstance() {
        return instance;
    }

    public static void setInstance(Clock clock) {
        instance = clock;
    }

    public abstract long currentTimeMillis();
}
