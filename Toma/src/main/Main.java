package main;

import java.util.HashSet;
import java.util.Set;

import algorithm.Algorithm;
import algorithm.Toma;

import problem.Problem;

public class Main {

	public static void main(String[] args) {
		
		Algorithm algorithm = new Toma();
		
		Set<Problem> problems = new HashSet<Problem>();
		
		for (Problem problem : problems){
			
			algorithm.init(problem);
			
			new Thread(algorithm).start();
		}
	}
}
