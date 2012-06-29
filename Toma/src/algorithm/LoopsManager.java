package algorithm;

public class LoopsManager {

	private double[] mLoopsStarts;
	private double[] mLoopsEnds;

	public LoopsManager(int pNumOfMonomers) {

		mLoopsStarts = new double[pNumOfMonomers];
		mLoopsEnds = new double[pNumOfMonomers];

		clear();
	}

	public void markLoop(int pFromIndex, int pToIndex) {

		double coolingValue = MobilityDecreasingStrategy
				.MobilityDecreasingValue(pToIndex - pFromIndex);

		// "a loop of length (toIndex - fromIndex) starts from me.."
		mLoopsStarts[pFromIndex] += coolingValue;

		// "a loop of length (toIndex - fromIndex) ends at me.."
		mLoopsEnds[pToIndex] += coolingValue;
	}

	public void clear() {

		for (int i = 0; i < mLoopsStarts.length; i++) {
			mLoopsStarts[i] = 0;
			mLoopsEnds[i] = 0;
		}
	}

	public double getLoopsStartsCoolingValue(int pIndex) {
		return mLoopsStarts[pIndex];
	}

	public double getLoopsEndsCoolingValue(int pIndex) {
		return mLoopsEnds[pIndex];
	}
}
