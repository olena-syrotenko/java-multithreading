package asd.syrotenko.tasks;

public class SayHello implements Runnable {

	private final String user;

	public SayHello(String message) {
		this.user = message;
	}

	@Override
	public void run() {
		System.out.println("Good morning, " + user + "!");
	}
}
