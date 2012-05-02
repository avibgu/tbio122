package problem;

import java.awt.Point;

public class Monomer {

	private MonomerType mType;
	private Point		mPosition;
	private Direction	mDirection;	// Theta
	private double		mF;			// F
	private double		mG;			// G
	
	public Monomer(char pType, Point pPosition, int pIndex) {

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
}
