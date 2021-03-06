package main;

import java.util.ArrayList;

/**
 * The sequence of monomer types that defines a protein.
 */
public class Sequence extends ArrayList<MonomerType> {

	private static final long serialVersionUID = -5416242680338342084L;

	public Sequence(String sequenceString) {

		for (int i = 0; i < sequenceString.length(); i++) {

			char c = sequenceString.charAt(i);
			add(MonomerType.byChar(c));
		}
	}

	public String toString() {

		String out = "";

		for (MonomerType type : this)
			out += type;

		return out;
	}
}
