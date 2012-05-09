package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.vecmath.Vector2d;

import problem.Direction;
import problem.Monomer;
import problem.MonomerType;
import problem.Pair;
import problem.Protein;

public class ConcreteTomaAlgorithm extends TomaAlgorithm {

	private Direction mOldDirectionOfI;
	private List<Vector2d> mTempPositions;
	private Vector2d mTempVector;
	private List<Pair> mLoops;

	public ConcreteTomaAlgorithm() {
		super();
		initDataStructures();
	}

	public ConcreteTomaAlgorithm(Protein pProblem) {
		super(pProblem);
		initDataStructures();
	}

	private void initDataStructures() {
		mTempPositions = new ArrayList<Vector2d>(mProblem.getNumOfMonomers());
		mTempVector = new Vector2d();
		mLoops = new ArrayList<Pair>();
	}

	@Override
	protected int selectResidueRandomly() {
		return (int) (mProblem.getRandom().nextDouble() * (mProblem
				.getNumOfMonomers() - 1));
	}

	@Override
	protected boolean shouldWeMoveIt(int pI) {

		// move it if Rnd < exp[f(i)/ck]

		// the criterion is always satisfied for residues not belonging to loop
		// defined by HH contacts

		double rnd = mProblem.getRandom().nextDouble();

		double x = Math.exp(mProblem.getMonomer(pI).getMobility()
				/ mProblem.getTemperature());

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

		calcPositionsStartingFromThisMonomer(pI);

		mTempPositions.clear();

		for (int i = 0; i <= pI; i++)
			mTempPositions.add(mProblem.getMonomer(pI).getPosition());

		for (int i = pI + 1; i < mProblem.getNumOfMonomers(); i++)
			if (mTempPositions.contains(mProblem.getMonomer(pI).getPosition()))
				return false;

		return true;
	}

	private void calcPositionsStartingFromThisMonomer(int pMonomerIndex) {

		if (2 > pMonomerIndex)
			pMonomerIndex = 2;

		Vector2d pointIminusOne = mProblem.getMonomers().get(pMonomerIndex - 1)
				.getPosition();
		Vector2d pointIminusTwo = mProblem.getMonomers().get(pMonomerIndex - 2)
				.getPosition();

		mTempVector.sub(pointIminusOne, pointIminusTwo);

		calcPositionsStartingFromThisMonomerRecursivly(pMonomerIndex,
				pointIminusOne, mTempVector);
	}

	private void calcPositionsStartingFromThisMonomerRecursivly(int pI,
			Vector2d pPointIminusOne, Vector2d pDirectionsVector) {

		if (mProblem.getNumOfMonomers() - 1 == pI)
			return;

		Vector2d pointI = mProblem.getMonomers().get(pI).getPosition();

		Direction directionOfI = mProblem.getMonomers().get(pI).getDirection();

		if (Direction.LEFT == directionOfI)
			turnDirectionVectorLeft(pDirectionsVector);

		else if (Direction.LEFT == directionOfI)
			turnDirectionVectorRight(pDirectionsVector);

		mProblem.getMonomers()
				.get(pI + 1)
				.getPosition()
				.set(pointI.getX() + mTempVector.getX(),
						pointI.getY() + mTempVector.getY());

		calcPositionsStartingFromThisMonomerRecursivly(pI + 1, pointI,
				pDirectionsVector);
	}

	private void turnDirectionVectorLeft(Vector2d pDirectionsVector) {

		double x = pDirectionsVector.getX();
		double y = pDirectionsVector.getY();

		pDirectionsVector.set(-y, x);
	}

	private void turnDirectionVectorRight(Vector2d pDirectionsVector) {

		double x = pDirectionsVector.getX();
		double y = pDirectionsVector.getY();

		pDirectionsVector.set(y, -x);
	}

	@Override
	protected void evaluateStructureEnergy() {

		mLoops.clear();

		for (int i = 0; i < mProblem.getNumOfMonomers(); i++) {

			Monomer monomer = mProblem.getMonomer(i);

			if (monomer.getType() == MonomerType.P)
				continue;

			Set<Vector2d> potencialsNeighbors = getPotencialsNeighbors(monomer
					.getPosition());

			for (Vector2d pn : potencialsNeighbors) {

				Monomer neighborMonomer = mProblem.getMonomerFromVector2d(pn);

				// i + 1 => for only monomers that are greater than me, and not
				// connected directly to me
				if (null != neighborMonomer
						&& neighborMonomer.getIndex() > (i + 1)
						&& neighborMonomer.getType() == MonomerType.H)

					mLoops.add(new Pair(monomer, neighborMonomer));
			}
		}

		mProblem.setEnergy(-mLoops.size());
	}

	private Set<Vector2d> getPotencialsNeighbors(Vector2d pPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void decreaseTemperature() {
		// should be performed due to some cooling strategy
		mProblem.setTemperature(mProblem.getTemperature() * 0.999999);

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

		// TODO: my project is improving F..
	}

	@Override
	protected void restoreStructure(int pI) {
		mProblem.getMonomer(pI).setDirection(mOldDirectionOfI);
		calcPositionsStartingFromThisMonomer(pI);
	}
}
