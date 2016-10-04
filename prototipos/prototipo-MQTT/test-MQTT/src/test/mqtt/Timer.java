package test.mqtt;

final class Timer {

	private long start;

	public Timer() {
		reset();
	}

	public long elapsed() {
		return System.currentTimeMillis() - start;
	}

	public boolean hasElapsed(int time) {
		return elapsed() >= time;
	}

	public void reset() {
		start = System.currentTimeMillis();
	}
}
