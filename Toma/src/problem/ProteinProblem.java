package problem;

import main.Main;

public class ProteinProblem extends Problem {

	private int			mN;
	private int			mE;
	private char[]		mSequense;
	private int[]		mTheta;
	private double[]	mF;
	private double[]	mG;
	private double		mCk;
	
	public ProteinProblem(String pSequense) {
	
		super();
		
		setN(pSequense.length());
		setE(0);
		setSequense(pSequense.toCharArray());
		setTheta(new int[mN]);
		setF(new double[mN]);
		
		initG();
		
		setCk(Double.parseDouble(Main.prop.getProperty("CK")));
	}

	private void initG() {

		setG(new double[mN]);
		
		for (int i = 0; i < mN; i++)
			setG(i,1);

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
}
