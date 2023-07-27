package asd.syrotenko;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MultiCalculationExample {

	public static void main(String[] args) {
		BigInteger factorialSumResult = Calculator.factorialSum(2, 10, 50, 100);
		if (factorialSumResult != null) {
			System.out.println("Factorial sum of 2, 10, 50 and 100 is " + factorialSumResult);
		}
	}

	public static class Calculator {
		public static BigInteger factorialSum(Integer... numbers) {
			List<CalculateFactorial> calculationThreads = Arrays.stream(numbers).map(CalculateFactorial::new).collect(Collectors.toList());
			calculationThreads.forEach(Thread::start);
			try {
				for (CalculateFactorial calculationThread : calculationThreads) {
					calculationThread.join(200);
				}
			} catch (InterruptedException e) {
				System.out.println("Calculation was interrupted");
				return null;
			}

			return calculationThreads.stream().map(CalculateFactorial::getResult).reduce(BigInteger.ZERO, BigInteger::add);
		}
	}

	public static class CalculateFactorial extends Thread {
		private BigInteger result = BigInteger.ONE;
		private Integer number = 0;

		public CalculateFactorial(Integer number) {
			this.number = number;
		}

		@Override
		public void run() {
			for (int i = 1; i <= number; ++i) {
				result = result.multiply(BigInteger.valueOf(i));
			}
		}

		public BigInteger getResult() {
			return result;
		}
	}
}
