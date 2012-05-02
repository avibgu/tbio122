package problem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2d;

import main.Main;

public class Protein extends Problem {

	private int					mN;
	private int					mE;
	private double				mCk;

	private List<Monomer>			mMonomers;
	private Map<Vector2d,Monomer>	mVector2dToMonomer;
	
	public Protein(String pSequense) {

		super();

		setN(pSequense.length());
		setE(0);
		setCk(Double.parseDouble(Main.prop.getProperty("CK")));
		initMonomers(pSequense);
	}
	
	
	private void initMonomers(String pSequense) {
	
		mMonomers = new ArrayList<Monomer>(getN());
		mVector2dToMonomer = new HashMap<Vector2d,Monomer>();
		
		char[] sequense = pSequense.toCharArray();
		
		for (int i = 0; i < sequense.length; i++){
			
			Vector2d vector = new Vector2d(i,0);
			Monomer monomer = new Monomer(sequense[i], vector, i);
			
			mMonomers.add(monomer);
			mVector2dToMonomer.put(vector, monomer);
		}
	}
	

	// G function from the paper
	public double getCoolingValue(int pK) {
		// improvement: return 20 / (pK + 17);
		return(1);
	}
	
	public boolean isNeighbors(Vector2d pX, Vector2d pY) {

		int difference = getMonomerFromVector2d(pX).getIndex() - getMonomerFromVector2d(pY).getIndex();
		
		return (-1 == difference || 1 == difference);
	}
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		
		sb.append("Energy: " + getE() + "\n");
		
		for (Monomer monomer : mMonomers)
			sb.append(monomer + " ");
		
		sb.append("\n");
		
		return sb.toString();
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
	
	public double getCk() {
		return mCk;
	}

	public void setCk(double pCk) {
		mCk = pCk;
	}

	public List<Monomer> getMonomers() {
		return mMonomers;
	}

	public void setMonomers(List<Monomer> pMonomers) {
		mMonomers = pMonomers;
	}
	
	public Monomer getMonomer(int pIndex){
		return mMonomers.get(pIndex);
	}

	public Map<Vector2d, Monomer> getVector2dToMonomer() {
		return mVector2dToMonomer;
	}

	public void setVector2dToMonomer(Map<Vector2d, Monomer> pVector2dToMonomer) {
		mVector2dToMonomer = pVector2dToMonomer;
	}
	
	public Monomer getMonomerFromVector2d(Vector2d pVector2d) {
		return mVector2dToMonomer.get(pVector2d);
	}
	
	
	
	// TODO: do it better, usinf Vector2d
	
	public void calcPositionsStartingFromI(int pIndex) {

		if (0 == pIndex)
			pIndex++;
		
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
}
