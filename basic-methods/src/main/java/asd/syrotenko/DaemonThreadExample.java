package asd.syrotenko;

import asd.syrotenko.tasks.SimpleThread;

public class DaemonThreadExample {

	public static void main(String[] args) {
		Thread userThread = new SimpleThread("UserThread");
		Thread daemonThread = new SimpleThread("DaemonThread");

		userThread.start();
		daemonThread.setDaemon(true);
		daemonThread.start();

		// only 'end of UserThread' will be printed
		// because main thread terminates before daemon thread executes
	}
}
