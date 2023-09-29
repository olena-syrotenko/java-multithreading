# Java Multithreading Concepts &amp; Examples

_Multithreading_ is the ability of a CPU to execute multiple threads independently at the same time but share the process resources simultaneously.

It has 2 main motivations: _responsiveness_ that is achived by concurrency and _highest perfomance_ that is achived by parallelism. 

Multithreading in Java is a feature that allows you to subdivide the specific program into two or more threads to make the execution of the program fast and easy.

## Plan
1. [Threads](#threads)
   - [creation](#creation)
   - [basic methods](#methods)
   - [daemon thread](#daemon)
   - [optimization](#optimization)   
2. [Executor](#executor)
3. [Synchronization](#synchronization)
4. [Locking](#locking)
   - [Deadlock](#deadlock)
   - [ReentrantLock](#lock)
6. [Inter-thread communication](#inter-thread-communication)
   - [Wait(), notify(), notifyAll()](#monitor-methods)
   - [Condition](#condition)
   - [Semaphore](#semaphore)
   - [CountDownLatch](#count-down-latch)
   - [CyclicBarrier](#cyclic-barrier)
   - [Exchanger](#exchanger)
   - [Phaser](#phaser)
7. [Virtual Threads](#virtual-threads)

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
<h3 id="methods"> Basic Methods </h3>

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

Atomic operations include:

- all reference assignments (get and set);
- assignments to a primitive type (**except** `long` and `double`)
- assignments to `long` and `double` with **`volatile`** keyword. _volatile_ field has special properties according to the Java Memory Model (JMM, specifies how multiple threads access common memory in a concurrent Java application). The reads and writes of a volatile variable are _synchronization_ actions;

- _java.util.concurrent.atomic_ classes.

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

## Locking

### Deadlock

***Deadlock*** - situation where multiple threads are blocked forever because they hold locks on different resources and are waiting for other resources to complete their task.

***Conditions***:

1. *Mutual exclusion* - when only one thread can have exclusive access to a resource at a given moment;
2. *Hold and wait* - when at least one thread is holding a resource and is waiting for another resource;
3. *Non-preemptive allocation* - when resource is released only after thread is done using it;
4. *Circular wait* - a chain of at least two threads each one is holding one resource and waiting for another resource.

```java
// in this example if two threads call moveToStorage and moveToShop methods at a given moment, then deadlock will occur because
// thread-1 will lock productInStorage object, thread-2 will lock productInShop object 
// so thread-1 will wait for productInShop unlock and thread-2 will wait for productInShop unlock
public static class ProductInventory {
	private final List<Object> productInStorage = new ArrayList<>();
	private final List<Object> productInShop = new ArrayList<>();

	public ProductInventory(List<Object> productInStorage, List<Object> productInShop) {
		this.productInStorage.addAll(productInStorage);
		this.productInShop.addAll(productInShop);
	}

	public void moveToStorage(int count) {
		synchronized (productInStorage) {
			synchronized (productInShop) {
				productInStorage.addAll(productInShop.stream().limit(count).collect(Collectors.toList()));
				productInShop.clear();
			}
		}
	}

	public void moveToShop(int count) {
		synchronized (productInShop) {
			synchronized (productInStorage) {
				productInShop.addAll(productInStorage.stream().limit(count).collect(Collectors.toList()));
				productInStorage.clear();
			}
		}
	}
}
```

***Solution***: avoid circular wait - enforce a strict order in lock acquisition.

```java
// if we use the same order of locking then deadlock will not occur
public static class ProductInventory {
	private final List<Object> productInStorage = new ArrayList<>();
	private final List<Object> productInShop = new ArrayList<>();

	public ProductInventory(List<Object> productInStorage, List<Object> productInShop) {
		this.productInStorage.addAll(productInStorage);
		this.productInShop.addAll(productInShop);
	}

	public void moveToStorage(int count) {
		synchronized (productInStorage) {
			synchronized (productInShop) {
				productInStorage.addAll(productInShop.stream().limit(count).collect(Collectors.toList()));
				productInShop.clear();
			}
		}
	}

	public void moveToShop(int count) {
		synchronized (productInStorage) {
			synchronized (productInShop) {
				productInShop.addAll(productInStorage.stream().limit(count).collect(Collectors.toList()));
				productInStorage.clear();
			}
		}
	}
}
```

### Lock

***ReentrantLock*** - an object that provides same functionality as `synchronized` keyword. Requires explicit locking with `lock()` and unlocking with `unlock()`. 

*Advantages*: provide more control over locking (wuth `lockInterruptibly()` and `tryLock()`) and guarantee fairness (with pass `true` into constructor `new ReentrantLock(true)`).

Good practice to avoid deadlocks - *surround* critical section with *try-catch* block and put `unlock()` method call in *finally* block.

```java
private final ReentrantLock lockObject = new ReentrantLock();

public void someMethod() {
	lockObject.lock();
	try {
		// do something
	} finally {
		lockObject.unlock();
	}
}
```

**`lockInterruptibly()`** - allow iterrupt suspended thread from waiting on the lock. 

```java
private final ReentrantLock lockObject = new ReentrantLock();

// if we iterrupt thread that waiting for lockObject in this method then threas will be successfully interrupted and be free to execute another task 
public void someMethod() {
	try {
    		lockObject.lockInterruptibly();
   		try {
			// do something
		} finally {
			lockObject.unlock();
		}
	} catch (InterruptedException e) {
		System.out.println("Thread with lock waiting was interrupted");
	}
}
```

**`tryLock()`** - allow lock object with checking lock status: returns `true` if lock is available and `false` is not available (in this case thread will not be blocked and go to another instruction).

```java
private final ReentrantLock lockObject = new ReentrantLock();

// if lockObject will be locked, then we just skeep critical section and go to another instructions
public void someMethod() {
	if (lockObject.tryLock()) {
		try {
			// do something
		} finally {
			lockObject.unlock();
		}
	}
	// do something else
}
```

<h2 id="inter-thread-communication"> Inter-thread communication </h2>

<h3 id="monitor-methods"> Wait(), notify(), notifyAll() </h3>

These methods of Object class are used to notify some threads of the actions of others. They are called only in **synchronized** context.

`wait()` - causes the current thread to wait until another thread wakes it up. Current thread goes to *wait* state and not consuming CPU.

`notify()` - wakes up a *single* waiting thread.

`notifyAll()` - wakes up *all* waiting threads.

```java
public synchronized void sell(Integer countToSell) {
	while (productsCount < countToSell) {
		// if required number of items is less than number of items in store
		// wait for storekeeper to bring in new items
		wait();
	}

	productsCount -= countToSell;
	System.out.println(countToSell + " items were sold, " + productsCount + " items in store");
	// signal to storekeeper threads to increase amount of items
	notifyAll();
}
```

### Condition

***Condition*** is associated with a *lock* and allows threads to wait for some condition to become true, due to some activity happening on other threads. 

`await()` - unlock *lock* and wait until signaled, similar to wait() method.

`signal()` - wakes up a single thread that waiting on condition. Waked up thread reacquires the lock. If there are 0 threads waiting, this method does nothing. Similar to notify().

`signalAll()` - wakes up *all* waiting threads, similar to notifyAll() method.

```java
Lock lockObject = new ReentrantLock();
// create condition on lock object
Condition condition = lockObject.newCondition();

public void sell(Integer countToSell) {
	lockObject.lock();
	try {
		while (productsCount < countToSell) {
			// if required number of items is less than number of items in store
			// wait for storekeeper to bring in new items
			condition.await();
		}

		productsCount -= countToSell;
		System.out.println(countToSell + " items were sold, " + productsCount + " items in store");
		// signal to storekeeper threads to increase amount of items
		condition.signalAll();
	} finally {
		lockObject.unlock();
	}
}
```

### Semaphore

***Semaphore*** is used to restrict the number of access to a particular resources. When *Lock* allows only *one* access to a resource, semaphore can restrict any number of resource.
It is suitable to be used in Producer-Consumer pattern.

```java
// semaphore to lock consumer
Semaphore full = new Semaphore(0);
// semaphore to lock producer
Semaphore empty = new Semaphore(1);
// item to consume
Object item = null

// Producer
private void produce() {
	while(true) {
		empty.acquire();
		item = produceSomething();
		full.release();
	}
}

// Consumer
private void consume() {
	while(true) {
		full.acquire();
		consumeItem(item);
		empty.release();
	}
}
```

Another way to use if we have limited number of users for some resource.

```java
public static void main(String[] args) throws InterruptedException {
	ParkingService parkingService = new ParkingService();
	ExecutorService executorService = Executors.newCachedThreadPool();

	// only first two cars will be parked, then next three cars stay in queue and wait for previous cars leaving parking slot
	List<Callable<Boolean>> carsToPark = IntStream.range(1, 6).mapToObj(number -> (Callable<Boolean>) () -> {
			System.out.println("Car " + number + " in parking queue");
			parkingService.parkCar(String.valueOf(number));
			return true;
		}).collect(Collectors.toList());
	executorService.invokeAll(carsToPark);
	executorService.shutdown();
}

public static class ParkingService {
	private static final int PARKING_SLOTS = 2;
	private final Semaphore semaphore = new Semaphore(PARKING_SLOTS);
	private final Random random = new Random();

	public void parkCar(String carNumber) throws InterruptedException {
		// park car
		semaphore.acquire();
		System.out.println("Car " + carNumber + " was parked");
		// stay car
		Thread.sleep(random.nextInt(100) + 10);
		// leave parking
		System.out.println("Car " + carNumber + " left parking");
		semaphore.release();
	}
}
```

<h3 id="count-down-latch"> CountDownLatch </h3>

***CountDownLatch*** is a synchronization tool that allows one or more threads to wait until a set of operations being performed in other threads completes. A CountDownLatch is initialized with a given _count_. Method `countDown()` is used to decrement the count of the latch, method `await()` is used to cause the current thread to wait until the latch has counted down to zero or the specified waiting time elapses. You **can not reuse** CountDownLatch once the count is reaches zero.

For example, server-side core Java application that uses services architecture, where multiple services are provided by multiple threads and the application can not start processing until all services have started successfully.

```java
public static void main(String[] args) throws InterruptedException {
	CountDownLatch countDownLatch = new CountDownLatch(4);
	ExecutorService executorService = Executors.newCachedThreadPool();
	executorService.submit(new InitializeService(countDownLatch, "AuthorizationService"));
	executorService.submit(new InitializeService(countDownLatch, "ValidationService"));
	executorService.submit(new InitializeService(countDownLatch, "CustomerService"));
	executorService.submit(new InitializeService(countDownLatch, "OrderService"));

	if (countDownLatch.await(10, TimeUnit.SECONDS)) {
		// wait until all services will be initialized
		System.out.println("Application is started");
	} else {
		System.out.println("Not all services were initialized");
	}

	executorService.shutdown();
}

private class InitializeService implements Runnable {

	private final CountDownLatch countDownLatch;
	private final String serviceName;

	public InitializeService(CountDownLatch countDownLatch, String name) {
		this.serviceName = name;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		System.out.println("Start of initializing " + serviceName);
		try {
			Thread.sleep(new Random().nextInt(1000));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		System.out.println(serviceName + " is initialized");
		countDownLatch.countDown();
	}
}
```

<h3 id="cyclic-barrier"> CyclicBarrier </h3>

***CyclicBarrier*** is synchronization tool that allows a set of threads to all wait for each other to reach a common barrier point. A CyclicBarrier is initialized with a given _count_ and _Runnable task_ that will be executed after enough threads reach a barrier. Method `await()` is used to register that a certain thread has reached the barrier point. It **can be re-used** after the waiting threads are released.

CyclicBarriers are used in programs in which we have a fixed number of threads that must wait for each other to reach a common point before continuing execution.

```java
public static void main(String[] args) {

// after each group of 4 players the game starts
CyclicBarrier cyclicBarrier = new CyclicBarrier(4, () -> System.out.println("Game started"));
	try (ExecutorService executorService = Executors.newCachedThreadPool()) {
		for (int i = 0; i < 20; ++i) {
			executorService.submit(new AddPlayer(cyclicBarrier, "user" + i));
		}
	}
}

private static class AddPlayer implements Runnable {
	private final CyclicBarrier cyclicBarrier;
	private final String username;

	public AddPlayer(CyclicBarrier cyclicBarrier, String username) {
		this.cyclicBarrier = cyclicBarrier;
		this.username = username;
	}

	@Override
	public void run() {
		Thread.sleep(new Random().nextInt(100));
		System.out.println("Player " + username + " joined the lobby");
		cyclicBarrier.await();
	}
}
```

### Exchanger

***Exchanger*** is a synchronization tool to share objects between pairs of threads. It has only on method - `exchange()` that is used to wait for another thread to arrive at exchange point and transfer the given object to it, receiving its object in return. Exchangers may be useful in applications such as genetic algorithms and pipeline designs.

```java
// it can be used for a simple pipes
public static void main(String[] args) throws InterruptedException {
	Exchanger<Queue<Integer>> readerExchanger = new Exchanger<>();
	Exchanger<Queue<Integer>> writerExchanger = new Exchanger<>();
	ExecutorService executorService = Executors.newCachedThreadPool();

	executorService.submit(new GenerateTask(readerExchanger, 4));
	executorService.submit(new SquareNumberTask(readerExchanger, writerExchanger));
	executorService.submit(new DisplayNumber(writerExchanger));
}

private static class GenerateTask implements Runnable {
	private final Exchanger<Queue<Integer>> readerExchanger;
	private final Integer limit;
	private final Random random = new Random();
	private Queue<Integer> generatedNumbers = new ConcurrentLinkedQueue<>();

	private GenerateTask(Exchanger<Queue<Integer>> readerExchanger, Integer limit) {
		this.readerExchanger = readerExchanger;
		this.limit = limit;
	}

	@Override
	public void run() {
		while (true) {
			generatedNumbers.add(random.nextInt(100));
			if (generatedNumbers.size() >= limit) {
				generatedNumbers = readerExchanger.exchange(generatedNumbers);
			}
		}
	}
}

private static class SquareNumberTask implements Runnable {
	private final Exchanger<Queue<Integer>> readerExchanger;
	private final Exchanger<Queue<Integer>> writerExchanger;
	private Queue<Integer> generatedNumbers = new ConcurrentLinkedQueue<>();
	private Queue<Integer> processedNumbers = new ConcurrentLinkedQueue<>();

	private SquareNumberTask(Exchanger<Queue<Integer>> readerExchanger, Exchanger<Queue<Integer>> writerExchanger) {
		this.readerExchanger = readerExchanger;
		this.writerExchanger = writerExchanger;
	}

	@Override
	public void run() {
		generatedNumbers = readerExchanger.exchange(generatedNumbers);
		while (true) {
			processedNumbers.add((int) Math.pow(Optional.ofNullable(generatedNumbers.poll()).orElse(0), 2));
			if (generatedNumbers.isEmpty()) {
				processedNumbers = writerExchanger.exchange(processedNumbers);
				generatedNumbers = readerExchanger.exchange(generatedNumbers);
			}
		}
	}
}

private static class DisplayNumber implements Runnable {
	private final Exchanger<Queue<Integer>> writerExchanger;
	private Queue<Integer> processedNumbers = new ConcurrentLinkedQueue<>();

	private DisplayNumber(Exchanger<Queue<Integer>> writerExchanger) {
		this.writerExchanger = writerExchanger;
	}

	@Override
	public void run() {
		processedNumbers = writerExchanger.exchange(processedNumbers);
		while (true) {
			System.out.println("Processed number: " + processedNumbers.poll());
			if (processedNumbers.isEmpty()) {
				processedNumbers = writerExchanger.exchange(processedNumbers);
			}
		}
	}
}
```

### Phaser

***Phaser*** is tool to build logic in which threads need to wait on the barrier before going to the next step of execution. It is similar in functionality to CyclicBarrier and CountDownLatch but supporting more flexible usage. Each phase can have a _different_ number of threads waiting to advance to another phase.

`register()` - is used to add a new unarrived party to phaser. 

`arrive()` - is used to signal that thread arrived at the barrier without waiting for others to arrive.

`arriveAndAwaitAdvance()` - is used to signal that thread arrived at the barrier and await others.

`arriveAndDeregister()` - is used to signal that thread arrived at the barrier and deregister from it without waiting for others to arrive.

`getPhase()` - is used to get number of the current phase.

```java
public static void main(String[] args) {
	ExecutorService executorService = Executors.newCachedThreadPool();

// we can use Phaser to execute task which parts should be completed in different phases
	Phaser phaser = new Phaser();
// on the 1st phase we read data
	executorService.submit(new ReadTask(phaser, "request1.json"));
// on the 2nd phase we create different entities
	executorService.submit(new CreateTask(phaser, "Order", 1));
	executorService.submit(new CreateTask(phaser, "OrderDetails", 1));
// on the 3rd phase we send emails
	executorService.submit(new SendEmailTask(phaser, "customer@mail.com", 2));
	executorService.submit(new SendEmailTask(phaser, "owner@mail.com", 2));
	executorService.shutdown();
}

private static class ReadTask implements Runnable {
	private final Phaser phaser;
	private final String file;

	private ReadTask(Phaser phaser, String file) {
		this.phaser = phaser;
		this.file = file;
		phaser.register();
	}

	@Override
	public void run() {
		Thread.sleep(100);
		System.out.println("Read request from file " + file);
		phaser.arriveAndDeregister();
	}
}

private static class CreateTask implements Runnable {
	private final Phaser phaser;
	private final String entity;
	private final Integer phase;

	private CreateTask(Phaser phaser, String entity, Integer phase) {
		this.phaser = phaser;
		this.entity = entity;
		this.phase = phase;
		phaser.register();
	}

	@Override
	public void run() {
		while (phaser.getPhase() < phase) {
			phaser.arriveAndAwaitAdvance();
		}
		Thread.sleep(new Random().nextInt(200));
		System.out.println("Created " + entity);
		phaser.arriveAndDeregister();
	}
}

private static class SendEmailTask implements Runnable {
	private final Phaser phaser;
	private final String receiver;
	private final Integer phase;

	private SendEmailTask(Phaser phaser, String receiver, Integer phase) {
		this.phaser = phaser;
		this.receiver = receiver;
		this.phase = phase;
		phaser.register();
	}

	@Override
	public void run() {
		while (phaser.getPhase() < phase) {
			phaser.arriveAndAwaitAdvance();
		}
		Thread.sleep(new Random().nextInt(50));
		System.out.println("Send email to " + receiver);
		phaser.arriveAndDeregister();
	}
}

```

<h2 id="virtual-threads"> Virtual Threads </h2>

***Virtual thread*** is and instance of Java Thread object but it is fully managed by JVM and garbage collector and isn't tied to a specific OS thread. A virtual thread still runs code on an OS thread. However, when code running in a virtual thread calls a blocking I/O operation, the Java runtime suspends the virtual thread until it can be resumed. The OS thread associated with the suspended virtual thread is now free to perform operations for other virtual threads.

Can be created using static `Thread.ofVirtual()` method to get thread builder or static `Executors.newVirtualThreadPerTaskExecutor()` to get an executor.

*Best practices*:

1. Virtual threads are good to use for *blocking tasks* (DB, I/O) and aren't intended for long-running CPU-intensive operations. They provide improve in *throughput* but not it latency.
2. Inefficient with short and frequent blocking calls in general (because of time for mounting/unmounting) but more efficient than platform threads (because context switching is more expensive).
3. Never create fixed-size pools of virtual threads. Use `Executors.newVirtualThreadPerTaskExecutor()` and *Semaphores* for limited resources instead of fixed thread pools.
4. Virtual threads are always *daemon* threads. `setDaemon()` method throws an exception.
5. Virtual threads always have *default* priority. `setPriority()` methods does nothing.
