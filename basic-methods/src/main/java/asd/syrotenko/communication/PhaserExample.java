package asd.syrotenko.communication;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class PhaserExample {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		createOrderFromRequest(executorService, "request1.json");
		executorService.shutdown();
	}

	private static void createOrderFromRequest(ExecutorService executorService, String file) {
		Phaser phaser = new Phaser();
		executorService.submit(new ReadTask(phaser, file));
		executorService.submit(new CreateTask(phaser, "Order", 1));
		executorService.submit(new CreateTask(phaser, "OrderDetails", 1));
		executorService.submit(new SendEmailTask(phaser, "customer@mail.com", 2));
		executorService.submit(new SendEmailTask(phaser, "owner@mail.com", 2));
	}

	private static class ReadTask implements Runnable {
		private final Phaser phaser;
		private final String file;

		private ReadTask(Phaser phaser, String file) {
			this.phaser = phaser;
			this.file = file;
			phaser.register();
		}

		@Override
		public void run() {
			try {
				Thread.sleep(100);
				System.out.println("Read request from file " + file);
				phaser.arriveAndDeregister();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class CreateTask implements Runnable {
		private final Phaser phaser;
		private final String entity;
		private final Integer phase;

		private CreateTask(Phaser phaser, String entity, Integer phase) {
			this.phaser = phaser;
			this.entity = entity;
			this.phase = phase;
			phaser.register();
		}

		@Override
		public void run() {
			while (phaser.getPhase() < phase) {
				phaser.arriveAndAwaitAdvance();
			}
			try {
				Thread.sleep(new Random().nextInt(200));
				System.out.println("Created " + entity);
				phaser.arriveAndDeregister();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class SendEmailTask implements Runnable {
		private final Phaser phaser;
		private final String receiver;
		private final Integer phase;

		private SendEmailTask(Phaser phaser, String receiver, Integer phase) {
			this.phaser = phaser;
			this.receiver = receiver;
			this.phase = phase;
			phaser.register();
		}

		@Override
		public void run() {
			while (phaser.getPhase() < phase) {
				phaser.arriveAndAwaitAdvance();
			}
			try {
				Thread.sleep(new Random().nextInt(50));
				System.out.println("Send email to " + receiver);
				phaser.arriveAndDeregister();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
