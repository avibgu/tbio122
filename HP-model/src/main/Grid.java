package main;

import javax.vecmath.Vector3f;

/**
 *  The world in which toy proteins reside.
 *  The class provides an abstraction of integral coordinate system with unlimited range in all direction.
 *  The first monomer of a toy protein always resides at the origin of this system.
 */
public class Grid {

    private Monomer[][][] grid;
    private int maxX, maxY,maxZ;
    private int minX, minY, minZ;
    private Protein protein = null;
    private Vector3f neighbor = new Vector3f();
    private final Dimensions dimensions;


    public Grid(int initialSize, Dimensions dimensions) {
        this.dimensions = dimensions;
        setInitialSize(initialSize);
        grid = new Monomer[maxX-minX+1][maxY-minY+1][maxZ-minZ+1];
    }

    public void setInitialSize(int proteinSize) {
        minX = (int) -Math.sqrt(proteinSize);
        maxX  = -minX;
        minY = minX;
        maxY  = maxX;
        if (dimensions == Dimensions.TWO)
            minZ = maxZ = 0;
        else {
            minZ = minX;
            maxZ = maxX;
        }
    }
    public Monomer getCell(int x, int y, int z)  {
        return grid[x-minX][y-minY][z-minZ];
    }
    public boolean setCell(int x, int y, int z, Monomer monomer) {
        if (grid[x-minX][y-minY][z-minZ] != null) return false;
        grid[x-minX][y-minY][z-minZ] = monomer;
        return true;
    }

    /**
     *  Updatetes the matrix by linking the parameter monomer to the corresponding cell if it is empty.
     *  If the matrix is two small it is created again with larger size.
     * @param monomer
     * @return true upon success and false upon failur, that is in case the cell was already occupied.  
     */
    public boolean update(Monomer monomer){
        protein = monomer.protein;
        int x = monomer.getX();
        int y =monomer.getY();
        int z =monomer.getZ();
        boolean modified = false;
        if (x < minX)          {minX = x; modified = true;}
        else if (x > maxX){maxX = x+1; modified = true;}
        if (y < minY)          {minY = y; modified = true;}
        else if (y > maxY){maxY = y+1; modified = true;}
        if (z < minZ)          {minZ = z; modified = true;}
        else if (z > maxZ){maxZ = z+1; modified = true;}
        if (modified) {
            grid =  new Monomer[maxX-minX+1][maxY-minY+1][maxZ-minZ+1];
            for (Monomer CurrentMonomer:protein) {
                if (CurrentMonomer == monomer) break;
                if (!setCell(CurrentMonomer.getX(),CurrentMonomer.getY(),CurrentMonomer.getZ(),CurrentMonomer))
                    throw new RuntimeException("Overlaping monomers "+
                            getCell(CurrentMonomer.getX(),CurrentMonomer.getY(),CurrentMonomer.getZ())+" and "+CurrentMonomer);
            }
        }
        return(setCell(x,y,z,monomer));//true if the (x,y,z) cell was empty and false otherwis.
    }
    /**
     *  Counts the number of HH contacts between the
     * @param monomer and its neighbors with higher spetial indeces.
     *  The other contacts will be taken care of by the other monomers.
     * @return  the number of contacts
     */
    public int countContacts(Monomer monomer) {
         Monomer monomer1;
        Vector3f pos = monomer.getR();

        if (monomer.type != MonomerType.H) return 0;
        int count = 0;
        int x = monomer.getX();
        int y = monomer.getY();
        int z = monomer.getZ();
        if (!xEdge(x)) {
            monomer1 = getCell(x+1, y, z);
            if ((GARun.debug) &&(monomer1 != null) &&(monomer.protein!=monomer1.protein)) throw new RuntimeException("Two proteins on the grid\n"+monomer.protein+"\n"+monomer1.protein);
            if ((monomer1 != null) && (monomer1!=monomer.getPrev())&&(monomer1!=monomer.getNext())&&(monomer1.type == MonomerType.H)) count++;
        }
         if (!yEdge(y)){
            monomer1 = getCell(x,y+1,z);
             if ((GARun.debug) &&(monomer1 != null) &&(monomer.protein!=monomer1.protein)) throw new RuntimeException("Two proteins on the grid\n"+monomer.protein+"\n"+monomer1.protein);
             if ((monomer1 != null) && (monomer1!=monomer.getPrev())&&(monomer1!=monomer.getNext())&& (monomer1.type == MonomerType.H)) count++;
        }
        if (!zEdge(z)) {
            monomer1 = getCell(x,y,z+1);
            if ((GARun.debug) &&(monomer1 != null) &&(monomer.protein!=monomer1.protein)) throw new RuntimeException("Two proteins on the grid\n"+monomer.protein+"\n"+monomer1.protein);
            if ((monomer1 != null) && (monomer1!=monomer.getPrev())&&(monomer1!=monomer.getNext())&& (monomer1.type == MonomerType.H)) count++;
        }
        return count;
    }

     public boolean xEdge(int x) {
         if (x > maxX) throw new RuntimeException("x>maxX"+x+">"+maxX);
         return (x==maxX);
     }
     public boolean yEdge(int y) {
         if (y > maxY) throw new RuntimeException("y>maxY"+y+">"+maxY);
         return (y==maxY);
     }
       public boolean zEdge(int z) {
           if (z > maxZ) throw new RuntimeException("z>maxZ"+z+">"+maxZ);
          return (z==maxZ);
     }
    public void reset(Protein protein) {
        reset(protein, protein.get(protein.size()-1));
    }
        public void reset(Protein protein, Monomer endMonomer) {
        this.protein = protein;
        for (Monomer monomer:protein) {
            reset(monomer);
            if (monomer == endMonomer) return;
        }
        throw new RuntimeException("Apparently "+endMonomer+" is not a monomer of "+protein);
    }
    public void reset(Monomer monomer) {
          grid[monomer.getX()-minX][monomer.getY()-minY][monomer.getZ()-minZ] = null;
    }

   public boolean testEmpty() {
       boolean found = false;

       for (int iX = 0; iX<grid.length; iX++)
           for(int iY=0; iY<grid[iX].length; iY++)
               for(int iZ=0; iZ<grid[iX][iY].length; iZ++)
                   if (grid[iX][iY][iZ] != null)   {
                       System.out.println("testEmpty found "+iX+" "+iY+" "+iZ+" "+grid[iX][iY][iZ]+" of  "+grid[iX][iY][iZ].protein.name);
                       found = true;
                   }
       return (!found);
   }
}
