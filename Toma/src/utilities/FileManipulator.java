package utilities;

import java.util.HashSet;
import java.util.Set;

import problem.ProteinProblem;

public class FileManipulator {

	public static Set<ProteinProblem> getProblemsFromFile(String pFileName) {
		
		// TODO: ..
		
		Set<ProteinProblem> answer = new HashSet<ProteinProblem>();
		
		answer.add(new ProteinProblem("HHHPPHPHPHPPHPHPHPPH"));

		return answer;
	}
}
