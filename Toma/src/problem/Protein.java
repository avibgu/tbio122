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
	private Map<Vector2d, Monomer> mPositionToMonomerLookupMap;

	private int mNumOfHMonomers;
	private int mNumOfPMonomers;

	private Vector2d mTempPosition;

	public Protein(String pSequense) {

		super();

		setNumOfMonomers(pSequense.length());
		setEnergy(0);
		initMonomers(pSequense);
		setTemperature(CoolingStrategy.getInitialTemperature(mNumOfHMonomers,
				mNumOfPMonomers));
		setTempPosition(new Vector2d());
	}

	private void initMonomers(String pSequense) {

		mMonomers = new ArrayList<Monomer>(getNumOfMonomers());
		mPositionToMonomerLookupMap = new HashMap<Vector2d, Monomer>();

		char[] sequense = pSequense.toCharArray();

		mNumOfHMonomers = 0;
		mNumOfPMonomers = 0;

		for (int i = 0; i < sequense.length; i++) {

			Vector2d posiotion = new Vector2d(i, 0);
			Monomer monomer = new Monomer(sequense[i], posiotion, i);

			mMonomers.add(monomer);
			mPositionToMonomerLookupMap.put(posiotion, monomer);

			if (monomer.getType() == MonomerType.H)
				mNumOfHMonomers++;

			else
				mNumOfPMonomers++;
		}
	}

	public boolean setMonomerPosition(int pMonomerIndex, double pNewX,
			double pNewY) {

		mTempPosition.set(pNewX, pNewY);

		if (mPositionToMonomerLookupMap.containsKey(mTempPosition))
			return true;

		Monomer monomer = getMonomers().get(pMonomerIndex);

		Vector2d position = monomer.getPosition();

		mPositionToMonomerLookupMap.remove(position);

		position.set(pNewX, pNewY);

		mPositionToMonomerLookupMap.put(position, monomer);

		return false;
	}

	public void saveResults() {

		StringBuilder sb = new StringBuilder();

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

	public void setNumOfMonomers(int pNumOfMonomer) {
		mNumOfMonomers = pNumOfMonomer;
	}

	public int getEnergy() {
		return mEnergy;
	}

	public void setEnergy(int pEnergy) {
		mEnergy = pEnergy;
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

	public Map<Vector2d, Monomer> getPositionToMonomerLookupMap() {
		return mPositionToMonomerLookupMap;
	}

	public void setPositionToMonomerLookupMap(
			Map<Vector2d, Monomer> pPositionToMonomerLookupMap) {
		mPositionToMonomerLookupMap = pPositionToMonomerLookupMap;
	}

	public Monomer getMonomerFromPosition(Vector2d pPosition) {
		return mPositionToMonomerLookupMap.get(pPosition);
	}

	public int getNumOfHMonomers() {
		return mNumOfHMonomers;
	}

	public void setNumOfHMonomers(int pNumOfHMonomers) {
		mNumOfHMonomers = pNumOfHMonomers;
	}

	public int getNumOfPMonomers() {
		return mNumOfPMonomers;
	}

	public void setNumOfPMonomers(int pNumOfPMonomers) {
		mNumOfPMonomers = pNumOfPMonomers;
	}

	public Vector2d getTempPosition() {
		return mTempPosition;
	}

	public void setTempPosition(Vector2d pTempPosition) {
		mTempPosition = pTempPosition;
	}
}
