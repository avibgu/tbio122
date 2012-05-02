package problem;

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
}
