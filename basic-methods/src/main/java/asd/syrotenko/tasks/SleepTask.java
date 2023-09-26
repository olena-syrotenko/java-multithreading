package asd.syrotenko.tasks;

public class SleepTask implements Runnable {
	@Override
	public void run() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("SleepTask was interrupted. InterruptedException was caught");
		}
	}
}
