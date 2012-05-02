package problem;

public class Pair {

	private Monomer mFirst;
	private Monomer mSecond;

	public Pair(Monomer pFirstMonomer, Monomer pSecondMonomer) {
		mFirst = pFirstMonomer;
		mSecond = pSecondMonomer;
	}

	public Monomer getFirst() {
		return mFirst;
	}

	public Monomer getSecond() {
		return mSecond;
	}
}
