package main;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class OutputFilesWriter 
{
	private FileWriter writerRes;
	private File resultFile;
	private Configuration config;
	private String seed;
	
	public OutputFilesWriter(String seed, Configuration config) throws IOException
	{		
		this.seed = seed;
		this.resultFile = new File(config.prefixForOutputFiles+ seed + ".csv");
		this.writerRes = new FileWriter(resultFile, true);
		this.config = config;
		
		writeTitles();
	}
	
	
	
	private void writeTitles() throws IOException 
	{
		writerRes.write("Sequence");
		writerRes.write(",");
		writerRes.write("Dimension");
		writerRes.write(",");
		writerRes.write("Population size");
		writerRes.write(",");
		writerRes.write("Generations");
		writerRes.write(",");
		writerRes.write("Higest fitness");
		writerRes.write(",");
		writerRes.write("Lowest energy");
		writerRes.write(",");
		writerRes.write("Conformation");
		writerRes.write("\n");
		//writerRes.close();
	}

	public void writeResults(GARun run) throws IOException
	{	
		writerRes.write(config.sequence);
		writerRes.write(",");
		writerRes.write(config.dimensions.number);
		writerRes.write(",");
		writerRes.write(Integer.toString(config.populationSize));
		writerRes.write(",");
		writerRes.write(Integer.toString(config.numberOfGenerations));
		
		Conformation best = run.getFitestOfRun();
		
		writerRes.write(",");
		writerRes.write(Float.toString(best.getFitness()));
		writerRes.write(",");
		writerRes.write(Float.toString(best.getEnergy()));
		writerRes.write(",");
		writerRes.write(best.toString());
		writerRes.write("\n");
        System.out.println(best);
		
		this.writeResultPerRun(run);
	}
	
	/**
	 * Write result per Generation. this method is used to print to file the results of the run per generation
	 * 
	 * @param run the run
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeResultPerRun(GARun run) throws IOException {
		File file = new File(config.prefixForOutputFiles + this.seed +"-"+ run.getId() + ".csv");
		FileWriter writer = new FileWriter(file, true);
		
		writer.write("Generation,Best Fitness,Average Fitness,Worst Fitness, " +
				"Best Energy,Average Energy, Worst Energy, Effective Population \n");
		String line = "";
		for (int i=0;i<config.numberOfGenerations/config.reportEvery;i++){
			
			line = "" + run.getGeneration()[i];
			line +="," + run.getBestFitness()[i];
			line +="," + run.getAverageFitness()[i];
			line +="," +run.getWorstFitness()[i];
			line +="," + run.getBestEnergy()[i];
			line +="," + run.getAverageEnergy()[i];
			line +="," + run.getWorstEnergy()[i];
			writer.write(line + "\n");
		}
		writer.close();
	}
	
	public void close() throws IOException
	{
		writerRes.close();
	}
}
