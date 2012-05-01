package main;

import java.io.IOException;
import java.util.Date;

import static java.lang.Integer.parseInt;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import mutation.MutationManager;

public class GA {

	private static long startTime;

	public static void main(String[] argv) {
		
		startTime = new Date().getTime();
		Configuration config = getConfiguration(argv);
		OutputFilesWriter outWriter = getOutWriter(config);
		MutationManager mutationManager = getMutationManager(config);
		GARun run = new GARun(outWriter, config, mutationManager);

		for (int i = 0; i < config.numberOfRepeats; i++) {
			
			run.initiate(i); // An independent run
			System.out.println("Starting Run " + i + " ...");
			run.execute();
			long runningTime = (new Date().getTime() - startTime) / 1000;
			System.out.println("Done. Time: " + runningTime
					+ " seconds. Faild " + mutationManager.numOfFailers
					+ " out of " + mutationManager.numOfIterations);
		}
		try {
			outWriter.close();
		} catch (IOException ex1) {
			throw new RuntimeException("Failed to close output file\n" + ex1);
		}
	}

	private static Configuration getConfiguration(String[] argv) {
		if (argv.length != 2) {
			throw new RuntimeException(
					"Cannot run GA \n Usage: java main.GA <config file name> <seed>");
		}
		Configuration config;
		System.out.print("Loading Configuration ...");
		String configFileName = argv[0];
		System.out.println("Configuration file name " + configFileName);
		int seed;
		try {
			seed = parseInt(argv[1]);
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Cannot parse " + argv[1]
					+ " as a seed.");
		}

		try {
			config = new Configuration(configFileName, seed);
		} catch (IOException ex1) {
			throw new RuntimeException(
					"Failed to find, open or read configuration file: "
							+ configFileName + "\n" + ex1);
		}

		System.out.println("OK!");
		return config;
	}

	private static OutputFilesWriter getOutWriter(Configuration config) {
		System.out.print("Creating output file writers ...");
		OutputFilesWriter outWriter;
		try {
			outWriter = new OutputFilesWriter(Long.toString(config.seed),
					config);
		} catch (IOException ex1) {
			throw new RuntimeException("Cannot open output file\n" + ex1);
		}
		System.out.println("OK!");
		return outWriter;
	}

	private static MutationManager getMutationManager(Configuration config) {
		System.out.print("Loading Mutation Manager ...");
		MutationManager mutationManager = new MutationManager(config);
		System.out.println("OK!");
		return mutationManager;
	}
}
