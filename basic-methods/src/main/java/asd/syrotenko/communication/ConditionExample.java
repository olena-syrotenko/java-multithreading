package asd.syrotenko.communication;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ConditionExample {

	public static void main(String[] args) {
		Store store = new Store();

		Thread storekeeperThread = new Storekeeper(store);
		storekeeperThread.setDaemon(true);
		storekeeperThread.start();

		ExecutorService executorService = Executors.newCachedThreadPool();
		IntStream.range(0, 15).forEach(i -> executorService.submit(new Customer(store)));

		executorService.shutdown();
	}

	public static class Store {
		public static final Integer MAX_FROM_STOCK = 7;
		public static final Integer MAX_TO_SELL = 5;
		private Integer productsCount = MAX_FROM_STOCK;
		private Lock lockObject = new ReentrantLock();
		private Condition condition = lockObject.newCondition();

		public void sell(Integer countToSell) {
			lockObject.lock();
			try {
				while (productsCount < countToSell) {
					try {
						// if required number of items is less than number of items in store
						// wait for storekeeper to bring in new items
						condition.await();
					} catch (InterruptedException e) {
						System.out.println("Interrupted waiting of sell");
					}
				}

				productsCount -= countToSell;
				System.out.println(countToSell + " items were sold, " + productsCount + " items in store");
				condition.signalAll();
			} finally {
				lockObject.unlock();
			}
		}

		public void stock() {
			lockObject.lock();
			try {
				while (productsCount >= MAX_TO_SELL) {
					try {
						// wait until you need to refill items
						condition.await();
					} catch (InterruptedException e) {
						System.out.println("Interrupted waiting of stock");
					}
				}

				productsCount += MAX_FROM_STOCK;
				System.out.println(MAX_FROM_STOCK + " items were brought in, " + productsCount + " items in store");
				condition.signalAll();
			} finally {
				lockObject.unlock();
			}
		}
	}

	public static class Customer extends Thread {

		private final Store store;
		private final Random random;

		public Customer(Store store) {
			this.store = store;
			random = new Random();
		}

		@Override
		public void run() {
			store.sell(random.nextInt(Store.MAX_TO_SELL));
		}
	}

	public static class Storekeeper extends Thread {

		private final Store store;

		public Storekeeper(Store store) {
			this.store = store;
		}

		@Override
		public void run() {
			while (true) {
				store.stock();
			}
		}
	}

}
