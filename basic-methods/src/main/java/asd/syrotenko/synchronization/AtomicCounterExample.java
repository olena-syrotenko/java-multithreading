package asd.syrotenko.synchronization;

public class AtomicCounterExample {
	public static void main(String[] args) throws InterruptedException {
		Counter counter = new Counter();

		Thread increaseAtomicThread = new Thread(() -> counter.increaseAtomically(1000000));
		Thread decreaseAtomicThread = new Thread(() -> counter.decreaseAtomically(1000000));

		increaseAtomicThread.start();
		decreaseAtomicThread.start();

		increaseAtomicThread.join();
		decreaseAtomicThread.join();

		System.out.println("Result with atomic method is " + counter.getAtomicCounter());

	}
}
