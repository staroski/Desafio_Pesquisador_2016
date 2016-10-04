package test.mqtt;

final class Timer {

	private long start;

	public Timer() {
		reset();
	}

	public long elapsed() {
		return System.currentTimeMillis() - start;
	}

	public void reset() {
		start = System.currentTimeMillis();
	}

	public void waitMilis(int interval) {
		long timeToWait = interval - elapsed();
		if (timeToWait > 0) {
			synchronized (this) {
				try {
					wait(timeToWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		reset();
	}
}
