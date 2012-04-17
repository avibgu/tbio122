/*
 *
 */
package main;

import java.io.IOException;
import java.util.Random;

import mutation.MutationManager;


/**
 * A single run of the  GA algorithm.
 */
public class GARun {
    public static final boolean debug = true;

	private boolean isFirstInit;
	/** The id for the run */
	private String id;

	//Data per generation
	/** The best energy. */
	private float[] bestEnergy;

	/** The best fitness. */
	private float[] bestFitness;

	/** The average energy. */
	private float[] averageEnergy;

	/** The average fitness. */
	private float[] averageFitness;

	/** The worst energy. */
	private float[] worstEnergy;

	/** The worst fitness. */
	private float[] worstFitness;

    public int[] getGeneration() {
        return generation;
    }

    private int generation[];

	/** The fitest per generation */
	private Conformation[] fittest;



	//Data for All the Run
	/** The protein  population. */
	private Population population;

	/** The mutation manager. */
	private MutationManager mutationManager;

	/** The current generarion num. */
	private int currentGenerarionNum;

	/** The random number for usage in the run. */
	private Random random;

    /** the output file writer. */
    private OutputFilesWriter fileWriter;

    /** The configuration class to get User given arguments. */
    private Configuration config;

    private int numberOfGenerations, reportEvery;

    /**
     * Instantiates a new Genetic Algorithm (GA) run.
     *
     * @param fileWriter the file writer to write the stored data in the run
     * @param config the configuration object to load user given data
     */

    public GARun(OutputFilesWriter fileWriter, Configuration config, MutationManager mutationManager) {

    	this.config = config;
        this.random = config.random;
		this.fileWriter = fileWriter;
		this.mutationManager = mutationManager;
        isFirstInit = true;
        this.numberOfGenerations =  config.numberOfGenerations;
        this.reportEvery = config.reportEvery;
	}

    public void initiate(int runNumber){
		//create new random population

	    System.out.println("Initializing run # "+runNumber);
        setId("runNumber "+runNumber);

        population = new Population(config, random, mutationManager);
		//Collections.sort(population, Collections.reverseOrder());
	    population.sort();
		currentGenerarionNum = 0;
		if (isFirstInit){ //if this is the first init then create the needed arrays else. clear the existing one's
            population.sort();
            //Collections.sort(population, Collections.reverseOrder());
			bestEnergy            = new float[numberOfGenerations/reportEvery+1];
			averageEnergy     = new float[numberOfGenerations/reportEvery+1];
			worstEnergy         = new float[numberOfGenerations/reportEvery+1];
			bestFitness         = new float[numberOfGenerations/reportEvery+1];
			averageFitness  = new float[numberOfGenerations/reportEvery+1];
			worstFitness       = new float[numberOfGenerations/reportEvery+1];
			fittest                   = new Conformation[numberOfGenerations/reportEvery+1];
            generation            = new int[numberOfGenerations/reportEvery+1];
		}
	    for (int i=0;i<numberOfGenerations/config.reportEvery+1;i++){
				bestEnergy[i] = 0;
				averageEnergy[i] = 0;
				worstEnergy[i] = 0;
				bestFitness[i] = 0;
				averageFitness[i] = 0;
				worstFitness[i] = 0;
				fittest[i] = null;
		}
    }


    /**
     * Execute the run.
     */
    public void execute() {
        Protein in1, in2, out1, out2;
        Protein[] temp;

        long startTime = System.currentTimeMillis();
        long runningTime;
    	for (currentGenerarionNum = 0; currentGenerarionNum < config.numberOfGenerations; currentGenerarionNum++){
           // Collections.sort(population, Collections.reverseOrder());
    		population.sort();
            if (currentGenerarionNum%config.reportEvery==0) {
                bestEnergy[currentGenerarionNum/reportEvery]         =  population.getBestEnergy();
                averageEnergy[currentGenerarionNum/reportEvery]  =  population.getAverageEnergy();
                worstEnergy[currentGenerarionNum/reportEvery]       =  population.getWorstEnergy();
                fittest[currentGenerarionNum/reportEvery]                 =  population.getFirst().conformation;
                averageFitness[currentGenerarionNum/reportEvery] =  population.getAverageFitness();
                worstFitness[currentGenerarionNum/reportEvery]      =  population.getLast().getFitness();
                bestFitness[currentGenerarionNum/reportEvery]        =  population.getFirst().getFitness();
                generation[currentGenerarionNum/reportEvery]           =  currentGenerarionNum;
            }

            if (random.nextFloat() <config.crossoverRate) {
               in1    = population.chooseProtein();
               in2    = population.chooseProtein();
               temp = population.getLastTwo();
               out1 = temp[0];
               out2 = temp[1];
               Protein.crossover(in1, in2, out1, out2, random);
               population.updateLastTwo();

            }
            else {
                  in1 = population.chooseProtein();
                  out1  = population.getLast();
                  mutationManager.mutate(in1,out1,10);
                  population.updateLastTwo();
            }



			runningTime = (System.currentTimeMillis() - startTime);
			if(currentGenerarionNum%config.reportEvery == 0)
                System.out.println("Generation  "+currentGenerarionNum+"  out of   "+config.numberOfGenerations+
                                                     "  Time is:  " + runningTime + "  msec.      Fittest \n"+population.getFirst());
    	}
    	//write results after run
        try {
    	    fileWriter.writeResults(this);
        } catch (IOException ex) {throw new RuntimeException("Failed to write output file\n"+ex) ;}
    }

    /**
     * Gets the fitest.
     *
     * @return the fitest
     */
    public Conformation getFitestOfRun(){
        Conformation best = fittest[0];
        for (int i=0; i< fittest.length; i++) {
           if (fittest[i] != null) {
               if (fittest[i].getFitness() > best.getFitness())
                    best = fittest[i];
           }
        }
        return best;
    }

	/**
	 * Gets the best energy per generation array.
	 *
	 * @return the best energy
	 */
	public float[] getBestEnergy() {
		return bestEnergy;
	}

	/**
	 * Gets the best fitness per generation array.
	 *
	 * @return the best fitness
	 */
	public float[] getBestFitness() {
		return bestFitness;
	}

	/**
	 * Gets the average energy per generation array.
	 *
	 * @return the average energy
	 */
	public float[] getAverageEnergy() {
		return averageEnergy;
	}

	/**
	 * Gets the average fitness per generation array.
	 *
	 * @return the average fitness
	 */
	public float[] getAverageFitness() {
		return averageFitness;
	}

	/**
	 * Gets the worst energy per generation array.
	 *
	 * @return the worst energy array
	 */
	public float[] getWorstEnergy() {
		return worstEnergy;
	}

	/**
	 * Gets the worst fitness per generation array.
	 *
	 * @return the worst fitness array
	 */
	public float[] getWorstFitness() {
		return worstFitness;
	}


	/**
	 * Gets the fitest Of Generation array.
	 *
	 * @return the fitest array
	 */
	public Conformation[] getFittest() {
		return fittest;
	}


	/**
	 * Gets the id of this Run.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the Run.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
}
