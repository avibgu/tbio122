package algorithm;

import java.util.Date;

import main.Main;

import problem.Protein;

public abstract class TomaAlgorithm implements Runnable {

	protected Protein mProtein;
	protected boolean mInitialized;
	protected int mNumOfMoves;

	public TomaAlgorithm() {
		mInitialized = false;
	}

	public TomaAlgorithm(Protein pProtein) {
		this();
		init(pProtein);
	}

	public void init(Protein pProtein) {
		mProtein = pProtein;
		mInitialized = true;
		mNumOfMoves = 0;
	}

	@Override
	public void run() {

		if (!mInitialized) {

			System.err.println("Initialize this algorith with a problem");
			return;
		}

		int numOfRuns = Integer.parseInt(Main.prop.getProperty("NUM_OF_RUNS"));

		mProtein.setStartTime(new Date());

		// i Assume that the initial state is extended structure

		while (numOfRuns-- > 0) {

			//TODO: just for debug..
			System.out.println(numOfRuns + ": " + mProtein);
			
			int i = selectResidueRandomly();

			if (shouldWeMoveIt(i)) {

				performRandomlyMovement(i);

				if (isTheStructureValid(i)) {

					evaluateStructureEnergy();
					countThisMove();
					decreaseTemperature();
					updateF();
				} else
					restoreStructure(i);
			}
		}

		mProtein.setEndTime(new Date());

		mProtein.markAsSolved();
	}

	protected void countThisMove() {
		mNumOfMoves++;
	}

	protected abstract int selectResidueRandomly();

	protected abstract boolean shouldWeMoveIt(int i);

	protected abstract void performRandomlyMovement(int i);

	protected abstract boolean isTheStructureValid(int i);

	protected abstract void evaluateStructureEnergy();

	protected abstract void decreaseTemperature();

	protected abstract void updateF();

	protected abstract void restoreStructure(int pI);
}
