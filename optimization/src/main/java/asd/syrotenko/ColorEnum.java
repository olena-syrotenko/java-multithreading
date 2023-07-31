package asd.syrotenko;

public enum ColorEnum {

	BLUE(0x000000FF, 0), GREEN(0x0000FF00, 8), RED(0x00FF0000, 16), ALPHA(0xFF000000, 32);

	private final int mask;
	private final int shift;

	ColorEnum(int mask, int shift) {
		this.mask = mask;
		this.shift = shift;
	}

	public int getMask() {
		return mask;
	}

	public int getShift() {
		return shift;
	}
}
