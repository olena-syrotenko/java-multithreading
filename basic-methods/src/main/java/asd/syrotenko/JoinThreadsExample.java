package asd.syrotenko;

public class JoinThreadsExample {

	public static void main(String[] args) throws InterruptedException {
		Thread thread1 = new SimpleThread("thread1");
		Thread thread2 = new SimpleThread("thread2");
		Thread thread3 = new SimpleThread("thread3");

		// start the 1st thread
		thread1.start();
		// wait 2 seconds
		thread1.join(2000);
		// start the 2nd thread
		thread2.start();
		// wait for the 1st thread to complete
		thread1.join();
		// start the 3rd thread
		thread3.start();

		// wait for all threads to complete
		thread2.join();
		thread3.join();

		System.out.println("All threads are completed");

		/* Output:
			Start of thread1
			Start of thread2
			End of thread1
			Start of thread3
			End of thread2
			End of thread3
			All threads are completed
		 */
	}

	public static class SimpleThread extends Thread {

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
}
