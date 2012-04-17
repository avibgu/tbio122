package utilities;

import java.util.HashSet;
import java.util.Set;

import problem.ProteinProblem;

public class FileManipulator {

	public static Set<ProteinProblem> getProblemsFromFile(String pFileName) {
		
		Set<ProteinProblem> answer = new HashSet<ProteinProblem>();
		
		answer.add(new ProteinProblem("ppphhpphhppppphhhhhhhpphhpppphhpphpp"));

		return answer;
	}

}
