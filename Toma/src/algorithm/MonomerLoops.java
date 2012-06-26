package algorithm;

import java.util.ArrayList;

import problem.Protein;

public class MonomerLoops {

	private static final int MAX_NUM_OF_LOOPS = 3;
	
	private Protein mProtein;
	
	private ArrayList<Integer> mLoopsLengths;
	private int mIndex;
	
	public MonomerLoops(Protein pProtein) {
		
		mProtein = pProtein;
		
		mLoopsLengths = new ArrayList<Integer>(MAX_NUM_OF_LOOPS);

		for (int i = 0; i < MAX_NUM_OF_LOOPS; i++)
			mLoopsLengths.add(-1);
		
		clear();
	}
	
	public void add(int pLoopLength) {
		mLoopsLengths.set(mIndex++, pLoopLength);
	}

	public void clear() {
		mIndex = 0;
	}

	public int getCoolingValue() {
		
		int sum = 0;
		
		for (int i = 0; i < mIndex; i++)
			sum += mProtein.getCoolingValue(mLoopsLengths.get(i));
		
		return sum;
	}
}
