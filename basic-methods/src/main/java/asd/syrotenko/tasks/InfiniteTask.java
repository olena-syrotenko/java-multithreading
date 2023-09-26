package asd.syrotenko.tasks;

public class InfiniteTask implements Runnable {
	@Override
	public void run() {
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("InfiniteTask was interrupted. Interruption flag was checked");
				return;
			}
		}
	}
}
