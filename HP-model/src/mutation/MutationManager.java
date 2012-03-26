package mutation;

import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.vecmath.Vector3f;

import main.Configuration;
import main.*;
import math.MatrixManipulation;


/**
 *  An operator  that impose conformational changes (mutations in the GA jargon) on proteins.
 *  To this end it uses libraries of  predefined, self avoiding local mutations. Each library includes mutations of the same length.
 */
public class MutationManager {
    private Dimensions dimensions;

    /** The set of  mutation libraries*/
    public MutationLibrary[] mutationLibraries;
    private MutationLibrary mutationLibrary;

    /** The mutation list. unsorted. just loaded from the file*/
    private List<Vector<Mutation>> mutationList;

    private MatrixManipulation matrixMan;

    private float pm;
    private Random random;

    public MutationManager(Configuration config) {
        matrixMan = new MatrixManipulation(config.dimensions);
        this.random = config.random;
        dimensions = config.dimensions;
        int numberOfDirections = 3;
        if (config.dimensions == Dimensions.THREE)
            numberOfDirections = 5;

        loadData(config.mutationsFileName,numberOfDirections);
        buildDictionaries();
    }

    


    /**
     * Loading Mutations data from file
     *
     * @param fileName the file name
     * @param numberOfDirections the number of directions
     */
    private void loadData(String fileName, int numberOfDirections) {
        mutationList = readFromFile(fileName);
    }

