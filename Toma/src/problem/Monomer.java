package problem;

import javax.vecmath.Vector2d;

public class Monomer {

	private MonomerType mType;
	private Vector2d	mPosition;
	private Direction	mDirection;	// Theta
	private double		mF;			// F
	private double		mG;			// G
	
	public Monomer(char pType, Vector2d pPosition, int pIndex) {

		if ('h' == pType || 'H' == pType) 
			mType = MonomerType.H;
		
		else
			mType = MonomerType.P;
		
		mPosition = pPosition;
		mDirection = Direction.AHEAD;
		mF = 0;
		
		// improvement: mG = 20 / (pIndex + 17);
		mG = pIndex;
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

	public double getF() {
		return mF;
	}

	public void setF(double pF) {
		mF = pF;
	}

	public double getG() {
		return mG;
	}

	public void setG(double pG) {
		mG = pG;
	}
}
