package mutation;

import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.vecmath.Vector3f;

import main.*;
import math.MatrixManipulation;

/**
 * An operator that impose conformational changes (mutations in the GA jargon)
 * on proteins. To this end it uses libraries of predefined, self avoiding local
 * mutations. Each library includes mutations of the same length.
 */
public class MutationManager {
	private Dimensions dimensions;

	/** The set of mutation libraries */
	public MutationLibrary[] mutationLibraries;
	private MutationLibrary mutationLibrary;

	/** The mutation list. unsorted. just loaded from the file */
	private List<Vector<Mutation>> mutationList;

	private MatrixManipulation matrixMan;

	private float pm;
	private Random random;

	/**
	 * The following collections are used by mutate(), and are saved as members
	 * to improve performance.
	 */
	private ArrayList<MutationLibraryEntry> candidates = new ArrayList<MutationLibraryEntry>();
	private ArrayList<Vector3f> proteinPositions = new ArrayList<Vector3f>();

	/** times setconfirmation() failed. used for performance measuring */
	public int numOfFailers = 0;

	/** times setconfirmation() called. used for performance measuring */
	public int numOfIterations = 0;

	/**
	 * temp Vectors used by activateMutationOnProtein(). Declared as members to
	 * avoid "new".
	 */
	private Vector3f v1 = new Vector3f();
	private Vector3f v2 = new Vector3f();
	private Vector3f v3 = new Vector3f();

	public MutationManager(Configuration config) {
		matrixMan = new MatrixManipulation(config.dimensions);
		this.random = config.random;
		dimensions = config.dimensions;
		int numberOfDirections = 3;
		if (config.dimensions == Dimensions.THREE)
			numberOfDirections = 5;

		loadData(config.mutationsFileName, numberOfDirections);
		buildDictionaries();
	}

	/**
	 * Loading Mutations data from file
	 * 
	 * @param fileName
	 *            the file name
	 * @param numberOfDirections
	 *            the number of directions
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
			String chainStr, adjenciesStr, pairStr, firstVectorStr, lastVectorStr;
			Pair<Integer, Integer> pair;
			Integer pairNum1, pairNum2;
			while (line != null) {
				if (line.charAt(0) != '#' && line.charAt(0) != '!'
						&& line.trim().length() != 0) {// Skip Comments\ headers
														// \ empty lines
					sT = new StringTokenizer(line, "\t");

					chainStr = sT.nextToken();
					chainStr = chainStr.substring(1, chainStr.length() - 1);
					firstVectorStr = sT.nextToken();
					firstVectorStr = firstVectorStr.substring(1,
							firstVectorStr.length() - 1);
					lastVectorStr = sT.nextToken();
					lastVectorStr = lastVectorStr.substring(1,
							lastVectorStr.length() - 1);

					adjenciesStr = sT.nextToken();
					adjenciesStr = adjenciesStr.substring(1,
							adjenciesStr.length() - 1);

					mutation = new Mutation(stringToVector(firstVectorStr),
							stringToVector(lastVectorStr), chainStr, 0);

					if (adjenciesStr.length() != 0) {
						sT = new StringTokenizer(adjenciesStr, ":");
						while (sT.hasMoreTokens()) {
							pairStr = sT.nextToken();
							pairNum1 = new Integer(pairStr.substring(
									pairStr.indexOf('<') + 1,
									pairStr.indexOf(',')));
							pairNum2 = new Integer(pairStr.substring(
									pairStr.indexOf(',') + 1).replace(">", ""));
							pair = new Pair<Integer, Integer>(pairNum1,
									pairNum2);
							mutation.getAdjacencyList().add(pair);
						}
					}
					currentVector.add(mutation);
				} else if (line.charAt(0) == '!') // if header
				{
					currentVector = new Vector<Mutation>();
					list.addLast(currentVector);
				}
				line = bufRead.readLine();
			}
			bufRead.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * String to vector.
	 * 
	 * @param vectorStr
	 *            the vector str
	 * 
	 * @return the vector3f
	 */
	private Vector3f stringToVector(String vectorStr) {
		vectorStr = vectorStr.substring(1, vectorStr.length() - 1);
		StringTokenizer sT = new StringTokenizer(vectorStr, ",");
		return new Vector3f(Float.parseFloat(sT.nextToken()),
				Float.parseFloat(sT.nextToken()), Float.parseFloat(sT
						.nextToken()));
	}

