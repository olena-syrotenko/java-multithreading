# Java Multithreading Concepts &amp; Examples

_Multithreading_ is the ability of a CPU to execute multiple threads independently at the same time but share the process resources simultaneously.

It has 2 main motivations: _responsiveness_ that is achived by concurrency and _highest perfomance_ that is achived by parallelism. 

Multithreading in Java is a feature that allows you to subdivide the specific program into two or more threads to make the execution of the program fast and easy.

## Plan
1. [Threads](#threads)
   - creation
   - basic methods
   - thread priority
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


