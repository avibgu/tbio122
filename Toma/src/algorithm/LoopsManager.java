package algorithm;

import java.util.ArrayList;

import problem.Protein;

public class LoopsManager {

	private Protein mProtein;
	
	private ArrayList<MonomerLoops> mLoopsStarts;
	private ArrayList<MonomerLoops> mLoopsEnds;
	
	public LoopsManager(Protein pProtein, int pNumOfMonomers) {
		
		mProtein = pProtein;
		
		mLoopsStarts = new ArrayList<MonomerLoops>(pNumOfMonomers);
		mLoopsEnds = new ArrayList<MonomerLoops>(pNumOfMonomers);
		
		for (int i = 0; i < pNumOfMonomers; i++){
			mLoopsStarts.add(new MonomerLoops(mProtein));
			mLoopsEnds.add(new MonomerLoops(mProtein));
		}
	}
	
	public void markLoop(int fromIndex, int toIndex) {

		int loopLength = toIndex - fromIndex; 
		
		// "a loop of length (toIndex - fromIndex) starts from me.."
		mLoopsStarts.get(fromIndex).add(loopLength);
		
		// "a loop of length (toIndex - fromIndex) ends at me.."
		mLoopsEnds.get(toIndex).add(loopLength);
	}

	public void clear() {
		
		for (MonomerLoops monomerLoops : mLoopsStarts)
			monomerLoops.clear();
		
		for (MonomerLoops monomerLoops : mLoopsEnds)
			monomerLoops.clear();
	}

	public ArrayList<MonomerLoops> getLoopsStarts() {
		return mLoopsStarts;
	}

	public void setLoopsStarts(ArrayList<MonomerLoops> pLoopsStarts) {
		mLoopsStarts = pLoopsStarts;
	}

	public ArrayList<MonomerLoops> getLoopsEnds() {
		return mLoopsEnds;
	}

	public void setLoopsEnds(ArrayList<MonomerLoops> pLoopsEnds) {
		mLoopsEnds = pLoopsEnds;
	}

}
