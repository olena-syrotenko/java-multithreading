package asd.syrotenko.tasks;

import java.math.BigInteger;

public class CalculateFactorial extends Thread {
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