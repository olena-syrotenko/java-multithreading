package asd.syrotenko;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LatencyExample {

	private static final String ORIGINAL_IMAGE_PATH = "optimization/src/main/resources/original_dress.jpg";
	private static final String RESULT_IMAGE_PATH = "optimization/src/main/resources/result_dress.jpg";

	public static void main(String[] args) throws IOException {
		BufferedImage originalImage = ImageIO.read(new File(ORIGINAL_IMAGE_PATH));
		BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

		// takes 1490 millis
		// recolorInSingleThread(originalImage, resultImage);

		// with 2 threads - 977 millis
		// with 4 threads - 831 millis
		// with 6 threads - 790 millis
		// with 8 threads - 847 millis
		recolorInMultiThread(originalImage, resultImage, 6);

		ImageIO.write(resultImage, "jpg", new File(RESULT_IMAGE_PATH));
	}

	public static void recolorInMultiThread(BufferedImage originalImage, BufferedImage resultImage, int threadsCount) {
		List<Thread> threads = new ArrayList<>();
		int width = originalImage.getWidth();
		int height = originalImage.getHeight() / threadsCount;

		for (int i = 0; i < threadsCount; ++i) {
			final int threadNumber = i;
			threads.add(new Thread(() -> recolorImage(originalImage, resultImage, 0, threadNumber * height, width, height)));
		}

		threads.forEach(Thread::start);
		threads.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void recolorInSingleThread(BufferedImage originalImage, BufferedImage resultImage) {
		recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
	}

	public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int left, int top, int width, int height) {
		for (int x = left; x < left + width && x < originalImage.getWidth(); ++x) {
			for (int y = top; y < top + height && y < originalImage.getHeight(); ++y) {
				recolorPixel(originalImage, resultImage, x, y);
			}
		}
	}

	public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
		int rgb = originalImage.getRGB(x, y);

		int red = getColor(rgb, ColorEnum.RED);
		int green = getColor(rgb, ColorEnum.GREEN);
		int blue = getColor(rgb, ColorEnum.BLUE);

		if (isShadeOfBrightRed(red, green, blue)) {
			red = Math.max(0, red - 10);
			green = Math.min(255, green + 50);
			blue = Math.min(255, blue + 150);
		}

		resultImage.getRaster().setDataElements(x, y, resultImage.getColorModel().getDataElements(createRgb(red, green, blue), null));
	}

	public static boolean isShadeOfBrightRed(int red, int green, int blue) {
		return red >= 195 && green <= 95 && blue <= 95;
	}

	public static int createRgb(int red, int green, int blue) {
		return ColorEnum.ALPHA.getMask() | (red << ColorEnum.RED.getShift()) | (green << ColorEnum.GREEN.getShift()) | blue;
	}

	public static int getColor(int rgb, ColorEnum color) {
		return (rgb & color.getMask()) >> color.getShift();
	}

}
