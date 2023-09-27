package asd.syrotenko.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierExample {

	public static void main(String[] args) {
		List<Double> results = new ArrayList<>();
		CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new AggregateTask(results));
		try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
			for (int i = 1; i <= 5; ++i) {
				executorService.submit(new FactorialTask(cyclicBarrier, i, results));
				executorService.submit(new ExponentiationTask(cyclicBarrier, i, results));
				executorService.submit(new LogarithmTask(cyclicBarrier, i, results));
			}
		}
	}

	private static class AggregateTask implements Runnable {
		private final List<Double> results;

		public AggregateTask(List<Double> results) {
			this.results = results;
		}

		@Override
		public void run() {
			double sum = results.stream().reduce(0.0, Double::sum);
			results.clear();
			System.out.println("Result of sum is: " + sum);
		}
	}

	private static class FactorialTask implements Runnable {

		private final CyclicBarrier cyclicBarrier;
		private final Integer number;
		private final List<Double> results;

		public FactorialTask(CyclicBarrier cyclicBarrier, Integer number, List<Double> results) {
			this.cyclicBarrier = cyclicBarrier;
			this.number = number;
			this.results = results;
		}

		@Override
		public void run() {
			double result = 1;
			for (int i = 1; i <= number; ++i) {
				result *= i;
			}
			results.add(result);
			System.out.println("Calculated factorial for " + number + " that is " + result);
			try {
				cyclicBarrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class ExponentiationTask implements Runnable {

		private final CyclicBarrier cyclicBarrier;
		private final Integer number;
		private final List<Double> results;

		public ExponentiationTask(CyclicBarrier cyclicBarrier, Integer number, List<Double> results) {
			this.cyclicBarrier = cyclicBarrier;
			this.number = number;
			this.results = results;
		}

		@Override
		public void run() {
			double result = Math.pow(number, 5);
			results.add(result);
			System.out.println("Calculated exponentiation for " + number + " that is " + result);
			try {
				cyclicBarrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class LogarithmTask implements Runnable {

		private final CyclicBarrier cyclicBarrier;
		private final Integer number;
		private final List<Double> results;

		public LogarithmTask(CyclicBarrier cyclicBarrier, Integer number, List<Double> results) {
			this.cyclicBarrier = cyclicBarrier;
			this.number = number;
			this.results = results;
		}

		@Override
		public void run() {
			double result = Math.log(number);
			results.add(result);
			System.out.println("Calculated logarithm for " + number + " that is " + result);
			try {
				cyclicBarrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