	/**
	 * Building the mutationLibrary based on the data loaded on to mutationList
	 * field mutation length is equal to the number of monomers that participate
	 * in the mutation
	 */
	private void buildDictionaries() {
		mutationLibraries = new MutationLibrary[mutationList.size() + 2];
		int currentDictionaryIndex = 2; // minimum mutation length is 2
		for (Vector<Mutation> vector : mutationList) {
			mutationLibraries[currentDictionaryIndex] = new MutationLibrary();
			for (Mutation mutation : vector) {
				mutation.setProbability(pm);
				addLibraryEntry(mutation,
						mutationLibraries[currentDictionaryIndex]);
			}
			currentDictionaryIndex++;
		}
	}

	/**
	 * Adds a mutation to a {@link MutationLibrary}. Six versions of of each
	 * mutation are added to mutationLibrary entry. the method fill's for the
	 * given mutation all the possible vector in the given rotations
	 * <p>
	 * 0 degrees on axis x
	 * <p>
	 * 90 degrees on axis x
	 * <p>
	 * -90 degrees on axis x
	 * <p>
	 * 180 degrees on axis x
	 * <p>
	 * 90 degrees on axis y
	 * <p>
	 * -90 degrees on axis y
	 * 
	 * @param mutation
	 *            the mutation
	 * @param library
	 *            the mutationLibrary
	 */
	private void addLibraryEntry(Mutation mutation, MutationLibrary library) {
		Vector3f positionVector = new Vector3f();
		double degree;
		char axis;
		MonomerDirection relative = MonomerDirection.FORWARD;
		MutationLibraryEntry newEntry;
		// TODO: check for duplicity in the Dictionary while putting mutation
		positionVector.sub(mutation.getLastMonomerVector(),
				mutation.getFirstMonomerVector());
		// put in library as it is
		newEntry = new MutationLibraryEntry(mutation, 'x', 0, relative,
				dimensions, matrixMan);
		library.put(positionVector, newEntry);

		// rotation of 90 deg around x axis
		degree = Math.PI / 2;
		axis = 'x';
		if (dimensions == Dimensions.THREE) {
			relative = MonomerDirection.UP;
			newEntry = new MutationLibraryEntry(mutation, axis, degree,
					relative, dimensions, matrixMan);
			library.put(matrixMan.LorentzTransformation(positionVector, axis,
					degree), newEntry);
		}

		// rotation of -90 deg around x axis
		degree = -Math.PI / 2;
		axis = 'x';
		if (dimensions == Dimensions.THREE) {
			relative = MonomerDirection.DOWN;
			newEntry = new MutationLibraryEntry(mutation, axis, degree,
					relative, dimensions, matrixMan);
			library.put(matrixMan.LorentzTransformation(positionVector, axis,
					degree), newEntry);
		}

		// rotation of 180 deg around x axis
		degree = Math.PI;
		axis = 'x';
		// TODO: check definition in here !!!
		relative = MonomerDirection.FORWARD;
		newEntry = new MutationLibraryEntry(mutation, axis, degree, relative,
				dimensions, matrixMan);
		if ((dimensions == Dimensions.THREE)
				|| (newEntry.getDirection().ordinal() < 3))
			library.put(matrixMan.LorentzTransformation(positionVector, axis,
					degree), newEntry);

		// rotation of 90 deg around y axis
		degree = Math.PI / 2;
		axis = 'z';
		relative = MonomerDirection.LEFT;
		newEntry = new MutationLibraryEntry(mutation, axis, degree, relative,
				dimensions, matrixMan);
		if ((dimensions == Dimensions.THREE)
				|| (newEntry.getDirection().ordinal() < 3))
			library.put(matrixMan.LorentzTransformation(positionVector, axis,
					degree), newEntry);

		// rotation of -90 deg around y axis
		degree = -Math.PI / 2;
		axis = 'z';
		relative = MonomerDirection.RIGHT;
		newEntry = new MutationLibraryEntry(mutation, axis, degree, relative,
				dimensions, matrixMan);
		if ((dimensions == Dimensions.THREE)
				|| (newEntry.getDirection().ordinal() < 3))
			library.put(matrixMan.LorentzTransformation(positionVector, axis,
					degree), newEntry);
	}

