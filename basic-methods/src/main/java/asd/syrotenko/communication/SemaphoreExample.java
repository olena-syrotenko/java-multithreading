package asd.syrotenko.communication;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SemaphoreExample {

	public static void main(String[] args) throws InterruptedException {
		ParkingService parkingService = new ParkingService();
		ExecutorService executorService = Executors.newCachedThreadPool();

		// only first two cars will be parked, then next three cars stay in queue and wait for previous cars leaving parking slot
		List<Callable<Boolean>> carsToPark = IntStream.range(1, 6).mapToObj(number -> new ParkCar(parkingService, String.valueOf(number)))
				.collect(Collectors.toList());
		executorService.invokeAll(carsToPark);
		executorService.shutdown();
	}

	public static class ParkCar implements Callable<Boolean> {
		private final ParkingService parkingService;
		private final String carNumber;

		public ParkCar(ParkingService parkingService, String carNumber) {
			this.parkingService = parkingService;
			this.carNumber = carNumber;
		}

		@Override
		public Boolean call() {
			try {
				System.out.println("Car " + carNumber + " in parking queue");
				parkingService.parkCar(carNumber);
			} catch (InterruptedException e) {
			}
			return true;
		}
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

}
