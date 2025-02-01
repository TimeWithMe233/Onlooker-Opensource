package dev.onlooker.utils.time;


public class WelcomrUtil {
	public WelcomrUtil(){
		this.lastMS = this.getCurrentMS();
	}
	private long lastMS;

	private long getCurrentMS() {
		return System.nanoTime() / 1000000L;
	}

	public boolean check(float milliseconds) {
		return getTime() >= milliseconds;
	}

	public boolean hasReached(double milliseconds) {
		return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
	}

	public void reset() {
		this.lastMS = this.getCurrentMS();
	}

	public boolean delay(float milliSec) {
		return (float) (this.getTime() - this.lastMS) >= milliSec;
	}

	public long getTime() {
		return System.nanoTime() / 1000000L;
	}
}
