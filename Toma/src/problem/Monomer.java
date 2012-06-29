package problem;

import javax.vecmath.Vector2d;

public class Monomer {

	private MonomerType mType;
	private Vector2d mPosition;
	private Direction mDirection; // Theta
	private double mMobility; // F
	private int mIndex;

	public Monomer(char pType, Vector2d pPosition, int pIndex) {

		if ('h' == pType || 'H' == pType)
			mType = MonomerType.H;

		else
			mType = MonomerType.P;

		mPosition = pPosition;
		mDirection = Direction.AHEAD;
		mMobility = 0;
		mIndex = pIndex;
	}

	public void decreaseMobility(double pValue) {
		mMobility -= pValue;
	}

	public String getResults() {
		return (int) mPosition.getX() + "," + (int) mPosition.getY() + ","
				+ getType();
	}

	@Override
	public String toString() {
		return "(" + (int) mPosition.getX() + "," + (int) mPosition.getY()
				+ ")";
	}

	public MonomerType getType() {
		return mType;
	}

	public void setType(MonomerType pType) {
		mType = pType;
	}

	public Vector2d getPosition() {
		return mPosition;
	}

	public void setPosition(Vector2d pPosition) {
		mPosition = pPosition;
	}

	public Direction getDirection() {
		return mDirection;
	}

	public void setDirection(Direction pDirection) {
		mDirection = pDirection;
	}

	public double getMobility() {
		return mMobility;
	}

	public void setMobility(double pMobility) {
		mMobility = pMobility;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int pIndex) {
		mIndex = pIndex;
	}
}