    public LinkedList<Vector<Mutation>> readFromFile(String filename) {
		try {
			LinkedList<Vector<Mutation>> list = new LinkedList<Vector<Mutation>>();
			Vector<Mutation> currentVector = null;

			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input);

			String line = bufRead.readLine();
			StringTokenizer sT;
			Mutation mutation;
			String chainStr,adjenciesStr,pairStr,firstVectorStr,lastVectorStr;
			Pair<Integer,Integer> pair;
			Integer pairNum1,pairNum2;
			while (line != null)  {
				if (line.charAt(0) != '#' && line.charAt(0) != '!' && line.trim().length() != 0) {//Skip Comments\ headers \ empty lines
					sT = new StringTokenizer(line,"\t");

					chainStr = sT.nextToken();
					chainStr = chainStr.substring(1, chainStr.length()-1);
					firstVectorStr = sT.nextToken();
					firstVectorStr = firstVectorStr.substring(1, firstVectorStr.length()-1);
					lastVectorStr = sT.nextToken();
					lastVectorStr = lastVectorStr.substring(1, lastVectorStr.length()-1);

					adjenciesStr = sT.nextToken();
					adjenciesStr = adjenciesStr.substring(1, adjenciesStr.length()-1);

					mutation = new Mutation(stringToVector(firstVectorStr),stringToVector(lastVectorStr),chainStr,0);

					if (adjenciesStr.length() != 0) {
						sT = new StringTokenizer(adjenciesStr,":");
						while (sT.hasMoreTokens()) {
							pairStr = sT.nextToken();
							pairNum1 = new Integer(pairStr.substring(pairStr.indexOf('<')+1, pairStr.indexOf(',')));
							pairNum2 = new Integer(pairStr.substring(pairStr.indexOf(',')+1).replace(">", ""));
							pair = new Pair<Integer, Integer>(pairNum1,pairNum2);
							mutation.getAdjacencyList().add(pair);
						}
					}
					currentVector.add(mutation);
				}
				else if (line.charAt(0) == '!') //if header
				{
					currentVector = new Vector<Mutation>();
					list.addLast(currentVector);
				}
				line = bufRead.readLine();
			}
			bufRead.close();
			return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

    /**
	 * String to vector.
	 *
	 * @param vectorStr the vector str
	 *
	 * @return the vector3f
	 */
	private Vector3f stringToVector(String vectorStr) {
		vectorStr = vectorStr.substring(1,vectorStr.length()-1);
		StringTokenizer sT = new StringTokenizer(vectorStr,",");
		return new Vector3f(Float.parseFloat(sT.nextToken()),Float.parseFloat(sT.nextToken()),Float.parseFloat(sT.nextToken()));
	}
    /**
     * Building the mutationLibrary based on the data loaded on to mutationList field
     * mutation length is equal to the number of monomers that participate in the mutation
     */
    private void buildDictionaries() {
        mutationLibraries = new MutationLibrary[mutationList.size()+2];
        int currentDictionaryIndex = 2; // minimum mutation length is 2
        for (Vector<Mutation> vector : mutationList) {
            mutationLibraries[currentDictionaryIndex] = new MutationLibrary();
            for (Mutation mutation : vector) {
                mutation.setProbability(pm);
                addLibraryEntry(mutation, mutationLibraries[currentDictionaryIndex]);
            }
            currentDictionaryIndex++;
        }
    }

    /**
     * Adds a mutation to a {@link MutationLibrary}.
     *  Six versions of  of each mutation are added to mutationLibrary entry. the method fill's for the given mutation all the possible vector in the given rotations
     * <p> 0 degrees on axis x
     * <p> 90 degrees on axis x
     * <p> -90 degrees on axis x
     * <p> 180 degrees on axis x
     * <p> 90 degrees on axis y
     * <p> 90 degrees on axis y
     * @param mutation the mutation
     * @param library the mutationLibrary
     */
    private void addLibraryEntry(Mutation mutation, MutationLibrary library)  {
        Vector3f positionVector = new Vector3f();
        double degree;
        char axis;
        MonomerDirection relative = MonomerDirection.FORWARD;
        MutationLibraryEntry newEntry;
        //TODO: check for duplicity in the Dictionary while putting mutation
        positionVector.sub(mutation.getLastMonomerVector(), mutation.getFirstMonomerVector());
//		put in library as it is
        newEntry = new MutationLibraryEntry(mutation,'x',0,relative, dimensions);
        library.put(positionVector, newEntry);

//		rotation of 90 deg around x axis
        degree = Math.PI /2;
        axis = 'x';
        if (dimensions == Dimensions.THREE) {
            relative = MonomerDirection.UP;
            newEntry = new MutationLibraryEntry(mutation,axis,degree,relative,dimensions);
            library.put(matrixMan.LorentzTransformation(positionVector, axis, degree), newEntry);
        }

//		rotation of -90 deg around x axis
        degree = -Math.PI /2;
        axis = 'x';
        if (dimensions == Dimensions.THREE) {
            relative = MonomerDirection.DOWN;
            newEntry =  new MutationLibraryEntry(mutation,axis,degree,relative,dimensions);
            library.put(matrixMan.LorentzTransformation(positionVector, axis, degree), newEntry);
        }


//		rotation of 180 deg around x axis
        degree = Math.PI ;
        axis = 'x';
        //TODO: check definition in here !!!
        relative = MonomerDirection.FORWARD;
        newEntry =  new MutationLibraryEntry(mutation,axis,degree,relative,dimensions);
        if ((dimensions == Dimensions.THREE) || (newEntry.getDirection().ordinal() <3))
            library.put(matrixMan.LorentzTransformation(positionVector,axis,degree), newEntry);

//		rotation of 90 deg around y axis
        degree = Math.PI / 2 ;
        axis = 'z';
        relative = MonomerDirection.LEFT;
        newEntry =   new MutationLibraryEntry(mutation,axis,degree,relative,dimensions);
        if ((dimensions == Dimensions.THREE) || (newEntry.getDirection().ordinal() <3))
            library.put(matrixMan.LorentzTransformation(positionVector,axis,degree), newEntry);

//		rotation of -90 deg around y axis
        degree = -Math.PI / 2 ;
        axis = 'z';
        relative = MonomerDirection.RIGHT;
        newEntry =  new MutationLibraryEntry(mutation,axis,degree,relative,dimensions);
        if ((dimensions == Dimensions.THREE) || (newEntry.getDirection().ordinal() <3))
            library.put(matrixMan.LorentzTransformation(positionVector,axis,degree), newEntry);
    }

    private int selectMutationNumber(LinkedList<MutationLibraryEntry> list) {
        return random.nextInt(list.size());
    }

    private boolean activateMutationOnProtein(Protein protein, MutationLibraryEntry entry,int mutationStartMonomer, Conformation originalConformation) {
        int size = protein.size();
        Mutation selectedMutation =  entry.getMutation();
        MonomerDirection[] mutationArray = selectedMutation.getConfomation();
        Conformation newConformation = new Conformation(size);
        boolean success;
        Monomer monomer;
        int monomerNumber;
        int temp,i;
        int mutationEndMonomer = mutationStartMonomer+mutationArray.length-2;
        if (mutationEndMonomer >= size)  throw new RuntimeException("mutation too long");

        for (monomerNumber = 0; monomerNumber <= mutationStartMonomer; monomerNumber++)
            newConformation.add(originalConformation.get(monomerNumber));
        i = 0;
        for (monomerNumber = mutationStartMonomer+1;monomerNumber <= mutationEndMonomer; monomerNumber++) {
            newConformation.add(mutationArray[i]);
            i++;
        };

        for (monomerNumber = mutationEndMonomer+1;monomerNumber < size; monomerNumber++)      {
            MonomerDirection direction = originalConformation.get(monomerNumber);
            newConformation.add(direction);
        }
        temp =protein.setConformation(newConformation);

        if (temp <mutationStartMonomer)
            throw new RuntimeException("This is weird. This structure was already tested in the past, how come it failes now?");
        if (temp < size) return false;
        //if (protein.getFitness() > originalConformation.getFitness()+1)
        //System.out.println("Mutation accepted  \n"+originalConformation+"\n"+entry+" "+mutationStartMonomer+"\n"+protein);
        return true;
    }


    public void  mutate(Protein protein, Protein out, int max_tries) {


        int nTries = 0;
        boolean success=false;
        int mutationLength;
        int mutationStartMonomer;
        int mutationLastMonomer;
        Vector3f representativeVector = new Vector3f();
        Vector3f start, end;
        LinkedList<MutationLibraryEntry> list;

        int selectedMutationIndex;
        Conformation originalConformation = protein.conformation;
        int monomerNumber;
        MutationLibraryEntry entry;

        while (nTries < max_tries)  { // upon success the method will return
            nTries++;
            out.reset();
            //		Generate a number between [1..protein.size-2]
            mutationLength = random.nextInt(Math.min(protein.size()-1, mutationLibraries.length)-2)+2;
            //the start monomer may be between 1 and protein size- mutation length
            mutationStartMonomer = random.nextInt(protein.size()-mutationLength-1)+1;
            mutationLastMonomer   = mutationLength + mutationStartMonomer-1;
            start = protein.get(mutationStartMonomer).getR();
            end = protein.get(mutationLastMonomer).getR();
            representativeVector.sub(end, start);
            mutationLibrary =  mutationLibraries[mutationLength];
            list = mutationLibrary.get(representativeVector);

            if (list == null)  //list not found for given arguments
                throw new RuntimeException("ERROR: \nstart vector:" + start + " \nend Vector" + end + "\nsub:" + representativeVector+"\n"+
                        "Mutation length:" + mutationLength+"\n"+
                        "Mutation Start Monomer:" + mutationStartMonomer+"\n"+
                        "Mutation Last Monomer:" + mutationLastMonomer+"\n"+
                        "Protein:" + protein.toString() );
            if (list.size()> 0) {
                selectedMutationIndex = selectMutationNumber(list);
                entry =  list.get(selectedMutationIndex);

                //System.out.println("____________________________________\n"+protein+"\n"+mutationLength+" "+representativeVector);

                success = activateMutationOnProtein(out, entry, mutationStartMonomer, originalConformation);
            }
            if (success) return;
        }
        out.reset();
    }
}

