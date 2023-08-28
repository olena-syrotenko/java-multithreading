package asd.syrotenko;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PerformanceMetricsExample {
	public static void main(String[] args) throws InterruptedException {
		PerformanceMetrics performanceMetrics = new PerformanceMetrics();

		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(new MeasurePerformance(performanceMetrics));
		executorService.submit(new MeasurePerformance(performanceMetrics));
		executorService.submit(new MeasurePerformance(performanceMetrics));

		// random numbers are generated from 0 to 100, so the average is expected to be around 50
		ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
		scheduledThreadPool.scheduleAtFixedRate(() -> System.out.println(performanceMetrics.getAveragePerformance()), 0, 100, TimeUnit.MILLISECONDS);

		executorService.shutdown();
		if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
			executorService.shutdownNow();
		}
		scheduledThreadPool.shutdown();
	}

	// totally safe for multithreading app
	public static class PerformanceMetrics {

		// add volatile keyword to make read and write operations for long and double atomic
		private volatile long count = 0;
		private volatile double averagePerformance = 0.0;

		// contains math operations that are not atomic in spite of volatile keyword, so synchronized is used
		public synchronized void addSample(long time) {
			double total = averagePerformance * count;
			count++;
			averagePerformance = (total + time) / count;
		}

		// atomic according to volatile field
		public double getAveragePerformance() {
			return averagePerformance;
		}
	}

	public static class MeasurePerformance extends Thread {

		private PerformanceMetrics performanceMetrics;
		private Random random;

		public MeasurePerformance(PerformanceMetrics performanceMetrics) {
			this.performanceMetrics = performanceMetrics;
			random = new Random();
		}

		@Override
		public void run() {
			for (int i = 0; i < 100; ++i) {
				long start = System.currentTimeMillis();
				try {
					// mock execution of some logic
					Thread.sleep(random.nextInt(100));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				long end = System.currentTimeMillis();
				performanceMetrics.addSample(end - start);
			}
		}
	}

}
