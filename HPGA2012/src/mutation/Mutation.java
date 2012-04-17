package mutation;

import java.util.Iterator;
import java.util.Vector;
import javax.vecmath.Vector3f;

import main.Pair;
import main.MonomerDirection;

/**
 * A possible conformational change (mutation in the GA jargon) in a toy protein conformation.
 */
public class Mutation {
	
	/** The first monomer vector. */
	private Vector3f firstMonomerVector;
	
	/** The last monomer vector. */
	private Vector3f lastMonomerVector;
	
	/** The confomation. */
	private MonomerDirection[] confomation;
	
	/** The length. */
	private int length;
	
	/** The probability. */
	private float probability;
	
	/** The adjacency list. */
	private Vector<Pair<Integer,Integer>> adjacencyList;
	
	/**
	 * Instantiates a new mutation.
	 * 
	 * @param firstMonomerVector the first monomer vector
	 * @param lastMonomerVector the last monomer vector
	 * @param conformationString the conformation string
	 * @param probability the probability
	 */
	public Mutation(Vector3f firstMonomerVector, Vector3f lastMonomerVector, String conformationString, float probability)  {
		this.firstMonomerVector = firstMonomerVector;
		this.lastMonomerVector = lastMonomerVector;
		
		this.length = conformationString.length();
		this.confomation = new MonomerDirection[length];
		for (int i=0;i<this.confomation.length;i++) {
			this.confomation[i] = MonomerDirection.byNumber((int) conformationString.charAt(i)-48);
		}
		
		this.probability = probability;
		this.adjacencyList = new Vector<Pair<Integer,Integer>>();
	}
	

	public String toString(){
		String ans = "";
		ans ="{";
		for (int i=0;i<length;i++){
			ans += confomation[i].oneLetter;
		}
		ans += "}\t<" + firstMonomerVector + ">\t<" + lastMonomerVector + ">\t<";
		Iterator<Pair<Integer,Integer>> iter = adjacencyList.listIterator();
		Pair<Integer,Integer> pair;
		while (iter.hasNext()) {
			pair = iter.next();
			ans += "<" + pair.getFirst()+ "," + pair.getSecond() + ">:";
		}
		if (adjacencyList.size() != 0) {
			ans = ans.substring(0, ans.length()-1);
		}
		ans+=">";
		return ans;
	}
	
	/**
	 * Gets the confomation.
	 * 
	 * @return the confomation
	 */
	public MonomerDirection[] getConfomation() {
		return confomation;
	}

	/**
	 * Gets the first monomer vector.
	 * 
	 * @return the first monomer vector
	 */
	public Vector3f getFirstMonomerVector() {
		return firstMonomerVector;
	}

	/**
	 * Sets the first monomer vector.
	 * 
	 * @param firstMonomerVector the new first monomer vector
	 */
	public void setFirstMonomerVector(Vector3f firstMonomerVector) {
		this.firstMonomerVector = firstMonomerVector;
	}

	/**
	 * Gets the last monomer vector.
	 * 
	 * @return the last monomer vector
	 */
	public Vector3f getLastMonomerVector() {
		return lastMonomerVector;
	}

	/**
	 * Sets the last monomer vector.
	 * 
	 * @param lastMonomerVector the new last monomer vector
	 */
	public void setLastMonomerVector(Vector3f lastMonomerVector) {
		this.lastMonomerVector = lastMonomerVector;
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Sets the length.
	 * 
	 * @param length the new length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Gets the probability.
	 * 
	 * @return the probability
	 */
	public float getProbability() {
		return probability;
	}

	/**
	 * Sets the probability.
	 * 
	 * @param probability the new probability
	 */
	public void setProbability(float probability) {
		this.probability = probability;
	}

	/**
	 * Gets the adjencies list.
	 * 
	 * @return the adjencies list
	 */
	public Vector<Pair<Integer, Integer>> getAdjacencyList() {
		return adjacencyList;
	}	
}
