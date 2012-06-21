package problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilities.HashedVector2d;

public class Protein extends Problem {

	private int mNumOfMonomers;
	private int mEnergy; // E
	private double mTemperature; // Ck

	private List<Monomer> mMonomers;
	private Map<HashedVector2d, Monomer> mVector2dToMonomer;

	private String mResults;

	private int mNumOfH;
	private int mNumOfP;

	public Protein(String pSequense) {

		super();

		setNumOfMonomers(pSequense.length());
		setEnergy(0);
		initMonomers(pSequense);
		// setTemperature(Double.parseDouble(Main.prop.getProperty("CK")));
		setTemperature((0.3 * getNumOfH())
				/ (0.5 * (getNumOfH() + getNumOfP())));
	}

	private void initMonomers(String pSequense) {

		mMonomers = new ArrayList<Monomer>(getNumOfMonomers());
		mVector2dToMonomer = new HashMap<HashedVector2d, Monomer>();

		char[] sequense = pSequense.toCharArray();

		mNumOfH = 0;
		mNumOfP = 0;

		for (int i = 0; i < sequense.length; i++) {

			HashedVector2d vector = new HashedVector2d(i, 0);
			Monomer monomer = new Monomer(sequense[i], vector, i);

			mMonomers.add(monomer);
			mVector2dToMonomer.put(vector, monomer);

			if (monomer.getType() == MonomerType.H)
				mNumOfH++;

			else
				mNumOfP++;
		}
	}

	// G function from the paper
	public double getCoolingValue(int pK) {

		// return(1);

		// improvement:

		return 20 / (pK + 17);
	}

	public void setMonomerPosition(int pMonomerIndex, double pNewX, double pNewY) {

		Monomer monomer = getMonomers().get(pMonomerIndex);

		HashedVector2d position = monomer.getPosition();

		mVector2dToMonomer.remove(position);

		position.set(pNewX, pNewY);

		mVector2dToMonomer.put(position, monomer);
	}

	public void saveResults() {

		StringBuilder sb = new StringBuilder();

		sb.append("Energy: " + getEnergy() + "\n");
		sb.append("X,Y,Type\n");

		for (Monomer monomer : mMonomers)
			sb.append(monomer.getResults() + "\n");

		sb.append("\n");

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

	public Map<HashedVector2d, Monomer> getVector2dToMonomer() {
		return mVector2dToMonomer;
	}

	public void setVector2dToMonomer(
			Map<HashedVector2d, Monomer> pVector2dToMonomer) {
		mVector2dToMonomer = pVector2dToMonomer;
	}

	public Monomer getMonomerFromVector2d(HashedVector2d pVector2d) {
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
