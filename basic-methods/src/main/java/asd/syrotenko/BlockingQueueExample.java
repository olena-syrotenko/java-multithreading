package asd.syrotenko;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockingQueueExample {

	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(5);

		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(new MessageProducer(messageQueue));
		executorService.submit(new MessageConsumer(messageQueue));

		executorService.shutdown();
	}

	private static class MessageProducer implements Runnable {
		private final BlockingQueue<String> messageQueue;
		private final Random random;

		public MessageProducer(BlockingQueue<String> messageQueue) {
			this.messageQueue = messageQueue;
			random = new Random();
		}

		@Override
		public void run() {
			for (int i = 0; i < 10; ++i) {
				try {
					Thread.sleep(random.nextInt(10));
					messageQueue.put("Message " + i);
					System.out.println("Put message " + i);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private static class MessageConsumer implements Runnable {
		private final BlockingQueue<String> messageQueue;
		private final Random random;

		public MessageConsumer(BlockingQueue<String> messageQueue) {
			this.messageQueue = messageQueue;
			random = new Random();
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(random.nextInt(40));
					System.out.println("Get message: " + messageQueue.take());
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				if (messageQueue.peek() == null) {
					break;
				}
			}
		}
	}
}
