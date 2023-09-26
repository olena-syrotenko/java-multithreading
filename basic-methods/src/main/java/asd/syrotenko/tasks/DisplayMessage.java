package asd.syrotenko.tasks;

public class DisplayMessage implements Runnable {

	private final String message;

	public DisplayMessage(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		System.out.println(message);
	}
}