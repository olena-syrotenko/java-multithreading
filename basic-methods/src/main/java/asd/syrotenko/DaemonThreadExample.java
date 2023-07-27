package asd.syrotenko;

public class DaemonThreadExample {

	public static void main(String[] args) {
		Thread userThread = new SimpleThread("UserThread");
		Thread daemonThread = new SimpleThread("DaemonThread");

		userThread.start();
		daemonThread.setDaemon(true);
		daemonThread.start();

		// only 'Thread UserThread was started' will be printed
		// because main thread terminates before daemon thread executes
	}

	public static class SimpleThread extends Thread {

		public SimpleThread(String name) {
			super();
			this.setName(name);
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Thread " + this.getName() + " was started");
		}
	}
}
