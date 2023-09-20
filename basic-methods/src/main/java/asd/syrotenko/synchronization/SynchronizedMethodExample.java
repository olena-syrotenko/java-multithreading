package asd.syrotenko.synchronization;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizedMethodExample {

	public static void main(String[] args) throws InterruptedException {
		Counter counter = new Counter();
		ExecutorService executorService = Executors.newCachedThreadPool();

		executorService.submit(() -> counter.increase(1000000));
		executorService.submit(() -> counter.decrease(1000000));
		executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

		// Result will be different every time, but it will not be correct
		System.out.println("Result with not synchronized method is " + counter.getCounter());

		counter.resetCounter();
		executorService.submit(() -> counter.increaseSynchronized(1000000));
		executorService.submit(() -> counter.decreaseSynchronized(1000000));
		executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

		// With using synchronized method result will be correct - 0, because when using the method to increase, the method to decrease will be blocked for another thread
		System.out.println("Result with synchronized method is " + counter.getCounter());

		executorService.shutdown();
	}

}
