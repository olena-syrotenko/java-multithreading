package asd.syrotenko.locking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {

	public static void main(String[] args) throws InterruptedException {
		ExchangeRate exchangeRate = new ExchangeRate();

		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(new UpdateExchangeRate(exchangeRate));
		executorService.submit(new DisplayExchangeRate(exchangeRate));

		if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
			executorService.shutdownNow();
		}
	}

	private static class DisplayExchangeRate implements Runnable {
		private final ExchangeRate exchangeRate;

		public DisplayExchangeRate(ExchangeRate exchangeRate) {
			this.exchangeRate = exchangeRate;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				if (exchangeRate.getReentrantLock().tryLock()) {
					try {
						exchangeRate.getCurrencies().forEach((key, value) -> System.out.println(key + " - " + value));
						System.out.println();
					} finally {
						exchangeRate.getReentrantLock().unlock();
					}
				}
			}
		}
	}

	private static class UpdateExchangeRate implements Runnable {
		private final ExchangeRate exchangeRate;

		public UpdateExchangeRate(ExchangeRate exchangeRate) {
			this.exchangeRate = exchangeRate;
		}

		@Override
		public void run() {
			while (true) {
				exchangeRate.getReentrantLock().lock();
				try {
					Thread.sleep(2000);
					exchangeRate.getCurrencies().entrySet().forEach(entry -> entry.setValue(entry.getValue() + 1));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					exchangeRate.getReentrantLock().unlock();
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private static class ExchangeRate {
		private final ReentrantLock reentrantLock = new ReentrantLock();

		private final Map<String, Double> currencies;

		public ExchangeRate() {
			currencies = new HashMap<>();
			currencies.put("USD", 0.0);
			currencies.put("EUR", 0.0);
			currencies.put("UAH", 0.0);
		}

		public ReentrantLock getReentrantLock() {
			return reentrantLock;
		}

		public Map<String, Double> getCurrencies() {
			return currencies;
		}
	}

}
