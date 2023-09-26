package asd.syrotenko.executor;

import asd.syrotenko.tasks.DisplayWithDelay;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FixedThreadPoolExample {
	public static void main(String[] args) {
		ThreadPoolExecutor fixedThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
		fixedThreadPool.submit(new DisplayWithDelay("The first task"));
		fixedThreadPool.submit(new DisplayWithDelay("The second task"));
		fixedThreadPool.submit(new DisplayWithDelay("The third task"));

		// There is 1 task in queue, because all threads in pool are executing other two tasks
		System.out.println("Executing tasks: " + fixedThreadPool.getPoolSize());
		System.out.println("Tasks in queue: " + fixedThreadPool.getQueue().size());

		fixedThreadPool.shutdown();
	}

}
