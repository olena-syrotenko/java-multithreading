package asd.syrotenko;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiExecutorExample {

	public static void main(String[] args) {
		MultiExecutor multiExecutor = new MultiExecutor();
		multiExecutor.addTask(() -> System.out.println("New task that should be removed"));
		multiExecutor.executeAndRemove();

		multiExecutor.addTaskList(Arrays.asList(new DisplayMessage("Task 1"), new DisplayMessage("Task 2"), new SayHello("Tom")));
		multiExecutor.executeAll();

		multiExecutor.addTask(() -> System.out.println("New task"));
		multiExecutor.executeAll();
	}

	public static class MultiExecutor {
		private List<Runnable> tasks = new ArrayList<>();

		public void addTask(Runnable task) {
			tasks.add(task);
		}

		public void addTaskList(List<Runnable> taskList) {
			tasks.addAll(taskList);
		}

		public void executeAll() {
			tasks.forEach(task -> new Thread(task).start());
		}

		public void executeAndRemove() {
			tasks.forEach(task -> new Thread(task).start());
			tasks.clear();
		}
	}

	public static class DisplayMessage implements Runnable {

		private final String message;

		public DisplayMessage(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			System.out.println(message);
		}
	}

	public static class SayHello implements Runnable {

		private final String user;

		public SayHello(String message) {
			this.user = message;
		}

		@Override
		public void run() {
			System.out.println("Good morning, " + user + "!");
		}
	}

}
