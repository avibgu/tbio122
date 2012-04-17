package main;

import java.util.Random;

import javax.vecmath.Vector3f;

/**
 *
 *  The discrete directions, in which a monomer may turn,
 *  compared to its predecessor.
 */
public enum MonomerDirection {
        FORWARD ('^',true),
        LEFT        ('<',true),
        RIGHT      ('>',true),
        UP             ('u',false),
        DOWN        ('d',false),
        END_OF_CHAIN('e',true),
        FIRST('f',true),
        UNKNOWN  ('?',true);

    public final char         oneLetter;
    public final boolean  allowedBy2D;

     public static MonomerDirection byChar(char oneLetter) {
          for(MonomerDirection direction:values())
               if (direction.oneLetter == oneLetter) return direction;
          throw new RuntimeException("Weird parameter for byChar "+oneLetter);
     }




  public static MonomerDirection byNumber(int number) {
       for(MonomerDirection direction:values())
           if (direction.ordinal() == number) return direction;
       throw new RuntimeException("Weird parameter for byNumber "+number);
   }



     private MonomerDirection(char oneLetter, boolean allowedBy2D) {
         this.oneLetter = oneLetter;
        this.allowedBy2D       = allowedBy2D;
    }

    public void test(Dimensions dimensions) {
       if ((dimensions ==Dimensions.TWO) & (!allowedBy2D)) throw new RuntimeException("this direction "+this+" is not allowed");
        if (this == MonomerDirection.UNKNOWN)
            throw new RuntimeException("UNKNOWN direction is inappropriate here");
    }
    public static MonomerDirection getRandomDirection(Dimensions dimensions,  Random random) {
        float rnd = random.nextFloat();
        MonomerDirection out;
        if ((dimensions ==Dimensions.TWO)) out = byNumber((int) ( rnd * 3));
        // TODO: BUG? should be THREE?
        else if ((dimensions ==Dimensions.TWO)) out = byNumber((int) ( rnd * 5));
        else throw new RuntimeException( "Weird world dimensions "+dimensions);
        out.test(dimensions);
        return out;
    }

    /**
     * Returns a Random direction which is different then the current direction..
     * @return  a random direction which is different then the current direction..
     */
     public MonomerDirection anotherRandomDirection(Dimensions dimensions,  Random random) {
          MonomerDirection out = this;
         while (out == this)
             out = getRandomDirection(dimensions,  random);
         return out;
     }
     
     /**
      * Gets the position if the previous monomer and a relative direction, and return the position of the current monomer.
      * This function calculates the direction in the same way as {@link Monomer#calculateVectors()}.
      * @param vecs A vector of Vector3f representing the position of the previous monomer.
      * <ul>
      * <li>vecs[0] = r vector, absolute position.
      * <li>vecs[1] = o vector, helper vector for calculation.
      * <li>vecs[2] = s vector, helper vector for calculation.
      * </ul>
      * @param relativeDirection the direction from the previous monomer to the current.
      * @return An array of vectors with the same format as vecs argument.
      */
     public static Vector3f[] getPosition(Vector3f[] vecs , MonomerDirection relativeDirection){
    	 Vector3f r = new Vector3f(); 
    	 Vector3f o = new Vector3f();
    	 Vector3f s = new Vector3f();
    	 switch (relativeDirection) {
	         case FORWARD:
	             o.set(vecs[1]);
	             s.set(vecs[2]);
	             break;
	         case LEFT:
	             o.set(vecs[1]);
	             s.cross(vecs[1], vecs[2]);
	             break;
	         case RIGHT:
	             o.set(vecs[1]);
	             s.cross(vecs[2], vecs[1]);
	             break;         
	         default:             
	        	 throw new RuntimeException("Unsupported direction in MonomerDirection.java.updateVector()");
	     }
    	 r.add(vecs[0], s);
    	 Vector3f[] arr = {r,o,s};
    	 return arr;
    	 
    	 
    	 
    	 /*Vector3f result = new Vector3f(vec);
    	 switch(direction){
    	 case FORWARD: result.y++; break;
    	 case LEFT: result.x--; break;
    	 case RIGHT: result.x++; break;
    	 }
    	 return result;*/
     }
}



