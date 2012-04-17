package main;

/**
 * Minimum and maximum coordinates of a protein
 */
public class MinMax {
    public final int maxX, minX , maxY, minY, maxZ, minZ;
       public MinMax(Protein protein) {
           int mxX, mnX , mxY, mnY, mxZ, mnZ, temp;
            mnX = mnY = mnZ = 1000;
            mxX = mxY = mxZ = -1000;
           for (Monomer monomer:protein){
               temp =  monomer.getX();
              if ( temp < mnX) mnX =  temp;
               if ( temp > mxX) mxX =  temp;
             temp =  monomer.getY();
               if ( temp < mnY) mnY =  temp;
              if ( temp > mxY) mxY =  temp;
               temp =  monomer.getZ();
               if ( temp < mnZ) mnZ =  temp;
               if ( temp > mxZ) mxZ =  temp;
           }
           maxX = mxX;
           minX = mnX;
           maxY = mxY;
           minY = mnY;
           maxZ = mxZ;
           minZ= mnZ;
       }
    public int lengthX() {return maxX-minX+1;}
     public int lengthY() {return maxY-minY+1;}
     public int lengthZ() {return maxZ-minZ+1;}
    public String toString() {
        String out = ""+minX+" "+maxX+"\n"+minY+" "+maxY+"\n"+minZ+" "+maxZ+"\n"+lengthX()+" "+lengthY();
        return out;
    }
}
