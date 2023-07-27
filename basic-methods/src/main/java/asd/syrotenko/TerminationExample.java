package asd.syrotenko;

import java.math.BigInteger;

public class TerminationExample {

	public static void main(String[] args) {
		Thread sleepThread = new Thread(new SleepTask());
		sleepThread.start();
		sleepThread.interrupt();

		Thread infiniteThread = new Thread(new InfiniteTask());
		infiniteThread.start();
		infiniteThread.interrupt();

		Thread longCalculationThread = new Thread(new LongCalculationTask());
		longCalculationThread.start();
		longCalculationThread.interrupt();

		if (longCalculationThread.isInterrupted()) {
			System.out.println("Interruption flag for long calculation thread was set but calculation is continuing");
		}

	}

	public static class SleepTask implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("SleepTask was interrupted. InterruptedException was caught");
			}
		}
	}

	public static class InfiniteTask implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("InfiniteTask was interrupted. Interruption flag was checked");
					return;
				}
			}
		}
	}

	public static class LongCalculationTask implements Runnable {
		@Override
		public void run() {
			BigInteger number = new BigInteger("1567892");
			BigInteger result = BigInteger.ONE;
			for (int i = 0; i <= 10000; ++i) {
				result = result.multiply(number);
			}
			System.out.println("After long calculation result is " + result);
		}
	}
}
