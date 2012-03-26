package main;

import java.util.Random;

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
}



