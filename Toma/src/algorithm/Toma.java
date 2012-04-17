package algorithm;

import problem.ProteinProblem;

public class Toma extends Algorithm {

	private int mOldThetaI; 
	
	public Toma() {
		super();
	}

	public Toma(ProteinProblem pProblem) {
		super(pProblem);
	}

	@Override
	protected int selectResidueRandomly() {
		return (int) (mProblem.getRandom().nextDouble() * (mProblem.getN() - 1));
	}

	@Override
	protected boolean shouldWeMoveIt(int pI) {

		// move it if Rnd < exp[f(i)/ck]

		// the criterion is always satisfied for residues not belonging to loop
		// defined by HH contacts

		double rnd = mProblem.getRandom().nextDouble();
		
		double x = Math.exp(mProblem.getF(pI) / mProblem.getCk());
		
		return rnd < x;
	}

	@Override
	protected void performRandomlyMovement(int pI) {
		// choose Theta(i), while taking as invariant all other Theta
		// coordinates (this correponds to a pivot move)
		
		// is this ok?..
		
		double rnd = mProblem.getRandom().nextDouble();
		
		int newThetaI = 0;
		
		if (rnd < 0.3)
			newThetaI = -1;
		
		else if (rnd > 0.6)
			newThetaI = 1;
		
		mOldThetaI = mProblem.getThetaI(pI);
		
		mProblem.setThetaI(pI, newThetaI);
	}

	@Override
	protected boolean isTheStructureValid() {
		// TODO we can check collisions only between x,y s.t. x from X and y
		// from Y. X = {monomers before i}, Y = {monomers after i}, and maybe
		// also i..

		// improvement: we can calthe Rectangles of those two Sets, and only
		// when they collide we will perform the big check

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
	protected void restoreStructure(int pI) {
		mProblem.setThetaI(pI, mOldThetaI);
	}
}
