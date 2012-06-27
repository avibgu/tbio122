package problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2d;

import algorithm.CoolingStrategy;

public class Protein extends Problem {

	private int mNumOfMonomers;
	private int mEnergy; // E
	private double mTemperature; // Ck

	private List<Monomer> mMonomers;
	private Map<Vector2d, Monomer> mVector2dToMonomer;

	private int mNumOfH;
	private int mNumOfP;

	private Vector2d mTempPosition;

	public Protein(String pSequense) {

		super();

		setNumOfMonomers(pSequense.length());
		setEnergy(0);
		initMonomers(pSequense);
		setTemperature(CoolingStrategy.getInitialTemperature(mNumOfH, mNumOfP));
		mResults = "";
		mTempPosition = new Vector2d();
	}

	private void initMonomers(String pSequense) {

		mMonomers = new ArrayList<Monomer>(getNumOfMonomers());
		mVector2dToMonomer = new HashMap<Vector2d, Monomer>();

		char[] sequense = pSequense.toCharArray();

		mNumOfH = 0;
		mNumOfP = 0;

		for (int i = 0; i < sequense.length; i++) {

			Vector2d vector = new Vector2d(i, 0);
			Monomer monomer = new Monomer(sequense[i], vector, i);

			mMonomers.add(monomer);
			mVector2dToMonomer.put(vector, monomer);

			if (monomer.getType() == MonomerType.H)
				mNumOfH++;

			else
				mNumOfP++;
		}
	}

	public boolean setMonomerPosition(int pMonomerIndex, double pNewX,
			double pNewY) {

		mTempPosition.set(pNewX, pNewY);

		if (mVector2dToMonomer.containsKey(mTempPosition))
			return true;

		Monomer monomer = getMonomers().get(pMonomerIndex);

		Vector2d position = monomer.getPosition();

		mVector2dToMonomer.remove(position);

		position.set(pNewX, pNewY);

		mVector2dToMonomer.put(position, monomer);

		return false;
	}

	public void saveResults() {

		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append("Energy: " + getEnergy() + "\n");
		sb.append("X,Y,Type\n");

		for (Monomer monomer : mMonomers)
			sb.append(monomer.getResults() + "\n");

		setResults(sb.toString());
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("Energy: " + getEnergy() + "\n");

		for (Monomer monomer : mMonomers)
			sb.append(monomer + " ");

		sb.append("\n");

		return sb.toString();
	}

	public int getNumOfMonomers() {
		return mNumOfMonomers;
	}

	public void setNumOfMonomers(int pN) {
		mNumOfMonomers = pN;
	}

	public int getEnergy() {
		return mEnergy;
	}

	public void setEnergy(int pE) {
		mEnergy = pE;
	}

	public double getTemperature() {
		return mTemperature;
	}

	public void setTemperature(double pTemperature) {
		mTemperature = pTemperature;
	}

	public List<Monomer> getMonomers() {
		return mMonomers;
	}

	public void setMonomers(List<Monomer> pMonomers) {
		mMonomers = pMonomers;
	}

	public Monomer getMonomer(int pIndex) {
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

	public String getResults() {
		return mResults;
	}

	public void setResults(String pResults) {
		mResults = pResults;
	}

	public int getNumOfH() {
		return mNumOfH;
	}

	public int getNumOfP() {
		return mNumOfP;
	}
}
