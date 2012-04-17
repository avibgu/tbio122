package utilities;

import java.util.HashSet;
import java.util.Set;

import problem.Problem;
import problem.ProteinProblem;

public class FileManipulator {

	public static Set<Problem> getProblemsFromFile(String pString) {
		
		Set<Problem> answer = new HashSet<Problem>();
		
		answer.add(new ProteinProblem("ppphhpphhppppphhhhhhhpphhpppphhpphpp"));

		return answer;
	}

}
