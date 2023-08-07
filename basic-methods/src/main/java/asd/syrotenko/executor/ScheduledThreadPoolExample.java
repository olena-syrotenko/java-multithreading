package asd.syrotenko.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExample {

	public static void main(String[] args) {
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);

		// Scheduled message will be displayed after main thread message
		scheduledThreadPool.schedule(() -> System.out.println("Scheduled message"), 1, TimeUnit.SECONDS);
		System.out.println("Message from main thread");
		scheduledThreadPool.shutdown();
	}
}
