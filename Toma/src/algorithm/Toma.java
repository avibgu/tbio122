package algorithm;

import problem.Problem;

public class Toma extends Algorithm {

	public Toma() {
		super();
	}

	public Toma(Problem pProblem) {
		super(pProblem);
	}

	@Override
	protected int selectResidueRandomly() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	protected boolean shouldWeMoveIt(int i) {

		// TODO move it if Rnd < exp[f(i)/ck]

		// the criterion is always satisfied for residues not belonging to loop
		// defined by HH contacts

		return false;
	}

	@Override
	protected void performRandomlyMovement(int i) {
		// TODO choose Theta(i), while taking as invariant all other Theta
		// coordinates (this correponds to a pivot move)
	}

	@Override
	protected boolean isTheStructureValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void evaluateStructureEnergy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void decreaseTemperature() {
		// TODO should be performed due to some cooling strategy
	}

	@Override
	protected void updateF() {
		// TODO do this for all the residues that participates in loops
		// we should avoid double-counting
	}

	@Override
	protected void restoreStructure() {
		// TODO Auto-generated method stub
	}
}
