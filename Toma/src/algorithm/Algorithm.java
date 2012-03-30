package algorithm;

import java.util.Date;

import problem.Problem;

public abstract class Algorithm implements Runnable {

	protected Problem	mProblem;
	protected boolean	mInitialized;
	protected int 		mNumOfMoves;

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
		mNumOfMoves = 0;
	}

	@Override
	public void run() {

		if (!mInitialized) {

			System.err.println("Initialize this algorith with a problem");
			return;
		}

		int numOfRuns = 0;	//TODO: get it from the configuration file?..
		
		mProblem.setStartTime(new Date());
		
		// i Assume that the initial state is extended structure
		
		while(numOfRuns-- > 0){
			
			int i = selectResidueRandomly();	// i think they meant monomer..
			
			if (shouldWeMoveIt(i)){
				
				performRandomlyMovement(i);
				
				if (isTheStructureValid()){
					
					evaluateStructureEnergy();
					countThisMove();
					decreaseTemperature();
					updateF();
				}
				else
					restoreStructure();
					
			}
		}
		
		mProblem.setEndTime(new Date());
		
		mProblem.markAsSolved();
	}

	protected void countThisMove() {
		mNumOfMoves++;
	}

	protected abstract int		selectResidueRandomly();
	protected abstract boolean	shouldWeMoveIt(int i);
	protected abstract void		performRandomlyMovement(int i);
	protected abstract boolean	isTheStructureValid();
	protected abstract void		evaluateStructureEnergy();
	protected abstract void		decreaseTemperature();
	protected abstract void		updateF();
	protected abstract void		restoreStructure();
}
