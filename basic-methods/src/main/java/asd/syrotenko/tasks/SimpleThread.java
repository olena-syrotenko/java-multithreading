package asd.syrotenko.tasks;

public class SimpleThread extends Thread {

	public SimpleThread(String name) {
		super();
		this.setName(name);
	}

	@Override
	public void run() {
		System.out.println("Start of " + this.getName());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("End of " + this.getName());
	}
}