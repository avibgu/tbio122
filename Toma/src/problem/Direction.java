package problem;

public enum Direction {

	LEFT(-1),
	AHEAD(0),
	RIGHT(1);
	
	public int mDirection;
	
	Direction(int pDirection){
		mDirection = pDirection;
	}
}
