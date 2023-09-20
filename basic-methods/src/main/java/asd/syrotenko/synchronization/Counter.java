package asd.syrotenko.synchronization;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
	private int counter = 0;
	private AtomicInteger atomicCounter = new AtomicInteger(0);

	public int getCounter() {
		return counter;
	}

	public int getAtomicCounter() {
		return atomicCounter.get();
	}

	public void resetCounter() {
		counter = 0;
	}

	public void resetAtomicCounter() {
		atomicCounter.set(0);
	}

	public void increase(Integer times) {
		for (int i = 0; i < times; ++i) {
			counter += 1;
		}
	}

	public void decrease(Integer times) {
		for (int i = 0; i < times; ++i) {
			counter -= 1;
		}
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

	public void increaseAtomically(Integer times) {
		for (int i = 0; i < times; ++i) {
			atomicCounter.incrementAndGet();
		}
	}

	public void decreaseAtomically(Integer times) {
		for (int i = 0; i < times; ++i) {
			atomicCounter.decrementAndGet();
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
