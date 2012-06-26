package algorithm;

import java.util.ArrayList;
import java.util.List;

import problem.Direction;
import problem.Monomer;
import problem.MonomerType;
import problem.Protein;
import utilities.HashedVector2d;

public class ConcreteTomaAlgorithm extends TomaAlgorithm {

	private Direction mOldDirectionOfI;
	private List<HashedVector2d> mTempPositions;
	private HashedVector2d mTempVector;
	private List<HashedVector2d> mPotencialsNeighbors;
	// private List<Pair> mLoops;
	private LoopsManager mLoopsManager;

	public ConcreteTomaAlgorithm() {
		super();
		initDataStructures();
	}

	public ConcreteTomaAlgorithm(Protein pProtein) {
		super(pProtein);
		initDataStructures();
	}

	private void initDataStructures() {

		mTempPositions = new ArrayList<HashedVector2d>(
				mProtein.getNumOfMonomers());
		mTempVector = new HashedVector2d();

		mPotencialsNeighbors = new ArrayList<HashedVector2d>(4);

		mPotencialsNeighbors.add(0, new HashedVector2d());
		mPotencialsNeighbors.add(1, new HashedVector2d());
		mPotencialsNeighbors.add(2, new HashedVector2d());
		mPotencialsNeighbors.add(3, new HashedVector2d());

		// mLoops = new ArrayList<Pair>();

		mLoopsManager = new LoopsManager(mProtein, mProtein.getNumOfMonomers());
	}

	@Override
	protected int selectResidueRandomly() {

		return ((int) (mProtein.getRandom().nextDouble() * 1000))
				% mProtein.getNumOfMonomers();
	}

	@Override
	protected boolean shouldWeMoveIt(int pI) {

		// move it if Rnd < exp[f(i)/ck]

		// the criterion is always satisfied for residues not belonging to loop
		// defined by HH contacts

		if (0 == pI || mProtein.getNumOfMonomers() - 1 == pI)
			return false;

		double rnd = mProtein.getRandom().nextDouble();

		double x = Math.exp(mProtein.getMonomer(pI).getMobility()
				/ mProtein.getTemperature());

		return rnd < x;
	}

	@Override
	protected void performRandomlyMovement(int pI) {
		// choose Theta(i), while taking as invariant all other Theta
		// coordinates (this corresponds to a pivot move)

		int rnd = ((int) (mProtein.getRandom().nextDouble() * 1000)) % 3;

		Direction newThetaI = Direction.AHEAD;

		if (1 == rnd)
			newThetaI = Direction.LEFT;

		else if (2 == rnd)
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
			mTempPositions.add(mProtein.getMonomer(i).getPosition());

		for (int i = pI + 1; i < mProtein.getNumOfMonomers(); i++)
			if (mTempPositions.contains(mProtein.getMonomer(i).getPosition()))
				return false;

		return true;
	}

	private void calcPositionsStartingFromThisMonomer(int pMonomerIndex) {

		if (0 == pMonomerIndex)
			pMonomerIndex = 1;

		HashedVector2d pointI = mProtein.getMonomers().get(pMonomerIndex)
				.getPosition();

		HashedVector2d pointIminusOne = mProtein.getMonomers()
				.get(pMonomerIndex - 1).getPosition();

		mTempVector.sub(pointI, pointIminusOne);

		calcPositionsStartingFromThisMonomerRecursivly(pMonomerIndex,
				pointIminusOne, mTempVector);
	}

	private void calcPositionsStartingFromThisMonomerRecursivly(
			int pMonomerIndex, HashedVector2d pPointIminusOne,
			HashedVector2d pDirectionsVector) {

		if (mProtein.getNumOfMonomers() - 1 == pMonomerIndex)
			return;

		HashedVector2d pointI = mProtein.getMonomers().get(pMonomerIndex)
				.getPosition();

		Direction directionOfI = mProtein.getMonomers().get(pMonomerIndex)
				.getDirection();

		if (Direction.LEFT == directionOfI)
			turnDirectionVectorLeft(pDirectionsVector);

		else if (Direction.RIGHT == directionOfI)
			turnDirectionVectorRight(pDirectionsVector);

		mProtein.setMonomerPosition(pMonomerIndex + 1, pointI.getX()
				+ mTempVector.getX(), pointI.getY() + mTempVector.getY());

		calcPositionsStartingFromThisMonomerRecursivly(pMonomerIndex + 1,
				pointI, pDirectionsVector);
	}

