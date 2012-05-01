package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import mutation.MutationManager;

/**
 * A population of protein conformations for a Genetic Algorithm.
 */
@SuppressWarnings("serial")
public class Population extends ArrayList<Protein> {

	ArrayList<Reference> ref;

	/** The random number to used in the population. */
	public final Random random;

	/** The size. */
	public final int size;

	/** The mutation manger. */
	public final MutationManager mutationManger;

	public final Dimensions dimensions;

	public final Sequence sequence;

	private Grid grid;

	/**
	 * Instantiates a new population. filling the population with random (Legal)
	 * Proteins
	 * 
	 * @param rand
	 *            the random number to be used in the population
	 * @param mutationManger
	 *            the mutation manger
	 */
	public Population(Configuration config, Random rand,
			MutationManager mutationManger) {

		random = rand;
		size = config.populationSize;

		dimensions = config.dimensions;
		this.mutationManger = mutationManger;
		sequence = new Sequence(config.sequence);
		grid = new Grid(config.sequence.length(), dimensions);

		this.ref = new ArrayList<Reference>();

		for (int i = 0; i < size; i++) {

			Protein protein = new Protein(dimensions, sequence, random, grid,
					"protein" + i);
			add(protein);
			Reference r = new Reference(i, protein.getFitness());
			this.ref.add(r);
			protein.cleanGrid();
		}

		// Collections.sort(this,Collections.reverseOrder());
		Collections.sort(this.ref, Collections.reverseOrder());

	}

	/**
	 * Gets the last.
	 * 
	 * @return the last
	 */
	public Protein getLast() {

		// return get(size()-1);
		return getByRef(size() - 1);
	}

	/**
	 * Gets the last.
	 * 
	 * @return the last
	 */
	public Protein[] getLastTwo() {

		Protein[] out = new Protein[2];
		// out[0] = get(size()-1);
		// out[1] = get(size()-2);
		out[0] = getByRef(size() - 1);
		out[1] = getByRef(size() - 2);
		return out;
	}

	// our addition
	public Protein getByRef(int index) {

		int i = this.ref.get(index).getIndex();
		return this.get(i);
	}

	/**
	 * Gets the first.
	 * 
	 * @return the first
	 */
	public Protein getFirst() {
		return getByRef(0);
	}

	/**
	 * Mutate.
	 * 
	 */
	public void mutate() {

		Protein in = chooseProtein();
		Protein out = getLast();
		mutationManger.mutate(in, out, 10);
		this.ref.get(size() - 1).setFitness(out.getFitness());

		// Collections.sort(this);
		Collections.sort(this.ref);
	}

	/**
	 * Choose one of the proteins, with higher probability to the lower list
	 * position (better fitness)
	 * 
	 * @return the selected protein
	 */
	Protein chooseProtein() {

		float rnd = random.nextFloat(); // Evenly distibuted between 0 and 1
		rnd = 1 - ((2 * rnd) / (rnd + 1));// Uneenly distibuted between 0 and 1
		return getByRef((int) (rnd * (size() - 1)));
	}

	/**
	 * Gets the best energy.
	 * 
	 * @return the best energy
	 */
	public float getBestEnergy() {

		float best = this.getFirst().getEnergy();

		for (Protein protein : this) {

			if (protein.getEnergy() > best) {

				best = protein.getEnergy();
			}
		}
		return best;
	}

	/**
	 * Gets the best fitness.
	 * 
	 * @return the best fitness
	 */
	public float getBestFitness() {

		float best = this.getFirst().getFitness();

		for (Protein protein : this) {

			if (protein.getFitness() > best) {

				best = protein.getFitness();

			}
		}
		return best;
	}

	/**
	 * Gets the worst energy.
	 * 
	 * @return the worst energy
	 */
	public float getWorstEnergy() {

		float best = this.getFirst().getEnergy();

		for (Protein protein : this) {

			if (protein.getEnergy() < best) {

				best = protein.getEnergy();

			}
		}

		return best;
	}

	/**
	 * Gets the worst fitness.
	 * 
	 * @return the worst fitness
	 */
	public float getWorstFitness() {

		float best = this.getFirst().getFitness();

		for (Protein protein : this) {

			if (protein.getFitness() < best) {

				best = protein.getFitness();
			}
		}
		return best;
	}

	/**
	 * Gets the average fitness.
	 * 
	 * @return the average fitness
	 */
	public float getAverageFitness() {
		float totalFitness = 0;
		for (Protein protein : this) {
			totalFitness += protein.getFitness();
		}
		return totalFitness / size;
	}

	/**
	 * Gets the average energy.
	 * 
	 * @return the average energy
	 */
	public float getAverageEnergy() {
		float totalEnergy = 0;
		for (Protein protein : this) {
			totalEnergy += protein.getEnergy();
		}
		return totalEnergy / size;
	}

	public void updateLastTwo() {
		int s = size();
		int last = this.ref.get(s - 1).getIndex();
		this.ref.get(s - 1).setFitness(get(last).getFitness());
		int seconedToLast = this.ref.get(s - 2).getIndex();
		this.ref.get(s - 2).setFitness(get(seconedToLast).getFitness());
	}

	/**
	 * Gets the lowest energy.
	 * 
	 * @return the lowest energy
	 */
	public Protein getLowestEnergy() {
		Protein lowestProtein = getByRef(0);
		for (Protein protein : this) {
			if (lowestProtein.getEnergy() < protein.getEnergy())
				lowestProtein = protein;
		}
		return lowestProtein;
	}

	public void sort() {
		Collections.sort(this.ref, Collections.reverseOrder());
	}

}
