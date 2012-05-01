package algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import problem.Pair;
import problem.ProteinProblem;

public class Toma extends Algorithm {

	private int mOldThetaI;
	private List<Point> mTempPositions;
	private List<Pair> mLoops;

	public Toma() {
		super();
		initDataStructures();
	}

	public Toma(ProteinProblem pProblem) {
		super(pProblem);
		initDataStructures();
	}

	private void initDataStructures() {
		mTempPositions = new ArrayList<Point>(mProblem.getN());
		mLoops = new ArrayList<Pair>();
	}
	
	@Override
	protected int selectResidueRandomly() {
		return (int) (mProblem.getRandom().nextDouble() * (mProblem.getN() - 1));
	}

	@Override
	protected boolean shouldWeMoveIt(int pI) {

		// move it if Rnd < exp[f(i)/ck]

		// the criterion is always satisfied for residues not belonging to loop
		// defined by HH contacts

		double rnd = mProblem.getRandom().nextDouble();

		double x = Math.exp(mProblem.getF(pI) / mProblem.getCk());

		return rnd < x;
	}

	@Override
	protected void performRandomlyMovement(int pI) {
		// choose Theta(i), while taking as invariant all other Theta
		// coordinates (this corresponds to a pivot move)

		// is this ok?.. or should we choosee only 1 or -1?..

		double rnd = mProblem.getRandom().nextDouble();

		int newThetaI = 0;

		if (rnd <= 0.3)
			newThetaI = -1;

		else if (rnd >= 0.6)
			newThetaI = 1;

		mOldThetaI = mProblem.getThetaI(pI);

		mProblem.setThetaI(pI, newThetaI);
	}

	@Override
	protected boolean isTheStructureValid(int pI) {
		// we can check collisions only between x,y s.t. x from X and y
		// from Y. X = {monomers before i (include i)}, Y = {monomers after i}

		// improvement: we can calc the Rectangles of those two Sets, and only
		// when they collide we will perform the big check

		mProblem.calcPositionsStartingFromI(pI);

		mTempPositions.clear();

		for (int i = 0; i <= pI; i++)
			mTempPositions.add(mProblem.getPosition(i));

		for (int i = pI + 1; i < mProblem.getN(); i++)
			if (mTempPositions.contains(mProblem.getPosition(i)))
				return false;

		return true;
	}

	@Override
	protected void evaluateStructureEnergy() {

		mTempPositions.clear();
		mLoops.clear();

		for (int i = 0; i < mProblem.getN(); i++)
			if (mProblem.getType(i) == 'H')
				mTempPositions.add(mProblem.getPosition(i));

		for (int i = 0; i < mTempPositions.size(); i++) {

			Point x = mTempPositions.get(i);

			for (int j = i + 1; j < mTempPositions.size(); j++)
				if (!mProblem.isNeighbors(x, mTempPositions.get(j)))
					mLoops.add(new Pair(i,j));
		}

		mProblem.setE(mLoops.size());
	}

	@Override
	protected void decreaseTemperature() {
		// should be performed due to some cooling strategy
		mProblem.setCk(mProblem.getCk() * 0.999999);

		// improvement: mProblem.setCk((0.3 * nH) / (0.5 * (nH + nP)));
	}

	@Override
	protected void updateF() {
		// do this for all the residues that participates in loops
		// we should avoid double-counting
		
		int tmp;
		int length;
		
		for (Pair loop : mLoops){
			
			int from = loop.getFirst();
			int to = loop.getSecond();
			
			if (from > to){
				tmp = to;
				to = from;
				from = tmp;
			}
			
			length = to - from;
			
			for (int i = from; i <= to; i++)
				mProblem.decreaseF(i, mProblem.getG(length));
		}
	}

	@Override
	protected void restoreStructure(int pI) {
		mProblem.setThetaI(pI, mOldThetaI);
	}
}
