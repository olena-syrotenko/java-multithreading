# Java Multithreading Concepts &amp; Examples

_Multithreading_ is the ability of a CPU to execute multiple threads independently at the same time but share the process resources simultaneously.

It has 2 main motivations: _responsiveness_ that is achived by concurrency and _highest perfomance_ that is achived by parallelism. 

Multithreading in Java is a feature that allows you to subdivide the specific program into two or more threads to make the execution of the program fast and easy.

## Plan
1. [Threads](#threads)
   - [creation](#creation)
   - [basic methods](#methods)
   - data sharing
   - Daemon Thread
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