	private int selectMutationNumber(Collection<MutationLibraryEntry> list) {
		return random.nextInt(list.size());
	}

	private boolean activateMutationOnProtein(Protein protein,
			Protein originalProtein, MutationLibraryEntry entry,
			int mutationStartMonomer, int mutationEndMonomer,
			Conformation originalConformation) {

		numOfIterations++;
		int size = protein.size();
		Mutation selectedMutation = entry.getMutation();
		MonomerDirection[] mutationArray = selectedMutation.getConfomation();
		Conformation newConformation = new Conformation(size);
		int monomerNumber;

		int temp, i;
		if (mutationEndMonomer >= size)
			throw new RuntimeException("mutation too long");

		// conformation before mutation.
		for (monomerNumber = 0; monomerNumber <= mutationStartMonomer; monomerNumber++)
			newConformation.add(originalConformation.get(monomerNumber));

		// First monomer after start of mutation.
		// The conformation(direction) of this momomer is not necessarily as
		// written in mutation conformation,
		// therefore we will calculate its direction according to position of 3
		// vectors.
		Vector3f mutationStartPos = originalProtein.get(mutationStartMonomer)
				.getR();
		v1.set(originalProtein.get(mutationStartMonomer - 1).getR());
		v2.set(mutationStartPos);
		v3.set(mutationStartPos);
		v3.add(entry.positionOffsets.get(1));
		newConformation.add(matrixMan.getDirection(v1, v2, v3));

		// Conformation of mutation
		i = 1;
		for (monomerNumber = mutationStartMonomer + 2; monomerNumber <= mutationEndMonomer; monomerNumber++) {
			newConformation.add(mutationArray[i]);
			i++;
		}
		;

		// First monomer after end of mutation.
		// The conformation(direction) of this momomer is not necessarily as
		// written in mutation conformation,
		// therefore we will calculate its direction according to position of 3
		// vectors.
		v1.set(mutationStartPos);
		v1.add(entry.positionOffsets.get(entry.positionOffsets.size() - 2));
		v2.set(originalProtein.get(mutationEndMonomer).getR());
		v3.set(originalProtein.get(mutationEndMonomer + 1).getR());
		newConformation.add(matrixMan.getDirection(v1, v2, v3));

		// Comformatino after mutation
		for (monomerNumber = mutationEndMonomer + 2; monomerNumber < size; monomerNumber++)
			newConformation.add(originalConformation.get(monomerNumber));

		temp = protein.setConformation(newConformation);

		if (temp < mutationStartMonomer)
			throw new RuntimeException(
					"This is weird. This structure was already tested in the past, how come it failes now?");
		if (temp < size) {
			numOfFailers++;
			return false;
		}

		return true;
	}

	/*
	 * private boolean activateMutationOnProtein(Protein protein, Protein
	 * originalProtein, MutationLibraryEntry entry,int mutationStartMonomer, int
	 * mutationEndMonomer) { numOfIterations++; protein.reset();
	 * protein.conformation.clear();
	 * 
	 * Monomer mon; for (int i = 0; i < mutationStartMonomer; i++){ mon =
	 * protein.get(i); mon.setR(new Vector3f(originalProtein.get(i).getR()));
	 * protein.getGrid().update(mon); }
	 * 
	 * Vector3f mutationStartPos =
	 * originalProtein.get(mutationStartMonomer).getR(); for (int i =
	 * mutationStartMonomer, j = 0; i <= mutationEndMonomer; i++, j++){ mon =
	 * protein.get(i); Vector3f vec = new
	 * Vector3f(entry.positionOffsets.get(j)); vec.add(mutationStartPos);
	 * mon.setR(vec); protein.getGrid().update(mon); }
	 * 
	 * for (int i = mutationEndMonomer + 1; i < protein.size(); i++){ mon =
	 * protein.get(i); mon.setR(new Vector3f(originalProtein.get(i).getR()));
	 * protein.getGrid().update(mon); } protein.updateFitness();
	 * protein.conformation.setFitness(protein.getFitness());
	 * protein.conformation.setEnergy(protein.getEnergy());
	 * protein.getGrid().reset(protein);
	 * 
	 * return true; }
	 */

