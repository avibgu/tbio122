package problem;

import java.util.Date;

public abstract class Problem {

	protected boolean mSolved;
	protected Date mStartTime; 
	protected Date mEndTime;
	
	public Problem() {
		init();
	}
	
	public void init(){
		
		markAsUnsolved();
		setStartTime(null);
		setEndTime(null);
	}
	
	public boolean isSolved(){
		return mSolved;
	}

	public void setStartTime(Date date) {
		mStartTime = date;		
	}

	public void setEndTime(Date date) {
		mEndTime = date;
	}

	public void markAsSolved() {
		mSolved = true;
	}
	
	public void markAsUnsolved() {
		mSolved = false;
	}

	public void printSolution() {
		// TODO Auto-generated method stub
		
	}
}
