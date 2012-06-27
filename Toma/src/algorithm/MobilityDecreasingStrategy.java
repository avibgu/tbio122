package algorithm;

public class MobilityDecreasingStrategy {

	// G function from the paper
	public static double MobilityDecreasingValue(int pLoopLength) {
		return 20 / (pLoopLength + 17);
	}
}
