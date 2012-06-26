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

		TomaAlgorithm algorithm;

		Set<Protein> problems = FileManipulator.getProblemsFromFile(args[0]);

		for (Protein problem : problems) {

			algorithm = new ConcreteTomaAlgorithm(problem);

			executor.execute(algorithm);
		}

		executor.shutdown();

		executor.awaitTermination(1, TimeUnit.DAYS);

		produceRusultsAsCSV(problems);
		produceRusultsAsGraph(problems);
	}

	protected static void produceRusultsAsCSV(Set<Protein> problems) {

		int i = 1;

		for (Protein problem : problems) {

			System.out.println("FINISH\n" + problem.getResults());

			FileManipulator.writeResultsToFile(problem.getResults(), "problem"
					+ i++ + ".csv");
		}
	}

	protected static void produceRusultsAsGraph(Set<Protein> problems) {
		for (Protein problem : problems)
			ProteinGraph.show(problem.getResults());
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
