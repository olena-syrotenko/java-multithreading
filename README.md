# Java Multithreading Concepts &amp; Examples

_Multithreading_ is the ability of a CPU to execute multiple threads independently at the same time but share the process resources simultaneously.

It has 2 main motivations: _responsiveness_ that is achived by concurrency and _highest perfomance_ that is achived by parallelism. 

Multithreading in Java is a feature that allows you to subdivide the specific program into two or more threads to make the execution of the program fast and easy.

## Plan
1. [Threads](#threads)
   - [creation](#creation)
   - [basic methods](#methods)
   - [daemon thread](#daemon)
   - data sharing
   - [optimization](#optimization)   
2. [Executor](#executor)
3. [Synchronization](#synchronization)
4. Locking
5. Inter-thread communication
   - Semaphore
   - CyclicBarrier
   - CountDownLatch
6. Virtual Threads

## Threads
_Threads_ are the lightweight and smallest unit of processing that can be managed independently by a scheduler. They share the common address space and are independent of each other.

**Process** is a program in execution that is isolated and handled by PCB. While **thread** is a smallest unit of the particular process that share a common memory. So process can have single or multiple threads. According to that there are 2 types of architecture: **_Multithreaded_** and **_Multi-Process_**.

**_Multithreaded_** architecture is suitable when tasks share a lot of data and perfomance is more important because a thread requires less time for creation, termination, and context switching than a process and uses fewer resources than a process. **_Multi-Process_** architecture is suitable when security and stability have the highest priority and tasks are unrelated.

### Creation

The first way to create a thread is **implement _Runnable_** interface and pass to a new Thread object.

```java
Thread thread = new Thread(() -> System.out.println("New thread is running"));
// as Running is functional interface, we can use lambda
```
The second way to create a thread is **extend _Thread_** class and create an object of that class. It can be useful if we have a common part for custom threads.

```java
    public static void main(String[] args) {
        Thread thread = new CustomThread();
    }

    private static class CustomThread extends Thread {
        @Override
        public void run() {
            System.out.println("Custom thread is running");
        }
    }
```
### Methods

`start()` - is used to create a thread and start the execution of the task that is kept in the run() method. Can be called only once.

`run()` -  is used to start the execution of the created thread. Can be called multiple times. 

`sleep()` - is used to pause the execution of the current thread for some specified period.

`setName()` - is used to set name for the current thread.

`setPriopriy()` - is used to set priority for the current thread. Can take values from 1 to 10. Thread with the highest priority will get an execution chance prior to other threads.

`setUncaughtExceptionHandler()` - is used to set the handler invoked when the current thread terminates due to an uncaught exception. Is useful if you need to close some resources.
```java
thread.setUncaughtExceptionHandler((t, e) -> System.out.println("Unexpected error happened in thread "));

// if unchecked exception is thrown when thread will run, the output will be:
// Unexpected error happened in thread
```

`isInterrupted()` - is used to check the interrupt status and not clear the interrupt flag.

`interrupted()`- _static_, is used to check the interrupt status of the current thread, after that the interrupt flag will be cleared.

`interrupt()` - is used to set the interrupted status/flag of the target thread. If the method with interrupting handler (e.g. `wait()`, `sleep()`, `join()`) is used, then `InterruptedException` will be thrown. Or `ClosedByInterruptException` if the target thread is blocked in an I/O operation upon an interruptible channel.

```java
public static void main(String[] args) {
	Thread sleepThread = new Thread(new SleepTask());
	sleepThread.start();
	sleepThread.interrupt();

	Thread infiniteThread = new Thread(new InfiniteTask());
	infiniteThread.start();
	infiniteThread.interrupt();
}

public static class SleepTask implements Runnable {
	@Override
	public void run() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.out.println("SleepTask was interrupted. InterruptedException was caught");
		}
	}
}

public static class InfiniteTask implements Runnable {
	@Override
	public void run() {
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("InfiniteTask was interrupted. Interruption flag was checked");
				return;
			}
		}
	}
}

// Output
// SleepTask was interrupted. InterruptedException was caught
// InfiniteTask was interrupted. Interruption flag was checked
```

`join()` - is used to pause the execution of other threads unless and until the specified thread on which join is called is dead or completed. It can be useful if we need to stop a thread from running until another thread gets ended. Also time of pause can be passed as a prarameter.

```java
thread1.start();
thread1.join(2000);
// The 2nd thread will start after 2 seconds of the 1st thread's work
thread2.start();
thread1.join();
// The 3rd thread will start after the 1st thread finishes
thread3.start();
thread2.join();
thread3.join();
// message will be displayed after the 2nd and 3rd threads finish
System.out.println("All threads are completed");
```

`setDaemon()` - is used to mark the current thread as daemon thread if `true` is passed or remove daemon flag if `false` is passed.

`isDaemon()` - is used to check whether the current thread is daemon or not.

### Daemon

_Daemon threads_ are background threads that do not prevent the application from exiting after main thread termination. JVM waits for all user threads to finish their tasks before termination but excluding daemon ones. These threads are referred to as low priority threads. Daemon threads are good to use when:
1. Need to do background tasks that shouldn't block application from termination;
2. Code in a worker thread is not under out control and we do not want it to block application from termination.
Daemon threads are usually used to carry out some supportive or service tasks for other threads, so you should not do any I/O operation in them because resources will not be closed correctrly. Method `setDaemon(true)` is used to make user thread a daemon one.
```java
public static void main(String[] args) {
	Runnable displayTask = () -> {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Thread " + Thread.currentThread().getName() + " was interrupted");
		}
		System.out.println("Thread " + Thread.currentThread().getName() + " was started");
	};

	Thread userThread = new Thread(displayTask, "UserThread");
	Thread daemonThread = new Thread(displayTask, "DaemonThread");

	userThread.start();
	daemonThread.setDaemon(true);
	daemonThread.start();

	// only 'Thread UserThread was started' will be printed
	// because main thread terminates before daemon thread executes
}
```
### Optimization

**_Latency_** - the time of compilation of a single task. To improve perfomance according to latency criteria, we need to reduce time by breaking tasks into multiple ones. To achive the most effective optimization we need to check next steps:
1. The optimal number of threads is the number of cores if all threads are runnable independently and without interruption and nothing else is running on CPU. If there is some background tasks, the optimal number of thread is difference between total core number and cores for background tasks. (_e.g._ if we have 8 cores and some background tasks, the optimal number of threads is equal to 6)
2. It is necessary to compare the cost of execution of a task in single-threaded and multi-threaded cases for as many values as possible from the interval of execution of the task. (_e.g._ if the task is simple and takes little time, the time spent creating threads can increase the total execution time in a multi-threaded solution)
3. It is necessary to assess the ability to divide the task into subtasks.

**_Throughput_** - the amount of tasks completed in a gain period. To improve perfomance according to throughput criteria and perfom as many tasks as possible as fast as possible, we need to running independent tasks in parallel. It allows to skip pre- and postprocess steps and not to waste time for context switching. To achive the most effective optimization we can use a _thread pool_:
1. Best strategy - handling each request on a different thread.
2. If we need to execute CPU intensive tasks, the number of threads should be equal the number of cores.
3. If we need to execute IO intensive tasks (e.g DB or API calling), the number of threads should be more than the number of cores.


## Executor
**`Executor`** is a simple interface from `java.util.concurrent` framework that have a single method `execute(Runnable)` that should be implemented in custom executors.
```java
class ThreadPerTaskExecutor implements Executor {
	public void execute(Runnable r) {
		new Thread(r).start();
	}
}
```

**`ExecutorService`** is an interface that extends `Executor` with multiple methods for handling and checking the lifecycle of a concurrent task execution service.

`submit()` - is used to submit `Callable` or a `Runnable` task for execution and returns a resonse as `Future` instance.

`invokeAll()` - is used to execute all tasks from provided collection and return the list of `Future` results.

`invokeAny()` - is used to execute all tasks from provided collection and return the one of successfull results.

`shutdown()` - is used to shut down the executor service by stopping the acceptance of new tasks and shut down after all running threads finish their current work.

`shutdownNow()` - is used to shut down the executor service immediately without waiting for the completion of previously submitted tasks.

`awaitTermination()` - is used to  wait for all the tasks submitted to the service to complete execution according to provided time. Returns `true` if the executor service was terminated or returns `false` if the wait time elapsed before termination.

It is good practice to combine last 3 methods to shut down the executor service:
```java
// shut down the service
executorService.shutdown();
try {
    // wait for all tasks to be completed 
    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
        // if not all tasks was completed, stop execution immediately
        executorService.shutdownNow();
    } 
} catch (InterruptedException e) {
    // if execution was interrupted, stop execution immediately
    executorService.shutdownNow();
}
```

`ExecutorService` can work both with `Runnable` and `Callable` tasks. **`Runnable`** interface cannot be passed to `invokeAll` method and has single method `run()` that does not return any result and cannot throw a checked exception. **`Callable`** interface can be passed to `invokeAll` method and has single method `call()` that returns a result and can throw a checked exception. 

Executor Service contains of 3 main parts: thread pool, work queue and completion queue.  **_Thread pool_** is a collection of initialized threads that can be reused to execute incoming tasks. `ExecutorService` has next basic implementations of thread pool:

1. **_ThreadPoolExecutor_** - for executing tasks using a pool of threads. Once a thread is finished executing the task, it goes back into the pool. Has such parameters as **_corePoolSize_** (the number of core threads that will be instantiated and kept in the pool), **_maximumPoolSize_** (the number of thread the pool is allowed to grow up to) and **_keepAliveTime_** (interval of time for which the threads are allowed to exist in the idle state).

- **newSingleThreadExecutor()** - creates form of `ThreadPoolExecutor` containing a single thread. The single thread executor is ideal for creating an event loop.

```java
public static void main(String[] args) {
	AtomicInteger result = new AtomicInteger();
	ExecutorService singleThreadService = Executors.newSingleThreadExecutor();

	singleThreadService.submit(() -> {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Thread was interrupted");
		}
		result.set(10);
	});
	singleThreadService.submit(() -> System.out.println(result.get()));
	// The output is always be 10 because operations are executed sequentially
	singleThreadService.shutdown();
}
```

- **newFixedThreadPool()** - creates a `ThreadPoolExecutor` with equal corePoolSize and maximumPoolSize parameter values and a zero keepAliveTime, so the number of threads in this thread pool is always the same. If all threads are working, new tasks are put into a queue to wait for their turn.

```java
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
```

- **newCachedThreadPool()** - creates a `ThreadPoolExecutor` with 0 as a corePoolSize, `Integer.MAX_VALUE` as a maximumPoolSize and 60 seconds as a keepAliveTime. The cached thread pool may grow without bounds to accommodate any number of submitted tasks.  A typical use case is when we have a lot of short-living tasks in our application. The _queue_ size will always _be zero_ because internally a `SynchronousQueue` instance is used, in which pairs of insert and remove operations always occur simultaneously.

```java
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
```


2. **_ScheduledThreadPoolExecutor**_ - extends ThreadPoolExecutor and allows to schedule task execution instead of running it immediately when a thread is available. Created by **`newScheduledThreadPool()`** method with provided corePoolSize, unbounded maximumPoolSize and zero keepAliveTime. It provides next additional methods:

`schedule()` - is used to run a task once after a specified delay.

`scheduleAtFixedRate()` - is used to run a task after a specified initial delay and then run it repeatedly with a certain period, where _period_ is the time between the starting times of the tasks.

`scheduleWithFixedDelay()` - is used to run a task after a specified initial delay and then run it repeatedly with a certain period, where _period_ is the time between the end of the previous task and the start of the next. 

```java
public static void main(String[] args) {
	ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);

	// Scheduled message will be displayed after main thread message
	scheduledThreadPool.schedule(() -> System.out.println("Scheduled message"), 1, TimeUnit.SECONDS);
	System.out.println("Message from main thread");
	scheduledThreadPool.shutdown();
}
```

3. **_ForkJoinPool_** -  for dealing with recursive algorithms tasks. With using a simple ThreadPoolExecutor for a recursive algorithm, all your threads are busy waiting for the lower levels of recursion to finish. The ForkJoinPool implements the so-called work-stealing algorithm that allows it to use available threads more efficiently and do not create a new thread for each task or subtask

## Synchronization

Synchronization is a locking mechanism, designed to prevent access to a method or block of code and shared resource by multiple threads. It can be achieved with **`synchronized`** keyword.

- _synchronized method_ - the thread acquires a lock on the object when it enters the synchronized method and releases the lock when it leaves method. No other thread can use this method or other methods with `synchronized` keyword in this object until the current thread finishes its execution and release the lock.

```java
// if two threads call this method, then one of them will wait until the first one is finished
public synchronized void increase(Integer times, Integer value) {
	for (int i = 0; i < times; ++i) {
		counter += value;
	}
}
```

- _synchronized block_ - the thread acquires a lock on the object on the block with `synchronized` keyword and releases the lock when it leaves the block. Synchronized blocks should be preferred more as it boosts the performance of a particular program. It only locks a certain part of the program (critical section) rather than the entire method.

```java
// if two threads call this method,
// then both of them call dao concurrently and then one of them will block when the counter changes
public void increaseWithSaveHistory(Integer times, Integer value) throws InterruptedException {
	someDao.saveHistory(times, value);

	synchronized (this) {
		for (int i = 0; i < times; ++i) {
			counter += value;
		}
	}
}
```

- _static synchronization_ - the thread acquires a lock on the Class object associated with the class. Since only one Class object exists per JVM per class, only one thread can execute inside a static synchronized method per class, irrespective of the number of instances it has.

```java
// If threads work with different instances of class and call this method,
// then they will anyway be blocked during its execution
public static synchronized void doSomething() throws InterruptedException {
	System.out.println("Start of static synchronized method with thread " + Thread.currentThread().getName());
	Thread.sleep(1000);
	System.out.println("End of static synchronized method with thread " + Thread.currentThread().getName());
}
```
