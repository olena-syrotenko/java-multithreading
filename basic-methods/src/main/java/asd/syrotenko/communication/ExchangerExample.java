package asd.syrotenko.communication;

import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangerExample {
	public static void main(String[] args) throws InterruptedException {
		Exchanger<Queue<Integer>> readerExchanger = new Exchanger<>();
		Exchanger<Queue<Integer>> writerExchanger = new Exchanger<>();
		ExecutorService executorService = Executors.newCachedThreadPool();

		executorService.submit(new GenerateTask(readerExchanger, 4));
		executorService.submit(new SquareNumberTask(readerExchanger, writerExchanger));
		executorService.submit(new DisplayNumber(writerExchanger));

		Thread.sleep(500);
		executorService.shutdownNow();
	}

	private static class GenerateTask implements Runnable {
		private final Exchanger<Queue<Integer>> readerExchanger;
		private final Integer limit;
		private final Random random = new Random();
		private Queue<Integer> generatedNumbers = new ConcurrentLinkedQueue<>();

		private GenerateTask(Exchanger<Queue<Integer>> readerExchanger, Integer limit) {
			this.readerExchanger = readerExchanger;
			this.limit = limit;
		}

		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(10);
					generatedNumbers.add(random.nextInt(100));
					if (generatedNumbers.size() >= limit) {
						generatedNumbers = readerExchanger.exchange(generatedNumbers);
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class SquareNumberTask implements Runnable {
		private final Exchanger<Queue<Integer>> readerExchanger;
		private final Exchanger<Queue<Integer>> writerExchanger;
		private Queue<Integer> generatedNumbers = new ConcurrentLinkedQueue<>();
		private Queue<Integer> processedNumbers = new ConcurrentLinkedQueue<>();

		private SquareNumberTask(Exchanger<Queue<Integer>> readerExchanger, Exchanger<Queue<Integer>> writerExchanger) {
			this.readerExchanger = readerExchanger;
			this.writerExchanger = writerExchanger;
		}

		@Override
		public void run() {
			try {
				generatedNumbers = readerExchanger.exchange(generatedNumbers);
				while (true) {
					processedNumbers.add((int) Math.pow(Optional.ofNullable(generatedNumbers.poll()).orElse(0), 2));
					if (generatedNumbers.isEmpty()) {
						processedNumbers = writerExchanger.exchange(processedNumbers);
						generatedNumbers = readerExchanger.exchange(generatedNumbers);
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class DisplayNumber implements Runnable {
		private final Exchanger<Queue<Integer>> writerExchanger;
		private Queue<Integer> processedNumbers = new ConcurrentLinkedQueue<>();

		private DisplayNumber(Exchanger<Queue<Integer>> writerExchanger) {
			this.writerExchanger = writerExchanger;
		}

		@Override
		public void run() {
			try {
				processedNumbers = writerExchanger.exchange(processedNumbers);
				while (true) {
					Thread.sleep(10);
					System.out.println("Processed number: " + processedNumbers.poll());
					if (processedNumbers.isEmpty()) {
						processedNumbers = writerExchanger.exchange(processedNumbers);
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
