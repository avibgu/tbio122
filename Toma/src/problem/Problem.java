package problem;

import java.util.Date;
import java.util.Random;

import main.Main;

public abstract class Problem {

	protected boolean	mSolved;
	protected Date		mStartTime;
	protected Date		mEndTime;
	protected Random	mRandom;
	protected String	mResults;

	public Problem() {
		init();
	}

	public void init() {

		markAsUnsolved();
		setStartTime(null);
		setEndTime(null);
		setRandom(new Random(Integer.parseInt(Main.prop.getProperty("SEED"))));
		setResults("");
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

	public String getResults() {
		return mResults;
	}

	public void setResults(String pResults) {
		mResults = pResults;
	}

	public Date getStartTime() {
		return mStartTime;
	}

	public Date getEndTime() {
		return mEndTime;
	}

	public void setSolved(boolean pSolved) {
		mSolved = pSolved;
	}
}
