package asd.syrotenko.locking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DeadlockExample {
	public static void main(String[] args) {
		ProductInventory productInventory = new ProductInventory(Collections.nCopies(10, new Object()), Collections.nCopies(50, new Object()));
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);

		// with deadlock
		useDifferentOrder(scheduledThreadPool, productInventory);

		// without deadlock
		useStrictOrder(scheduledThreadPool, productInventory);
	}

	public static void useDifferentOrder(ScheduledExecutorService scheduledThreadPool, ProductInventory productInventory) {
		scheduledThreadPool.scheduleAtFixedRate(() -> productInventory.moveToShop(5), 0, 3, TimeUnit.MILLISECONDS);
		scheduledThreadPool.scheduleAtFixedRate(() -> productInventory.moveToStorage(2), 0, 1, TimeUnit.MILLISECONDS);
	}

	public static void useStrictOrder(ScheduledExecutorService scheduledThreadPool, ProductInventory productInventory) {
		scheduledThreadPool.scheduleAtFixedRate(() -> productInventory.moveToShopWithStrictOrder(5), 0, 3, TimeUnit.MILLISECONDS);
		scheduledThreadPool.scheduleAtFixedRate(() -> productInventory.moveToStorage(2), 0, 1, TimeUnit.MILLISECONDS);
	}

	public static class ProductInventory {
		private final List<Object> productInStorage = new ArrayList<>();
		private final List<Object> productInShop = new ArrayList<>();

		public ProductInventory(List<Object> productInStorage, List<Object> productInShop) {
			this.productInStorage.addAll(productInStorage);
			this.productInShop.addAll(productInShop);
		}

		public void moveToStorage(int count) {
			synchronized (productInStorage) {
				synchronized (productInShop) {
					System.out.println("Move products to storage...");
					productInStorage.addAll(productInShop.stream().limit(count).collect(Collectors.toList()));
					productInShop.clear();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		public void moveToShop(int count) {
			synchronized (productInShop) {
				synchronized (productInStorage) {
					System.out.println("Move products to shop...");
					productInShop.addAll(productInStorage.stream().limit(count).collect(Collectors.toList()));
					productInStorage.clear();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		public void moveToShopWithStrictOrder(int count) {
			synchronized (productInStorage) {
				synchronized (productInShop) {
					System.out.println("Move products to shop...");
					productInShop.addAll(productInStorage.stream().limit(count).collect(Collectors.toList()));
					productInStorage.clear();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
