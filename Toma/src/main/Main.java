package main;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import algorithm.TomaAlgorithm;
import algorithm.ConcreteTomaAlgorithm;

import problem.Protein;
import utilities.FileManipulator;
import utilities.ProteinGraph;

public class Main {

	public static final Properties prop = new Properties();

	public static void main(String[] args) throws Exception {

		ExecutorService executor = Executors.newFixedThreadPool(5);

		initPropertiesFromFile(args[1]);

		TomaAlgorithm algorithm = null;

		Set<Protein> proteins = FileManipulator.getProteinsFromFile(args[0]);

		for (Protein protein : proteins) {

			algorithm = new ConcreteTomaAlgorithm(protein);

			executor.execute(algorithm);
		}

		executor.shutdown();

		executor.awaitTermination(1, TimeUnit.DAYS);

		produceRusultsAsCSV(proteins);
		produceRusultsAsGraph(proteins);
	}

	protected static void produceRusultsAsCSV(Set<Protein> pProteins) {

		int i = 1;

		for (Protein protein : pProteins)
			FileManipulator.writeResultsToFile(protein.getResults(), "problem"
					+ i++ + ".csv");
	}

	protected static void produceRusultsAsGraph(Set<Protein> pProteins) {
		for (Protein protein : pProteins)
			ProteinGraph.show(protein.getResults());
	}

	private static void initPropertiesFromFile(String pPropertiesFileLocation)
			throws Exception {

		prop.load(new FileInputStream(new File(pPropertiesFileLocation)));

		if (null == prop.getProperty("SEED")
				|| null == prop.getProperty("NUM_OF_RUNS")) {

			throw new Exception("Please provide a valid Properties file.");
		}
	}
}
