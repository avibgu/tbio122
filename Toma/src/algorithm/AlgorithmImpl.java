package algorithm;

import problem.ProteinProblem;

public class AlgorithmImpl extends Algorithm {

	private int mOldThetaI;

	public AlgorithmImpl() {
		super();
	}

	public AlgorithmImpl(ProteinProblem pProblem) {
		super(pProblem);
	}

	@Override
	protected int selectResidueRandomly() {

		// TODO: instead of randomly, should get a set of possible moves, like
		// 'MutationManager.mutate' does (line 444), and choose one of them

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
		
		// TODO: after changing 'selectResidueRandomly',
		// we should return always true.
	}

	@Override
	protected void performRandomlyMovement(int pI) {
		// choose Theta(i), while taking as invariant all other Theta
		// coordinates (this corresponds to a pivot move)

		// is this ok?.. or should we choosee only 1 or -1?..

		double rnd = mProblem.getRandom().nextDouble();

		int newThetaI = 0;

		if (rnd <= 0.3)
			newThetaI = -1;

		else if (rnd >= 0.6)
			newThetaI = 1;

		mOldThetaI = mProblem.getThetaI(pI);

		mProblem.setThetaI(pI, newThetaI);
		
		// TODO: should perform the movement that 'selectResidueRandomly' chose.
	}

	@Override
	protected boolean isTheStructureValid() {
		// TODO we can check collisions only between x,y s.t. x from X and y
		// from Y. X = {monomers before i}, Y = {monomers after i}, and maybe
		// also i..

		// improvement: we can calthe Rectangles of those two Sets, and only
		// when they collide we will perform the big check

		return false;
		
		// TODO: after changing 'selectResidueRandomly',
		// we should return always true.
	}

	@Override
	protected void evaluateStructureEnergy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void decreaseTemperature() {
		// should be performed due to some cooling strategy
		mProblem.setCk(mProblem.getCk() * 0.999999);

		// improvement: mProblem.setCk((0.3 * nH) / (0.5 * (nH + nP)));
	}

	@Override
	protected void updateF() {
		// TODO do this for all the residues that participates in loops
		// we should avoid double-counting
	}

	@Override
	protected void restoreStructure(int pI) {
		mProblem.setThetaI(pI, mOldThetaI);
		
		// TODO: after changing 'selectResidueRandomly', this method is useless.
	}
}
