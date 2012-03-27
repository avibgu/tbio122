package algorithm;

import java.util.Date;

import problem.Problem;

public abstract class Algorithm implements Runnable {

	protected Problem mProblem;
	protected boolean mInitialized;

	public Algorithm() {
		mInitialized = false;
	}

	public Algorithm(Problem pProblem) {
		this();
		init(pProblem);
	}

	public void init(Problem pProblem) {
		mProblem = pProblem;
		mInitialized = true;
	}

	@Override
	public void run() {

		if (!mInitialized) {

			System.err.println("Initialize this algorith with a problem");
			return;
		}

		int numOfRuns = 0;	//TODO: get it from the configuration file?..
		
		mProblem.setStartTime(new Date());
		
		while(numOfRuns-- > 0){
			
			doSomething1();
			doSomething2();
			doSomething3();
		}
		
		mProblem.setEndTime(new Date());
		
		mProblem.markAsSolved();
	}

	protected abstract void doSomething3();
	protected abstract void doSomething2();
	protected abstract void doSomething1();
}