	private void turnDirectionVectorLeft(HashedVector2d pDirectionsVector) {

		double x = pDirectionsVector.getX();
		double y = pDirectionsVector.getY();

		pDirectionsVector.set(-y, x);
	}

	private void turnDirectionVectorRight(HashedVector2d pDirectionsVector) {

		double x = pDirectionsVector.getX();
		double y = pDirectionsVector.getY();

		pDirectionsVector.set(y, -x);
	}

	@Override
	protected void evaluateStructureEnergy() {

		// mLoops.clear();

		mLoopsManager.clear();

		int energy = 0;

		for (int i = 0; i < mProtein.getNumOfMonomers(); i++) {

			Monomer monomer = mProtein.getMonomer(i);

			if (monomer.getType() == MonomerType.P)
				continue;

			calcPotencialsNeighbors(monomer.getPosition());

			for (HashedVector2d pn : mPotencialsNeighbors) {

				Monomer neighborMonomer = mProtein.getMonomerFromVector2d(pn);

				// i + 1 => for only Monomers that are greater than me, and not
				// connected directly to me
				if (null != neighborMonomer
						&& neighborMonomer.getIndex() > (i + 1)
						&& neighborMonomer.getType() == MonomerType.H) {

					// improve this data-structure.. !!!
					// every Monomer can start or end up to 3 loops
					// (don't use new!!..)
					// mLoops.add(new Pair(monomer, neighborMonomer));

					// improvement
					mLoopsManager.markLoop(i, neighborMonomer.getIndex());

					energy--;
				}
			}
		}

		mProtein.setEnergy(energy);
	}

	private void calcPotencialsNeighbors(HashedVector2d pPosition) {

		mPotencialsNeighbors.get(0).set(pPosition.getX() + 1, pPosition.getY());
		mPotencialsNeighbors.get(1).set(pPosition.getX() - 1, pPosition.getY());
		mPotencialsNeighbors.get(2).set(pPosition.getX(), pPosition.getY() + 1);
		mPotencialsNeighbors.get(3).set(pPosition.getX(), pPosition.getY() - 1);
	}

	@Override
	protected void decreaseTemperature() {
		// should be performed due to some cooling strategy
		mProtein.setTemperature(mProtein.getTemperature() * 0.999999);
	}

	@Override
	protected void updateF() {
		// do this for all the residues that participates in loops
		// we should avoid double-counting

		int coolingValue = 0;

		for (int i = 0; i < mProtein.getNumOfMonomers(); i++) {

			coolingValue += mLoopsManager.getLoopsStarts().get(i)
					.getCoolingValue();

			mProtein.getMonomer(i).decreaseMobility(coolingValue);

			coolingValue -= mLoopsManager.getLoopsEnds().get(i)
					.getCoolingValue();
		}
	}

	@Deprecated
	protected void oldUpdateF() {
		// do this for all the residues that participates in loops
		// we should avoid double-counting

		// int length;
		//
		// for (Pair loop : mLoops) {
		//
		// int from = loop.getFirst().getIndex();
		// int to = loop.getSecond().getIndex();
		//
		// length = to - from;
		//
		// for (int i = from; i <= to; i++)
		// mProtein.getMonomer(i).decreaseMobility(
		// mProtein.getCoolingValue(length));
		// }

		// my project is to improve updateF from O(N^2) to O(N)

		/*
		 * Calc Energy Fills these structures (of size N):
		 * 
		 * loopStart[[k11,k12,k13],[k21,k22,k23],...]
		 * loopEnd[[[d11,d22,d23],[d21,d22,d23],...]
		 * 
		 * We iterate the protein, monomer by monomer, and reducing f(i) by X,
		 * When X = Sum(kij) - Sum(dij) that we saw until i (included?..)
		 */
	}

	@Override
	protected void restoreStructure(int pI) {
		mProtein.getMonomer(pI).setDirection(mOldDirectionOfI);
		calcPositionsStartingFromThisMonomer(pI);
	}
}
