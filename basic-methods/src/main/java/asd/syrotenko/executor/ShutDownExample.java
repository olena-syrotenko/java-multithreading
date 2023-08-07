package asd.syrotenko.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShutDownExample {
	public static void main(String[] args) {

		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(new DisplayWithDelay("Task with 200 ms delay", 200));
		executorService.submit(new DisplayWithDelay("Task with 1000 ms delay", 1000));
		executorService.submit(new DisplayWithDelay("Task with 2000 ms delay", 2000));

		// shut down the service
		executorService.shutdown();
		try {
			// wait for all tasks to be completed
			if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
				// if not all tasks was completed, stop execution immediately
				executorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			// if execution was interrupted, stop execution immediately
			executorService.shutdownNow();
		}

		//	output:
		//	Task with 200 ms delay
		//	Task with 1000 ms delay
		//	Thread of DisplayWithDelay was interrupted
	}
}
