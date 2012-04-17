package mutation;
import java.util.ArrayList;
import java.util.HashSet;

import javax.vecmath.Vector3f;

import main.*;
import math.MatrixManipulation;
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
	
	/** List of vectors representing the offset position from the first monomer of the mutation.
	 *  it will be used to determine if the mutation can be applied on the protein with no collisions**/
	public ArrayList<Vector3f> positionOffsets;
	/**
	 * Instantiates a new mutationLibrary entry.
	 * 
	 * @param mutation the mutation
	 * @param axis the axis
	 * @param degree the degree
     * @param direction the direction of a monomer
     * @param dimensions  the system dimensionality. 
	 */
	public MutationLibraryEntry(Mutation mutation, char axis, double degree, MonomerDirection direction, Dimensions dimensions, MatrixManipulation matrixman) {		
		this.mutation = mutation;
		this.axis = axis;
		this.degree = degree;
		this.direction = direction;
        if ((dimensions == Dimensions.TWO) &&(! direction.allowedBy2D))
                throw new RuntimeException("\nWeird MutationLibraryEntry "+this+"\n"+
                                                                          "dimension    =  "+dimensions+"\n"+

                                                                          "relativeDirection = "+direction);
        // Added
        // Initializing positionOffsetList.                               
        Vector3f r = new Vector3f();
        Vector3f o = new Vector3f(0,0,1);
        Vector3f s = new Vector3f(0,1,0);
        Vector3f[] vecs = {r,o,s};
        positionOffsets = new ArrayList<Vector3f>();
        positionOffsets.add(vecs[0]);
        MonomerDirection[] conf = mutation.getConfomation();
        for (int i = 0; i < conf.length -1; i++){        	
        	vecs = MonomerDirection.getPosition(vecs, conf[i]);        	
        	positionOffsets.add(matrixman.LorentzTransformation(vecs[0], axis, degree));
        }        
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
	
	/**
	 * @return a shallow copy of positionOffsetes.
	 */
	public HashSet<Vector3f >getPositionOffsetes(){
		return new HashSet<Vector3f>(positionOffsets);
	}
	
	
}
