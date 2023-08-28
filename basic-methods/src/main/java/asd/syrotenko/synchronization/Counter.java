package asd.syrotenko.synchronization;

public class Counter {
	private int counter = 0;

	public int getCounter() {
		return counter;
	}

	public void resetCounter() {
		counter = 0;
	}

	public Integer increase(Integer times) throws InterruptedException {
		Thread.sleep(100);
		for (int i = 0; i < times; ++i) {
			counter += 1;
		}
		return counter;
	}

	public Integer decrease(Integer times) throws InterruptedException {
		Thread.sleep(100);
		for (int i = 0; i < times; ++i) {
			counter -= 1;
		}
		return counter;
	}

	public synchronized void increaseSynchronized(Integer times) {
		for (int i = 0; i < times; ++i) {
			counter += 1;
		}
	}

	public synchronized void decreaseSynchronized(Integer times) {
		for (int i = 0; i < times; ++i) {
			counter -= 1;
		}
	}

	public synchronized Integer increaseWithSaveHistory(Integer times) throws InterruptedException {
		System.out.println("Save in your story data about input parameters...");
		Thread.sleep(1000);

		for (int i = 0; i < times; ++i) {
			counter += 1;
		}

		return counter;
	}

	public Integer increaseWithSaveHistorySynchronizedBlock(Integer times) throws InterruptedException {
		System.out.println("Save in your story data about input parameters...");
		Thread.sleep(1000);

		synchronized (this) {
			for (int i = 0; i < times; ++i) {
				counter += 1;
			}
			return counter;
		}
	}

	public synchronized void doSomething() {
		System.out.println("Start of synchronized method with thread " + Thread.currentThread().getName());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Synchronized method execution was interrupted");
		}
		System.out.println("End of synchronized method with thread " + Thread.currentThread().getName());
	}

	public static synchronized void doSomethingStatic() {
		System.out.println("Start of static synchronized method with thread " + Thread.currentThread().getName());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Static synchronized method execution was interrupted");
		}
		System.out.println("End of static synchronized method with thread " + Thread.currentThread().getName());
	}

}
