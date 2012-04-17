package main;

import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * An HP - model of toy protein.
 */
public class Protein extends ArrayList<Monomer> implements Comparable<Protein> {

	private static final long serialVersionUID = -2614207202416458330L;

	private float fitness;
	private float energy;
	private static final int MAX_TRIES = 10;
	public final Sequence sequence;
	private Grid grid;
	private int size;
	public final Random random;
	public final Dimensions dimensions;
	public final String name;
	/**
	 * A temporary object will serve several methods. It is intended to save the
	 * object creation time for frequently used methods. it is expected that
	 * every method clears this object before it is used.
	 */
	private static Conformation tempConformation = null;

	public final Conformation conformation;

	public Protein(Dimensions dimensions, Sequence sequence, Random random,
			Grid grid, String name) {
		super();
		this.sequence = sequence;
		this.grid = grid;
		this.random = random;
		this.dimensions = dimensions;
		this.name = name;
		size = sequence.size();
		int i = 0;
		for (MonomerType type : sequence) {
			add(new Monomer(type, i, this));
			i++;
		}
		Monomer prev = null;
		for (Monomer monomer : this) {
			if (prev != null)
				prev.setNext(monomer);
			monomer.setPrev(prev);
			prev = monomer;
		}
		grid.reset(this);
		conformation = getRandomConformation(dimensions, random, MAX_TRIES
				* MAX_TRIES); // An arbitrary numbers of attempts before we give
		// up.
		updateFitness();
		conformation.setFitness(fitness);
		conformation.setEnergy(energy);
		if (tempConformation == null)
			tempConformation = new Conformation(size);
		// System.out.println(this);
	}

	/**
	 * Grows a random protein conformation one monomer. after the other The
	 * conformation needs to be self-avoiding and thus a random building process
	 * may end-up in a dead end. In that case the building process is repeated.
	 * 
	 * @param maxTries
	 *            maximal numbers of tries before we give up.
	 */
	private Conformation getRandomConformation(Dimensions dimensions,
			Random random, int maxTries) {
		Conformation out = new Conformation(size);
		boolean success = false;
		int nTries = 0;
		while ((!success) && (nTries < maxTries)) { // Each round of this loop
			// is an attempt to build
			// the model
			success = getRandomConformation(dimensions, random, out);
			nTries++;
		}
		if (!success)
			throw new RuntimeException("Failed to create a random conformation");
		return out;
	}

	/**
	 * An attempts to grow a random protein conformation one monomer. after the
	 * other.
	 * 
	 * @return true if succeeded, false if reached a dead-end.
	 */
	private boolean getRandomConformation(Dimensions dimensions, Random random,
			Conformation conformation) {
		reset();
		conformation.clear();
		boolean first = true;
		for (Monomer monomer : this) {
			if (first) {
				conformation.add(MonomerDirection.FIRST);
				if (!monomer.setRelativeDirection(MonomerDirection.FIRST))
					throw new RuntimeException(
							"Why not accept first direction?");
				first = false;
			} else {
				int nTries = 0;
				boolean success = false;
				MonomerDirection newDirection;
				while ((!success) && (nTries < MAX_TRIES)) {
					newDirection = MonomerDirection.getRandomDirection(
							dimensions, random);
					success = monomer.setRelativeDirection(newDirection);
					nTries++;
				}
				if (nTries >= MAX_TRIES)
					return false; // Apparently we are in a dead end
				conformation.add(monomer.getRelativeDirection());
			}
		}
		return true;
	}

	/**
	 * Assign a conformation to the protein. The directions of the monomers are
	 * set iteratively and each monomer checkes that the direction assined to it
	 * is valid. The correctness of this method depends on the assumption that
	 * the grid is empty (all its cells are null) at the beginning of the
	 * execution.
	 * 
	 * @param conformation
	 * @return true if scuceeded.
	 */
	public int setConformation(Conformation conformation) {
		if (GARun.debug)
			if (!grid.testEmpty())
				throw new RuntimeException("Grid must be empty at this stage");
		Boolean success;
		reset();
		this.conformation.clear();
		int i = 0;

		for (Monomer monomer : this) {
			this.conformation.add(conformation.get(i));
			success = monomer.setRelativeDirection(conformation.get(i));
			if (!success) {
				fitness = -1;
				grid.reset(this, monomer);
				return i;
			}
			i++;
		}
		updateFitness();
		this.conformation.setFitness(fitness);
		this.conformation.setEnergy(energy);
		grid.reset(this);
		return i;
	}

