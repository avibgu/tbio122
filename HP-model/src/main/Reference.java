package main;

public class Reference implements Comparable<Object> {

	private double _fitness;
	private final int _index;

	public Reference(int index, double fitness) {
		this._fitness = fitness;
		this._index = index;
	}

	public int compareTo(Object arg0) {

		if (!(arg0 instanceof Reference))
			throw new NotAreferenceException();

		Reference r = (Reference) arg0;
		double delta = _fitness - r._fitness;

		if (delta < 0)
			return -1;

		if (delta > 0)
			return 1;

		return 0;

	}

	public int getIndex() {
		return this._index;
	}

	public double getFitness() {
		return this._fitness;
	}

	public void setFitness(double fitness) {
		this._fitness = fitness;
	}
}
