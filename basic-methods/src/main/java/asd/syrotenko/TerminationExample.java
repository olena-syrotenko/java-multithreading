package asd.syrotenko;

import asd.syrotenko.tasks.InfiniteTask;
import asd.syrotenko.tasks.LongCalculationTask;
import asd.syrotenko.tasks.SleepTask;

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
}
