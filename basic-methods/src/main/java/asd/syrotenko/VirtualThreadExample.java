package asd.syrotenko;

import asd.syrotenko.tasks.SleepTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadExample {

	private static final int NUMBER_OF_TASKS = 100_000;

	public static void main(String[] args) {
		long platformThreadsStartTime = System.currentTimeMillis();
		performTasksWithPlatformThreads();
		long platformThreadsEndTime = System.currentTimeMillis();
		// takes about 50s
		System.out.println("Time of executing blocking tasks with platform threads: " + (platformThreadsEndTime - platformThreadsStartTime));

		long virtualThreadsStartTime = System.currentTimeMillis();
		performTasksWithVirtualThreads();
		long virtualThreadsEndTime = System.currentTimeMillis();
		// takes about 6s
		System.out.println("Time of executing blocking tasks with virtual threads: " + (virtualThreadsEndTime - virtualThreadsStartTime));
	}

	private static void performTasksWithPlatformThreads() {
		// create fixed thread pool because current number of task leads to out of memory error
		try (ExecutorService executor = Executors.newFixedThreadPool(10000)) {
			for (int i = 0; i < NUMBER_OF_TASKS; ++i) {
				executor.submit(new SleepTask());
			}
		}
	}

	private static void performTasksWithVirtualThreads() {
		try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
			for (int i = 0; i < NUMBER_OF_TASKS; ++i) {
				executor.submit(new SleepTask());
			}
		}
	}
}
