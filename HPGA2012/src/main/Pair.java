package main;
/**
 * The Class Pair. this class is a generic type that allows Pairing of data types without the need
 * to define new classes to contain them.
 */
public class Pair<T,K>
{
	
	/** The first element. */
	private T first;
	
	/** The second element. */
	private K second;

	/**
	 * Instantiates a new pair.
	 * 
	 * @param first the first
	 * @param second the second
	 */
	public Pair(T first, K second)
	{
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets the first.
	 * 
	 * @return the first
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Sets the first.
	 * 
	 * @param first the new first element
	 */
	public void setFirst(T first) {
		this.first = first;
	}

	/**
	 * Gets the second.
	 * 
	 * @return the second
	 */
	public K getSecond() {
		return second;
	}

	/**
	 * Sets the second.
	 * 
	 * @param second the new second element
	 */
	public void setSecond(K second) {
		this.second = second;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "<" + first.toString() + "," + second.toString() + ">";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Pair<T,K> other)
	{
		if (this.first.equals(other.first) && this.second.equals(other.second))
			return true;
		
		return false;
	}
}
