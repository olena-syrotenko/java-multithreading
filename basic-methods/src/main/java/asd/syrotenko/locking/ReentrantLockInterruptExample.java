package asd.syrotenko.locking;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockInterruptExample {

	public static void main(String[] args) throws InterruptedException {
		LockService lockService = new LockService();

		ExecutorService fixedPool = Executors.newFixedThreadPool(2);

		// add infinity tasks
		Future<?> lockTask = fixedPool.submit(lockService::lock);
		Future<?> lockWithInterruptionTask = fixedPool.submit(lockService::lockWithInterruption);

		// add task to display something
		fixedPool.submit(() -> System.out.println("Display some information task"));

		// waiting 1 second and interrupt infinity threads if we have other tasks
		Thread.sleep(1000);
		if (!lockTask.isDone()) {
			lockTask.cancel(true);
		}

		// as we use lockInterruptibly then thread was interrupted and display task was executed
		if (!lockWithInterruptionTask.isDone()) {
			lockWithInterruptionTask.cancel(true);
		}
	}

	private static class LockService {
		private final ReentrantLock reentrantLock = new ReentrantLock();

		public void lock() {
			reentrantLock.lock();
			try {
				while (true) {
				}
			} finally {
				reentrantLock.unlock();
			}
		}

		public void lockWithInterruption() {
			try {
				reentrantLock.lockInterruptibly();

				try {
					while (true) {
					}
				} finally {
					reentrantLock.unlock();
				}
			} catch (InterruptedException e) {
				System.out.println("Thread with infinity lock was interrupted");
			}
		}
	}
}