	/**
	 * @param protein
	 *            - mutation will be created from this protein
	 * @param out
	 *            - protein with low fitness. if mutation process is successful
	 *            out will become the result of of the mutation, else out will
	 *            be reset.
	 * @param max_tries
	 *            - max tries for creating a mutation.
	 */
	public void mutate(Protein protein, Protein out, int max_tries) {
		
		int nTries = 0;
		boolean success = false;
		
		int mutationLength;
		int mutationStartMonomer;
		int mutationLastMonomer;
		
		Vector3f representativeVector = new Vector3f();
		Vector3f start, end;
		ArrayList<MutationLibraryEntry> list;

		int selectedMutationIndex;
		
		Conformation originalConformation = protein.conformation;
		
		MutationLibraryEntry entry;

		// upon success the method will return
		while (nTries < max_tries) { 

			nTries++;
			
			out.reset();

			// Generate a number between [1..protein.size-2]
			mutationLength = random.nextInt(Math.min(protein.size() - 1,
					mutationLibraries.length) - 2) + 2;

			// the start monomer may be between 1 and protein size- mutation
			// length
			mutationStartMonomer = random.nextInt(protein.size()
					- mutationLength - 1) + 1;
			
			mutationLastMonomer = mutationLength + mutationStartMonomer - 1;

			// get position vector of start monomer.
			start = protein.get(mutationStartMonomer).getR();

			// get position vector of end monomer.
			end = protein.get(mutationLastMonomer).getR();

			representativeVector.sub(end, start);

			// if start monomer and end monomer are on a straight line no
			// mutation is possible.
			if (representativeVector.length() + 1 == mutationLength)
				continue;

			mutationLibrary = mutationLibraries[mutationLength];
			
			list = mutationLibrary.get(representativeVector);

			if (list == null) // list not found for given arguments

				throw new RuntimeException("ERROR: \nstart vector:" + start
						+ " \nend Vector" + end + "\nsub:"
						+ representativeVector + "\n" + "Mutation length:"
						+ mutationLength + "\n" + "Mutation Start Monomer:"
						+ mutationStartMonomer + "\n"
						+ "Mutation Last Monomer:" + mutationLastMonomer + "\n"
						+ "Protein:" + protein.toString());

			if (list.size() > 0) {

				/* Filter mutations - save only valid mutations */

				// collect position of the protein before and after the
				// mutation.
				// save them not as absolut position but as offset from the star
				// of mutation, this
				// helps us compare them to the position needed by the mutation.
				proteinPositions.clear();

				for (int i = 0; i < protein.size(); i++) {

					if ((i < mutationStartMonomer || i > mutationLastMonomer)) {

						// TODO: avoid creating a new vector.
						Vector3f vec = new Vector3f(protein.get(i).getR());

						// Save position as an offset from start of mutation.
						vec.sub(start);

						proteinPositions.add(vec);
					}
				}

				// Save in candidates only the mutations that do no overlap with
				// already occupied position of the protein.
				// (the position collected in the previous loop)
				candidates.clear();

				for (MutationLibraryEntry lib : list) {

					boolean found = false;

					for (Vector3f vec : lib.positionOffsets)
						if ((found = proteinPositions.contains(vec)))
							break;

					if (!found)
						candidates.add(lib);
				}

				// if no candidates try new mutation.
				if (candidates.isEmpty())
					continue;

				// Select random mutation from candidates.
				selectedMutationIndex = selectMutationNumber(candidates);

				entry = candidates.get(selectedMutationIndex);

				success = activateMutationOnProtein(out, protein, entry,
						mutationStartMonomer, mutationLastMonomer,
						originalConformation);

				/*
				 * while(!list.isEmpty() && !success){ selectedMutationIndex =
				 * selectMutationNumber(list); entry =
				 * list.remove(selectedMutationIndex); success =
				 * activateMutationOnProtein(out, protein, entry,
				 * mutationStartMonomer, mutationLastMonomer,
				 * originalConformation); }
				 */
			}

			if (success)
				return;
		}
		out.reset();
	}
}
