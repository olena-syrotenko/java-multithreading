package asd.syrotenko.locking;

import asd.syrotenko.PerformanceMetricsExample;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ReentrantReadWriteLockExample {

	public static void main(String[] args) throws InterruptedException {
		ProductService productService = new ProductService();
		PerformanceMetricsExample.PerformanceMetrics performanceMetrics = new PerformanceMetricsExample.PerformanceMetrics();
		Thread updateThread = new Thread(new UpdateProduct(productService));
		updateThread.setDaemon(true);
		updateThread.start();
		for (int i = 0; i < 10; ++i) {
			long timeOfReading = readAndUpdate(productService);
			performanceMetrics.addSample(timeOfReading);
		}

		// With ReadWrite lock average time is about 175
		// With simple lock average time is about 790
		System.out.println("Average time of reading: " + performanceMetrics.getAveragePerformance());
	}

	private static long readAndUpdate(ProductService productService) throws InterruptedException {
		StopWatch stopWatch = new StopWatch();

		List<Thread> readThreads = Collections.nCopies(7, new ReadProduct(productService)).stream().map(Thread::new).collect(Collectors.toList());

		stopWatch.start();
		readThreads.forEach(Thread::start);
		for (Thread readThread : readThreads) {
			readThread.join();
		}
		stopWatch.stop();
		return stopWatch.getTime();
	}

	private static class ProductService {
		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private final Lock readLock = readWriteLock.readLock();
		private final Lock writeLock = readWriteLock.writeLock();
		private final ReentrantLock reentrantLock = new ReentrantLock();

		public void getProductInformation() throws InterruptedException {
			//			 reentrantLock.lock();
			readLock.lock();
			try {
				// mock reading long product info
				Thread.sleep(1);
			} finally {
				//				 reentrantLock.unlock();
				readLock.unlock();
			}
		}

		public void updateProduct() throws InterruptedException {
			//			 reentrantLock.lock();
			writeLock.lock();
			try {
				// mock updating product
				Thread.sleep(5);
			} finally {
				//				 reentrantLock.unlock();
				writeLock.unlock();
			}
			Thread.sleep(10);
		}
	}

	private static class ReadProduct implements Runnable {
		private final ProductService productService;

		public ReadProduct(ProductService productService) {
			this.productService = productService;
		}

		@Override
		public void run() {
			for (int i = 0; i < 100; ++i) {
				try {
					productService.getProductInformation();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private static class UpdateProduct implements Runnable {
		private final ProductService productService;

		public UpdateProduct(ProductService productService) {
			this.productService = productService;
		}

		@Override
		public void run() {

			while (true) {
				try {
					productService.updateProduct();
				} catch (InterruptedException e) {
					System.out.println("Update was interrupted");
				}
			}

		}
	}

}

