package problem;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import main.Main;

public class ProteinProblem extends Problem {

	private int					mN;
	private int					mE;
	private char[]				mSequense;
	private Point[]				mPositions;
	private Map<Point,Integer>	mPointToIndex;
	private int[]				mTheta;
	private double[]			mF;
	private double[]			mG;
	private double				mCk;

	public ProteinProblem(String pSequense) {

		super();

		setN(pSequense.length());
		setE(0);
		setSequense(pSequense.toCharArray());
		initPositions();
		setTheta(new int[mN]);
		setF(new double[mN]);

		initG();

		setCk(Double.parseDouble(Main.prop.getProperty("CK")));
	}

	protected void initPositions() {

		setPositions(new Point[getN()]);
		setPointToIndex(new HashMap<Point, Integer>(getN()));
		
		for (int i = 0; i < getN(); i++){
			
			Point point = new Point(i, 0);
			
			mPositions[i] = point;
			getPointToIndex().put(point, i);
		}
	}

	private void initG() {

		setG(new double[mN]);

		for (int i = 0; i < mN; i++)
			setG(i, 1);

		// improvement: setG(i,(20 / (i + 17)));
	}

	public int getN() {
		return mN;
	}

	public void setN(int pN) {
		mN = pN;
	}

	public int getE() {
		return mE;
	}

	public void setE(int pE) {
		mE = pE;
	}

	public char[] getSequense() {
		return mSequense;
	}

	public void setSequense(char[] pSequense) {
		mSequense = pSequense;
	}
	
	public char getType(int pIndex) {
		return mSequense[pIndex];
	}

	public Point[] getPositions() {
		return mPositions;
	}

	public void setPositions(Point[] pPositions) {
		mPositions = pPositions;
	}

	public Point getPosition(int pIndex) {
		return mPositions[pIndex];
	}

	public void setPosition(int pIndex, int pX, int pY) {
		mPositions[pIndex].setLocation(pX, pY);
	}

	public Map<Point,Integer> getPointToIndex() {
		return mPointToIndex;
	}

	public void setPointToIndex(Map<Point,Integer> pointToIndex) {
		mPointToIndex = pointToIndex;
	}
	
	public int getPointIndex(Point pPoint){
		return mPointToIndex.get(pPoint);
	}

	public int[] getTheta() {
		return mTheta;
	}

	public void setTheta(int[] pTheta) {
		mTheta = pTheta;
	}

	public double[] getF() {
		return mF;
	}

	public void setF(double[] pF) {
		mF = pF;
	}

	public double[] getG() {
		return mG;
	}

	public void setG(double[] pG) {
		mG = pG;
	}

	public double getCk() {
		return mCk;
	}

	public void setCk(double pCk) {
		mCk = pCk;
	}

	public double getF(int pI) {
		return getF()[pI];
	}

	public void setG(int pI, int pValue) {
		getG()[pI] = pValue;
	}

	public int getThetaI(int pI) {
		return getTheta()[pI];
	}

	public void setThetaI(int pI, int pValue) {
		getTheta()[pI] = pValue;
	}

	public void calcPositionsStartingFromI(int pIndex) {

		int xI = (int) getPosition(pIndex).getX();
		int yI = (int) getPosition(pIndex).getY();

		int xImOne = (int) getPosition(pIndex - 1).getX();
		int yImOne = (int) getPosition(pIndex - 1).getY();

		// 1 = 'right', -1 = 'left', 0 = 'up' or 'down'
		int i = xI - xImOne;

		// 1 = 'up', -1 = 'down', 0 = 'right' or 'left'
		int j = yI - yImOne;

		calcPosition(pIndex + 1, xI, yI, i, j);
	}

	private void calcPosition(int pIndex, int xImOne, int yImOne, int previousI,
			int previousJ) {

		int i = 0;
		int j = 0;

		if (getN() == pIndex)
			return;

		// right
		if (1 == previousI) {

			i = (getThetaI(pIndex) == 0) ? previousI : 0;
			j += getThetaI(pIndex);
		}

		// left
		else if (-1 == previousI) {

			i = (getThetaI(pIndex) == 0) ? previousI : 0;
			j -= getThetaI(pIndex);
		}

		// up
		else if (1 == previousJ) {

			i -= getThetaI(pIndex);
			j = (getThetaI(pIndex) == 0) ? previousJ : 0;
		}

		// down
		else if (-1 == previousJ) {

			i += getThetaI(pIndex);
			j = (getThetaI(pIndex) == 0) ? previousJ : 0;
		}

		setPosition(pIndex, xImOne + i, yImOne + j);

		int xI = (int) getPosition(pIndex).getX();
		int yI = (int) getPosition(pIndex).getY();

		calcPosition(pIndex + 1, xI, yI, i, j);
	}

	public boolean isNeighbors(Point pX, Point pY) {
		// TODO Auto-generated method stub
		return false;
	}
}
