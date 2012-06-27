package algorithm;

public class LoopsManager {

	private double[] mLoopsStarts;
	private double[] mLoopsEnds;

	public LoopsManager(int pNumOfMonomers) {

		mLoopsStarts = new double[pNumOfMonomers];
		mLoopsEnds = new double[pNumOfMonomers];

		clear();
	}

	public void markLoop(int fromIndex, int toIndex) {

		double coolingValue = MobilityDecreasingStrategy.MobilityDecreasingValue(toIndex - fromIndex);

		// "a loop of length (toIndex - fromIndex) starts from me.."
		mLoopsStarts[fromIndex] += coolingValue;

		// "a loop of length (toIndex - fromIndex) ends at me.."
		mLoopsEnds[toIndex] += coolingValue;
	}

	public void clear() {

		for (int i = 0; i < mLoopsStarts.length; i++){
			mLoopsStarts[i] = 0;
			mLoopsEnds[i] = 0;
		}
	}

	public double getLoopsStartsCoolingValue(int pI) {
		return mLoopsStarts[pI];
	}

	public double getLoopsEndsCoolingValue(int pI) {
		return mLoopsEnds[pI];
	}
}
