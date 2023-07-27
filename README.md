# Java Multithreading Concepts &amp; Examples

_Multithreading_ is the ability of a CPU to execute multiple threads independently at the same time but share the process resources simultaneously.

It has 2 main motivations: _responsiveness_ that is achived by concurrency and _highest perfomance_ that is achived by parallelism. 

Multithreading in Java is a feature that allows you to subdivide the specific program into two or more threads to make the execution of the program fast and easy.

## Plan
1. [Threads](#threads)
   - [creation](#creation)
   - [basic methods](#methods)
   - data sharing
   - [daemon thread](#daemon)
2. Runnable and Callable
3. Executor
4. Synchronization
5. Locking
6. Inter-thread communication
   - Semaphore
   - CyclicBarrier
   - CountDownLatch
7. Virtual Threads
8. Thread Scheduler

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
