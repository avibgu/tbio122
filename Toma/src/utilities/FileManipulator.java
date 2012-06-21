package utilities;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import problem.Protein;

public class FileManipulator {

	public static Set<Protein> getProblemsFromFile(String pFileName) {

		// TODO: ..

		Set<Protein> answer = new HashSet<Protein>();

		// answer.add(new Protein("HPPH"));
		// answer.add(new Protein("HHHPPHPHPHPPHPHPHPPH"));
		// answer.add(new Protein("PPPHHPPHHPPPPPHHHHHHHPPHHPPPPHHPPHPP"));
		answer.add(new Protein("PHPPHHPPPPHHPPPPHHPPPPHH"));
		// answer.add(new Protein("PHPPHHPPHHPPPPHHHHHHHHHHPPPPPPHHPPHHPPHPPHHHHH"));

		return answer;
	}

	public static void writeResultsToFile(String pResults, String pFilename) {

		PrintWriter out = null;

		try {

			out = new PrintWriter(pFilename);
			out.write(pResults);
			out.close();
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
