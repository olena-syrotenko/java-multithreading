package asd.syrotenko.synchronization;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StaticSynchronizationExample {

	public static void main(String[] args) throws InterruptedException {
		Counter counter1 = new Counter();
		Counter counter2 = new Counter();
		ExecutorService executorService = Executors.newCachedThreadPool();

		// With using different instances synchronized method is not blocked and threads run in parallel
		executorService.submit(counter1::doSomething);
		executorService.submit(counter2::doSomething);

		executorService.awaitTermination(2, TimeUnit.SECONDS);

		// With using different instances static synchronized method is blocked and another thread waits for the first thread to complete
		executorService.submit(Counter::doSomethingStatic);
		executorService.submit(Counter::doSomethingStatic);

		executorService.shutdown();
	}
}
