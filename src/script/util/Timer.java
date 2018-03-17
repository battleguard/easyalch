package script.util;

import java.util.concurrent.TimeUnit;

public class Timer {
    private long _startTime;
    private long _endIn;

    public Timer(long endTime) {
        setEndIn(endTime);
    }

    public void setEndIn(long endIn) {

        _endIn = endIn;
        reset();
    }

    public String toElapsedString() {
        return prettyTime(getElapsed());
    }

    public long getElapsed() {
        return System.currentTimeMillis() - _startTime;
    }

    public boolean isRunning() {
        return System.currentTimeMillis() < _startTime + _endIn;
    }

    public void reset() {
        _startTime = System.currentTimeMillis();
    }

    public static String prettyTime(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
