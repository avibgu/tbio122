package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import problem.Protein;

public class FileManipulator {

	public static Set<Protein> getProblemsFromFile(String pFileName) {

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(new File(pFileName));
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return readFromInputStream(fis);
	}

	public static Set<Protein> readFromInputStream(InputStream pInputStream) {

		Set<Protein> answer = new HashSet<Protein>();

		InputStreamReader isr = null;
		BufferedReader br = null;

		try {

			isr = new InputStreamReader(pInputStream);
			br = new BufferedReader(isr);

			while (br.ready())
				answer.add(new Protein(br.readLine()));

			pInputStream.close();
			isr.close();
			br.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

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
