package asd.syrotenko.synchronization;

import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SynchronizedBlockExample {
	public static void main(String[] args) throws InterruptedException {
		StopWatch stopWatch = new StopWatch();
		Counter counter = new Counter();
		ExecutorService executorService = Executors.newCachedThreadPool();

		stopWatch.start();
		executorService.invokeAll(defineIncreaseWithSaveHistoryTasks(counter, 3));
		stopWatch.stop();
		System.out.println("Time of execution with synchronized method: " + stopWatch.getTime());

		stopWatch.reset();

		stopWatch.start();
		executorService.invokeAll(defineIncreaseWithSaveHistorySynchronizedBlockTasks(counter, 3));
		stopWatch.stop();
		// Performance is 3 times better with the use of synchronized block
		System.out.println("Time of execution with synchronized block: " + stopWatch.getTime());

		executorService.shutdown();
	}

	public static List<Callable<Integer>> defineIncreaseWithSaveHistoryTasks(Counter counter, Integer size) {
		return IntStream.range(0, size).mapToObj(i -> (Callable<Integer>) () -> counter.increaseWithSaveHistory(10)).collect(Collectors.toList());
	}

	public static List<Callable<Integer>> defineIncreaseWithSaveHistorySynchronizedBlockTasks(Counter counter, Integer size) {
		return IntStream.range(0, size).mapToObj(i -> (Callable<Integer>) () -> counter.increaseWithSaveHistorySynchronizedBlock(10))
				.collect(Collectors.toList());
	}
}
