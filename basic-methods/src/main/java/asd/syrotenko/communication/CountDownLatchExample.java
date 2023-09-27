package asd.syrotenko.communication;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchExample {

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(4);
		ExecutorService executorService = Executors.newCachedThreadPool();

		executorService.submit(new InitializeService(countDownLatch, "AuthorizationService"));
		executorService.submit(new InitializeService(countDownLatch, "ValidationService"));
		executorService.submit(new InitializeService(countDownLatch, "CustomerService"));
		executorService.submit(new InitializeService(countDownLatch, "OrderService"));

		if (countDownLatch.await(10, TimeUnit.SECONDS)) {
			// wait until all services will be initialized
			System.out.println("Application is started");
		} else {
			System.out.println("Not all services were initialized");
		}

		executorService.shutdown();
	}

	private static class InitializeService implements Runnable {

		private final CountDownLatch countDownLatch;
		private final String serviceName;

		public InitializeService(CountDownLatch countDownLatch, String name) {
			this.serviceName = name;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			System.out.println("Start of initializing " + serviceName);
			try {
				Thread.sleep(new Random().nextInt(1000));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			System.out.println(serviceName + " is initialized");
			countDownLatch.countDown();
		}

	}
}


