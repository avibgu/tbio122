package mutation;
import main.*;
/**
 * An entry in the {@link MutationLibrary} .
 *  Warps a {@link Mutation}
 * @see MutationLibrary
 */
public class MutationLibraryEntry
{
	
	/** The mutation. */
	private Mutation mutation;
	
	/** The axis. correct values are: 'x','y' or 'z' only (<b>case sensitve</b>)*/
	private char axis;
	
	/** The degree. */
	private double degree;
	
	/** The relative direction of the first monomer. */
	private MonomerDirection direction;
	/**
	 * Instantiates a new mutationLibrary entry.
	 * 
	 * @param mutation the mutation
	 * @param axis the axis
	 * @param degree the degree
     * @param direction the direction of a monomer
     * @param dimensions  the system dimensionality. 
	 */
	public MutationLibraryEntry(Mutation mutation, char axis, double degree, MonomerDirection direction, Dimensions dimensions) {
		this.mutation = mutation;
		this.axis = axis;
		this.degree = degree;
		this.direction = direction;
        if ((dimensions == Dimensions.TWO) &&(! direction.allowedBy2D))
                throw new RuntimeException("\nWeird MutationLibraryEntry "+this+"\n"+
                                                                          "dimension    =  "+dimensions+"\n"+
                                                                           "relativeDirection = "+direction);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return mutation.toString() + "\taxis:" + getAxis() + "\tdegree:" + getDegree() + "\tDirection:" + getDirection();
	}

	/**
	 * Gets the axis.
	 * 
	 * @return the axis
	 */
	public char getAxis() {
		return axis;
	}


	/**
	 * @return the direction
	 */
	public MonomerDirection getDirection() {
		return direction;
	}

	/**
	 * Gets the degree.
	 * 
	 * @return the degree
	 */
	public double getDegree() {
		return degree;
	}


	/**
	 * Gets the mutation.
	 * 
	 * @return the mutation
	 */
	public Mutation getMutation() {
		return mutation;
	}
	
	
}
