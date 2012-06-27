package algorithm;

public class CollingStrategy {

	// G function from the paper
	public static double getCoolingValue(int pLoopLength) {
		return 20 / (pLoopLength + 17);
	}
}
