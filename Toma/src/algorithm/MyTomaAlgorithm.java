package algorithm;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import problem.Direction;
import problem.Monomer;
import problem.MonomerType;
import problem.Pair;
import problem.Protein;

public class MyTomaAlgorithm extends TomaAlgorithm {

	private Direction		mOldDirectionOfI;
	private List<Vector2d>	mTempPositions;
	private Vector2d		mTempVector;
	private List<Pair>		mLoops;

	public MyTomaAlgorithm() {
		super();
		initDataStructures();
	}

	public MyTomaAlgorithm(Protein pProblem) {
		super(pProblem);
		initDataStructures();
	}

	private void initDataStructures() {
		mTempPositions = new ArrayList<Vector2d>(mProblem.getN());
		mTempVector = new Vector2d();
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

		double x = Math.exp(mProblem.getMonomer(pI).getMobility()
				/ mProblem.getCk());

		return rnd < x;
	}

	@Override
	protected void performRandomlyMovement(int pI) {
		// choose Theta(i), while taking as invariant all other Theta
		// coordinates (this corresponds to a pivot move)

		// is this ok?.. or should we choosee only 1 or -1?..

		double rnd = mProblem.getRandom().nextDouble();

		Direction newThetaI = Direction.AHEAD;

		if (rnd <= 0.3)
			newThetaI = Direction.LEFT;

		else if (rnd >= 0.6)
			newThetaI = Direction.RIGHT;

		mOldDirectionOfI = mProblem.getMonomer(pI).getDirection();

		mProblem.getMonomer(pI).setDirection(newThetaI);
	}

	@Override
	protected boolean isTheStructureValid(int pI) {
		// we can check collisions only between x,y s.t. x from X and y
		// from Y. X = {monomers before i (include i)}, Y = {monomers after i}

		// improvement: we can calc the Rectangles of those two Sets, and only
		// when they collide we will perform the big check

		calcPositionsStartingFromI(pI);

		mTempPositions.clear();

		for (int i = 0; i <= pI; i++)
			mTempPositions.add(mProblem.getMonomer(pI).getPosition());

		for (int i = pI + 1; i < mProblem.getN(); i++)
			if (mTempPositions.contains(mProblem.getMonomer(pI).getPosition()))
				return false;

		return true;
	}

	private void calcPositionsStartingFromI(int pI) {

		List<Monomer> monomers = mProblem.getMonomers();

		if (0 == pI)
			pI++;

		for (int i = pI; i < mProblem.getN() - 1; i++) {

			Vector2d pointIminusOne = monomers.get(i - 1).getPosition();
			Vector2d pointI = monomers.get(i).getPosition();

			mTempVector.sub(pointI, pointIminusOne);

			// TODO: refactor it:

			Direction directionOfI = monomers.get(i).getDirection();

			// we are now changing the (i+1)th Monomer

			// right
			if (mTempVector.getX() == 1) {

				mTempVector.setX((0 == directionOfI.mDirection) ? mTempVector
						.getX() : 0);
				mTempVector.setY(mTempVector.getY() + directionOfI.mDirection);
			}

			// left
			else if (mTempVector.getX() == -1) {

				mTempVector.setX((0 == directionOfI.mDirection) ? mTempVector
						.getX() : 0);
				mTempVector.setY(mTempVector.getY() - directionOfI.mDirection);
			}

			// up
			else if (mTempVector.getY() == 1) {

				mTempVector.setX(mTempVector.getX() - directionOfI.mDirection);
				mTempVector.setY((0 == directionOfI.mDirection) ? mTempVector
						.getY() : 0);
			}

			// up
			else if (mTempVector.getY() == 1) {

				mTempVector.setX(mTempVector.getX() + directionOfI.mDirection);
				mTempVector.setY((0 == directionOfI.mDirection) ? mTempVector
						.getY() : 0);
			}

			monomers.get(i + 1).getPosition().set(
					pointI.getX() + mTempVector.getX(),
					pointI.getY() + mTempVector.getY());
		}
	}

	@Override
	protected void evaluateStructureEnergy() {

		mTempPositions.clear();
		mLoops.clear();

		for (int i = 0; i < mProblem.getN(); i++)
			if (mProblem.getMonomer(i).getType() == MonomerType.H)
				mTempPositions.add(mProblem.getMonomer(i).getPosition());

		for (int i = 0; i < mTempPositions.size(); i++) {

			Vector2d x = mTempPositions.get(i);

			for (int j = i + 1; j < mTempPositions.size(); j++) {

				Vector2d y = mTempPositions.get(j);

				if (!mProblem.isNeighbors(x, y))
					mLoops.add(new Pair(mProblem.getMonomerFromVector2d(x),
							mProblem.getMonomerFromVector2d(y)));
			}
		}

		mProblem.setE(-mLoops.size());
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

		for (Pair loop : mLoops) {

			int from = loop.getFirst().getIndex();
			int to = loop.getSecond().getIndex();

			if (from > to) {
				tmp = to;
				to = from;
				from = tmp;
			}

			length = to - from;

			for (int i = from; i <= to; i++)
				mProblem.getMonomer(i).decreaseMobility(
						mProblem.getCoolingValue(length));
		}
	}

	@Override
	protected void restoreStructure(int pI) {
		mProblem.getMonomer(pI).setDirection(mOldDirectionOfI);
		calcPositionsStartingFromI(pI);
	}
}
