package main;

import java.util.ArrayList;

public class Conformation extends ArrayList<MonomerDirection> {

	private static final long serialVersionUID = 6741557062826032591L;

	private float fitness;
	private boolean ended;

	private float energy;

	public Conformation(int size) {
		super(size);
		fitness = -1;
		energy = 9999;
		ended = false;
	}

	public void clear() {
		super.clear();
		fitness = -1;
		energy = 9999;
	}

	public boolean add(MonomerDirection direction) {
		if (ended)
			throw new RuntimeException(
					"Cannot add a direction after END_OF_CHAIN " + this);
		if (direction.equals(MonomerDirection.UNKNOWN))
			throw new RuntimeException("Weird direction to add: " + direction);
		if (direction.equals(MonomerDirection.END_OF_CHAIN))
			ended = true;
		if ((size() == 0) & (direction != MonomerDirection.FIRST))
			throw new RuntimeException(
					"The first direction must be of FIRST type (f)  and not "
							+ direction);
		return super.add(direction);
	}

	public String toString() {
		String out = "Conformation " + fitness + " " + energy + " ";
		for (MonomerDirection md : this) {
			out += md.oneLetter;
		}
		return out;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	public float getEnergy() {
		return energy;
	}

	public void setEnergy(float energy) {
		this.energy = energy;
	}

}
