package main;

/**
 *  The number of dimensions of the simulation.
 */
public enum Dimensions {
    TWO(2), THREE(3);
     public final int number;
    
    private Dimensions(int number ){
        this.number = number;
    }
}
