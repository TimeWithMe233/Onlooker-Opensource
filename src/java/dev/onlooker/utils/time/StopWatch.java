package dev.onlooker.utils.time;

public class StopWatch {
    private long millis;

    public StopWatch() {
        this.reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.millis;
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }
}

