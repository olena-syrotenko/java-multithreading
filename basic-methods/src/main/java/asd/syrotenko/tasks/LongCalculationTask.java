package asd.syrotenko.tasks;

import java.math.BigInteger;

public class LongCalculationTask implements Runnable {
	@Override
	public void run() {
		BigInteger number = new BigInteger("1567892");
		BigInteger result = BigInteger.ONE;
		for (int i = 0; i <= 10000; ++i) {
			result = result.multiply(number);
		}
		System.out.println("After long calculation result is " + result);
	}
}
