package problem;

import java.util.Date;
import java.util.Random;

import main.Main;

public abstract class Problem {

	protected boolean	mSolved;
	protected Date		mStartTime; 
	protected Date		mEndTime;
	protected Random	mRandom;
	
	public Problem() {
		init();
	}
	
	public void init() {

		markAsUnsolved();
		setStartTime(null);
		setEndTime(null);
		setRandom(new Random(Integer.parseInt(Main.prop.getProperty("SEED"))));
	}
	
	public boolean isSolved(){
		return mSolved;
	}

	public void setStartTime(Date pDate) {
		mStartTime = pDate;		
	}

	public void setEndTime(Date pDate) {
		mEndTime = pDate;
	}

	public void markAsSolved() {
		mSolved = true;
	}
	
	public void markAsUnsolved() {
		mSolved = false;
	}

	public Random getRandom() {
		return mRandom;
	}

	public void setRandom(Random pRandom) {
		mRandom = pRandom;
	}
	
	public void printSolution() {
		// TODO Auto-generated method stub
	}
}
