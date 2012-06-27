package algorithm;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import problem.Direction;
import problem.Monomer;
import problem.MonomerType;
import problem.Protein;


public class ConcreteTomaAlgorithm extends TomaAlgorithm {

	private Direction mOldDirectionOfI;
	private Vector2d mTempVector;
	private List<Vector2d> mPotencialsNeighbors;
	private LoopsManager mLoopsManager;
	private int mStopIndex;
	private boolean mRestoring;

	public ConcreteTomaAlgorithm() {
		super();
		initDataStructures();
	}

	public ConcreteTomaAlgorithm(Protein pProtein) {
		super(pProtein);
		initDataStructures();
	}

	private void initDataStructures() {

		mTempVector = new Vector2d();

		mPotencialsNeighbors = new ArrayList<Vector2d>(4);

		mPotencialsNeighbors.add(0, new Vector2d());
		mPotencialsNeighbors.add(1, new Vector2d());
		mPotencialsNeighbors.add(2, new Vector2d());
		mPotencialsNeighbors.add(3, new Vector2d());

		mLoopsManager = new LoopsManager(mProtein.getNumOfMonomers());

		mStopIndex = mProtein.getNumOfMonomers();

		mRestoring = false;
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
		return calcPositionsStartingFromThisMonomer(pI);
	}

	private boolean calcPositionsStartingFromThisMonomer(int pMonomerIndex) {

		if (0 == pMonomerIndex)
			pMonomerIndex = 1;

		Vector2d pointI = mProtein.getMonomers().get(pMonomerIndex)
				.getPosition();

		Vector2d pointIminusOne = mProtein.getMonomers()
				.get(pMonomerIndex - 1).getPosition();

		mTempVector.sub(pointI, pointIminusOne);

		return calcPositionsStartingFromThisMonomerRecursivly(pMonomerIndex,
				pointIminusOne, mTempVector);
	}

	private boolean calcPositionsStartingFromThisMonomerRecursivly(
			int pMonomerIndex, Vector2d pPointIminusOne,
			Vector2d pDirectionsVector) {

		if (mProtein.getNumOfMonomers() - 1 == pMonomerIndex)
			return true;

		if (mRestoring && pMonomerIndex >= mStopIndex){

			mStopIndex = mProtein.getNumOfMonomers();
			return true;
		}

		Vector2d pointI = mProtein.getMonomers().get(pMonomerIndex)
				.getPosition();

		Direction directionOfI = mProtein.getMonomers().get(pMonomerIndex)
				.getDirection();

		if (Direction.LEFT == directionOfI)
			turnDirectionVectorLeft(pDirectionsVector);

		else if (Direction.RIGHT == directionOfI)
			turnDirectionVectorRight(pDirectionsVector);

		boolean overlaps = mProtein.setMonomerPosition(pMonomerIndex + 1, pointI.getX()
				+ mTempVector.getX(), pointI.getY() + mTempVector.getY());

		if (overlaps){
			mStopIndex = pMonomerIndex;
			return false;
		}

		return calcPositionsStartingFromThisMonomerRecursivly(pMonomerIndex + 1,
				pointI, pDirectionsVector);
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

		mLoopsManager.clear();

		int energy = 0;

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
						&& neighborMonomer.getType() == MonomerType.H) {

					// every Monomer can start or end up to 3 loops
					mLoopsManager.markLoop(i, neighborMonomer.getIndex());

					energy--;
				}
			}
		}

		mProtein.setEnergy(energy);
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
		mProtein.setTemperature(mProtein.getTemperature() * 0.999998);
	}

	@Override
	protected void updateF() {
		// do this for all the residues that participates in loops
		// we should avoid double-counting

		double coolingValue = 0;

		for (int i = 0; i < mProtein.getNumOfMonomers(); i++) {

			coolingValue += mLoopsManager.getLoopsStartsCoolingValue(i);

			mProtein.getMonomer(i).decreaseMobility(coolingValue);

			coolingValue -= mLoopsManager.getLoopsEndsCoolingValue(i);
		}
	}

	@Override
	protected void restoreStructure(int pI) {

		mRestoring = true;

		mProtein.getMonomer(pI).setDirection(mOldDirectionOfI);
		calcPositionsStartingFromThisMonomer(pI);

		mRestoring = false;
	}
}
