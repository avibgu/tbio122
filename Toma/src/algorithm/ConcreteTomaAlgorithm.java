package algorithm;

import java.util.ArrayList;
import java.util.List;

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
	private List<Vector2d> mPotencialsNeighbors;
	private List<Pair> mLoops;
	
	public ConcreteTomaAlgorithm() {
		super();
		initDataStructures();
	}

	public ConcreteTomaAlgorithm(Protein pProtein) {
		super(pProtein);
		initDataStructures();
	}

	private void initDataStructures() {
		
		mTempPositions = new ArrayList<Vector2d>(mProtein.getNumOfMonomers());
		mTempVector = new Vector2d();
		
		mPotencialsNeighbors = new ArrayList<Vector2d>(4);
		
		mPotencialsNeighbors.add(0, new Vector2d());
		mPotencialsNeighbors.add(1, new Vector2d());
		mPotencialsNeighbors.add(2, new Vector2d());
		mPotencialsNeighbors.add(3, new Vector2d());
		
		mLoops = new ArrayList<Pair>();
	}

	@Override
	protected int selectResidueRandomly() {
		return (int) (mProtein.getRandom().nextDouble() * (mProtein
				.getNumOfMonomers() - 1));
	}

	@Override
	protected boolean shouldWeMoveIt(int pI) {

		// move it if Rnd < exp[f(i)/ck]

		// the criterion is always satisfied for residues not belonging to loop
		// defined by HH contacts

		double rnd = mProtein.getRandom().nextDouble();

		double x = Math.exp(mProtein.getMonomer(pI).getMobility()
				/ mProtein.getTemperature());

		return rnd < x;
	}

	@Override
	protected void performRandomlyMovement(int pI) {
		// choose Theta(i), while taking as invariant all other Theta
		// coordinates (this corresponds to a pivot move)

		// is this ok?.. or should we choosee only 1 or -1?..

		double rnd = mProtein.getRandom().nextDouble();

		Direction newThetaI = Direction.AHEAD;

		if (rnd <= 0.3)
			newThetaI = Direction.LEFT;

		else if (rnd >= 0.6)
			newThetaI = Direction.RIGHT;

		mOldDirectionOfI = mProtein.getMonomer(pI).getDirection();

		mProtein.getMonomer(pI).setDirection(newThetaI);
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
			mTempPositions.add(mProtein.getMonomer(pI).getPosition());

		for (int i = pI + 1; i < mProtein.getNumOfMonomers(); i++)
			if (mTempPositions.contains(mProtein.getMonomer(pI).getPosition()))
				return false;

		return true;
	}

	private void calcPositionsStartingFromThisMonomer(int pMonomerIndex) {

		if (2 > pMonomerIndex)
			pMonomerIndex = 2;

		Vector2d pointIminusOne = mProtein.getMonomers().get(pMonomerIndex - 1)
				.getPosition();
		Vector2d pointIminusTwo = mProtein.getMonomers().get(pMonomerIndex - 2)
				.getPosition();

		mTempVector.sub(pointIminusOne, pointIminusTwo);

		calcPositionsStartingFromThisMonomerRecursivly(pMonomerIndex,
				pointIminusOne, mTempVector);
	}

	private void calcPositionsStartingFromThisMonomerRecursivly(int pI,
			Vector2d pPointIminusOne, Vector2d pDirectionsVector) {

		if (mProtein.getNumOfMonomers() - 1 == pI)
			return;

		Vector2d pointI = mProtein.getMonomers().get(pI).getPosition();

		Direction directionOfI = mProtein.getMonomers().get(pI).getDirection();

		if (Direction.LEFT == directionOfI)
			turnDirectionVectorLeft(pDirectionsVector);

		else if (Direction.LEFT == directionOfI)
			turnDirectionVectorRight(pDirectionsVector);

		mProtein.getMonomers()
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

		for (int i = 0; i < mProtein.getNumOfMonomers(); i++) {

			Monomer monomer = mProtein.getMonomer(i);

			if (monomer.getType() == MonomerType.P)
				continue;

			calcPotencialsNeighbors(monomer.getPosition());

			for (Vector2d pn : mPotencialsNeighbors) {

				Monomer neighborMonomer = mProtein.getMonomerFromVector2d(pn);

				// i + 1 => for only Monomers that are greater than me, and not
				// connected directly to me
				if (null != neighborMonomer
						&& neighborMonomer.getIndex() > (i + 1)
						&& neighborMonomer.getType() == MonomerType.H)

					// TODO: improve this data-structure.. !!!
					// every Monomer can start or end up to 3 loops
					// (don't use new!!..)
					mLoops.add(new Pair(monomer, neighborMonomer));
			}
		}

		mProtein.setEnergy(-mLoops.size());
	}

	private void calcPotencialsNeighbors(Vector2d pPosition) {

		mPotencialsNeighbors.get(0).set(pPosition.getX() + 1, pPosition.getY());
		mPotencialsNeighbors.get(1).set(pPosition.getX() - 1, pPosition.getY());
		mPotencialsNeighbors.get(2).set(pPosition.getX(), pPosition.getY() + 1);
		mPotencialsNeighbors.get(3).set(pPosition.getX(), pPosition.getY() - 1);
	}

	@Override
	protected void decreaseTemperature() {
		// should be performed due to some cooling strategy
		mProtein.setTemperature(mProtein.getTemperature() * 0.999999);

		// improvement: mProblem.setCk((0.3 * nH) / (0.5 * (nH + nP)));
	}

	@Override
	protected void updateF() {
		// do this for all the residues that participates in loops
		// we should avoid double-counting

		int length;

		for (Pair loop : mLoops) {

			int from = loop.getFirst().getIndex();
			int to = loop.getSecond().getIndex();

			length = to - from;

			for (int i = from; i <= to; i++)
				mProtein.getMonomer(i).decreaseMobility(
						mProtein.getCoolingValue(length));
		}

		// TODO: my project is to improve updateF from O(N^2) to O(N)
	}

	@Override
	protected void restoreStructure(int pI) {
		mProtein.getMonomer(pI).setDirection(mOldDirectionOfI);
		calcPositionsStartingFromThisMonomer(pI);
	}
}
