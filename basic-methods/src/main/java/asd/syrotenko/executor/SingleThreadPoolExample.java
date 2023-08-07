package asd.syrotenko.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadPoolExample {

	public static void main(String[] args) {
		AtomicInteger result = new AtomicInteger();
		ExecutorService singleThreadService = Executors.newSingleThreadExecutor();

		singleThreadService.submit(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Thread was interrupted");
			}
			result.set(10);
		});
		singleThreadService.submit(() -> System.out.println(result.get()));
		// The output is always be 10 because operations are executed sequentially
		singleThreadService.shutdown();
	}
}
