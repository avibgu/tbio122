package main;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import algorithm.Algorithm;
import algorithm.Toma;

import problem.Problem;
import utilities.FileManipulator;

public class Main {

	public static final Properties prop = new Properties();

	public static void main(String[] args) throws Exception{
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		try {
		
			initPropertiesFromFile(args[1]);
		}
		
		catch (Exception e) {
			
			initDefaultProperties();
		}
		
		Algorithm algorithm;
		
		Set<Problem> problems = FileManipulator.getProblemsFromFile(args[0]);
		
		for (Problem problem : problems){
			
			algorithm = new Toma(problem);
			
			executor.execute(algorithm);
		}
		
		executor.awaitTermination(1, TimeUnit.DAYS);
	}

	private static void initPropertiesFromFile(String pPropertiesFileLocation) throws Exception{

		prop.load(new FileInputStream(new File(pPropertiesFileLocation)));
		
		if (null == prop.getProperty("SEED"))
			prop.setProperty("SEED","17");
		
		if (null == prop.getProperty("NUM_OF_RUNS"))
			prop.setProperty("NUM_OF_RUNS","10000");
	}
	
	private static void initDefaultProperties() {

		prop.setProperty("SEED","17");
		prop.setProperty("NUM_OF_RUNS","10000");
	}
}
