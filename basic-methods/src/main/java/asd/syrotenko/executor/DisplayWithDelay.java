package asd.syrotenko.executor;

public class DisplayWithDelay implements Runnable {
	private final String message;
	private int delay = 2000;

	public DisplayWithDelay(String message) {
		this.message = message;
	}

	public DisplayWithDelay(String message, int delayMilliseconds) {
		this.message = message;
		this.delay = delayMilliseconds;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(delay);
			System.out.println(message);
		} catch (InterruptedException e) {
			System.out.println("Thread of DisplayWithDelay was interrupted");
		}
	}
}
