package asd.syrotenko.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CachedThreadPoolExample {

	public static void main(String[] args) throws InterruptedException {
		ThreadPoolExecutor cachedThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		cachedThreadPool.submit(new DisplayWithDelay("The first task"));
		cachedThreadPool.submit(new DisplayWithDelay("The second task"));
		cachedThreadPool.submit(new DisplayWithDelay("The third task"));

		// There is no task in queue, because new thread is created for new task
		System.out.println("Executing tasks: " + cachedThreadPool.getPoolSize());
		System.out.println("Tasks in queue: " + cachedThreadPool.getQueue().size());

		cachedThreadPool.shutdown();
	}

}
