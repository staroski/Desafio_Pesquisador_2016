package test.mqtt;

final class Memory {

	private final Runtime jvm = Runtime.getRuntime();

	public long free() {
		return jvm.freeMemory();
	}

	public long total() {
		return jvm.totalMemory();
	}

	public long used() {
		return total() - free();
	}
}