	public void reset() {
		for (Monomer monomer : this) {
			monomer.reset();
			fitness = -1;
			energy = 99999;
		}
		if (conformation != null)
			conformation.clear();
	}

	public void cleanGrid() {
		for (Monomer monomer : this) {
			grid.reset(monomer);
		}
	}

	public float evaluateEnergy() {
		energy = 0;
		for (Monomer monomer : this) {
			energy -= grid.countContacts(monomer);
		}
		return energy;
	}

	public float updateFitness() {
		fitness = -evaluateEnergy();
		return fitness;
	}

	public int compareTo(Protein protein) {
		float delta = fitness - protein.getFitness();
		if (delta < 0)
			return -1;
		if (delta == 0)
			return 0;
		if (delta > 0)
			return 1;
		return 0;
	}

	/**
	 * Crossover event between two proteins.(in1 & in2) into two other proteins
	 * (out1 & out2). If the crossover fails the out proteins are reset.
	 * 
	 * @param in1
	 * @param in2
	 * @param out1
	 * @param out2
	 */
	public static void crossover(Protein in1, Protein in2, Protein out1,
			Protein out2, Random random) {
		tempConformation.clear();
		boolean success; // in assigniong direction to a monomer
		int size = in1.size(); // It is the size of all proteins anyway.
		float rand = random.nextFloat();

		int tempPoint;
		int point = (int) (random.nextFloat() * size);

		for (int i = 0; i < point; i++) {
			Monomer monomer = in1.get(i);
			tempConformation.add(i, monomer.getRelativeDirection());
		}
		for (int i = point; i < size; i++)
			tempConformation.add(i, in2.get(i).getRelativeDirection());
		tempPoint = out1.setConformation(tempConformation);
		if (tempPoint < point)
			throw new RuntimeException(
					"One could expect that copying directions from a protein would not create any problem.");
		if (tempPoint < size) // Cross over failed for out1. Its conformation is
			// meaningless and it can be recycled.
			out2 = out1;
		tempConformation.clear();
		for (int i = 0; i < point; i++)
			tempConformation.add(i, in2.get(i).getRelativeDirection());
		for (int i = point; i < size; i++)
			tempConformation.add(i, in1.get(i).getRelativeDirection());
		tempPoint = out2.setConformation(tempConformation);
		if (tempPoint < point)
			throw new RuntimeException(
					"One could expect that copying directions from a protein would not create any problem.");
		if (tempPoint < size) // Cross over failed for out1. Its conformation is
			// meaningless and it can be recycled.
			out2.reset();
	}

	public String toString() {
		String out = name + "\nfitness:" + fitness + "\n";
		out += "energy: " + energy + "\n";
		out += "sequnce: " + sequence + "\n";
		out += conformation + "\n";
		int x, y, prevX = '.', prevY = '.';
		Iterator<Monomer> iterator = iterator();
		while (iterator.hasNext())
			out += iterator.next().toString();
		out += "\n\n";
		if (dimensions == Dimensions.TWO) {
			MinMax minMax = new MinMax(this);
			char[][] charMatrix = new char[2 * (minMax.lengthX() + 1)][2 * (minMax
					.lengthY() + 1)];
			Monomer prev = null;
			for (char[] row : charMatrix)
				for (int i = 0; i < row.length; i++)
					row[i] = ' ';
			boolean first = true;
			for (Monomer monomer : this) {
				x = 2 * (monomer.getX() - minMax.minX);
				y = 2 * (monomer.getY() - minMax.minY);
				if (monomer.type == MonomerType.H) {
					if (first)
						charMatrix[x][y] = 'H';
					else
						charMatrix[x][y] = 'h';
				} else {
					if (first)
						charMatrix[x][y] = 'P';
					else
						charMatrix[x][y] = 'p';
				}
				first = false;
				if (prev != null) {
					if (prevX != x)
						charMatrix[(x + prevX) / 2][y] = '|';
					else
						charMatrix[x][(y + prevY) / 2] = '-';
				}
				prevX = x;
				prevY = y;
				prev = monomer;
			}
			for (char[] row : charMatrix) {
				for (int i = 0; i < row.length; i++)
					out += row[i];
				out += "\n";
			}
		}
		return out;
	}

	/**
	 * public void calculateVectors() { for (Monomer monomer:this)
	 * monomer.calculateVectors(); }
	 **/
	// ---------------------------------------------------- getters &setters
	// ----------------------------------------------
	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public float getEnergy() {
		return energy;
	}

	public float getFitness() {
		return fitness;
	}
}
