package mutation;

import java.util.*;

import javax.vecmath.Vector3f;

/**
 * The Class MutationDictonary. the class is using a java {@link java.util.Hashtable} only allowing
 * to add multiple items in one key. (By chaining method using {@link java.util.LinkedList})
 *
 * 
 * @see java.util.Hashtable
 * @see java.util.LinkedList
 */
public class MutationLibrary {
	
	/** The hashTable containing the data. */
	private Hashtable<Vector3f, ArrayList<MutationLibraryEntry>> data;
	
	/** The size of the <b>elements</b> in the table */
	private int size;

	
	/**
	 * Instantiates a new mutation dictonary.
	 */
	public MutationLibrary() {
		data = new Hashtable<Vector3f, ArrayList<MutationLibraryEntry>>();
		size = 0;
	}
	
	/**
	 * put the element in the table in the end of the list for the key
	 * but does not allow the same object in the same list
	 * 
	 * @param key the key
	 * @param element the element
	 */
	public void put(Vector3f key, MutationLibraryEntry element) {
		if (!data.containsKey(key))
			data.put(key, new ArrayList<MutationLibraryEntry>());
		
		//TODO: check for duplicity problem here !!!
		data.get(key).add(element);
		size++;
	}
	
	/**
	 * Gets the List of elements for the key parameter
	 * 
	 * @param key the key
	 * 
	 * @return the linked list<dictonary entry>
	 */
	public ArrayList<MutationLibraryEntry> get(Vector3f key) {
        ArrayList<MutationLibraryEntry> out= new ArrayList<MutationLibraryEntry>(data.get(key));
		return out;
	}
	
	/**
	 * Return the size of the elements in the table
	 * 
	 * @return the size of the elements in the table
	 */
	public int size() {
		return size;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String ans = "";
		Enumeration<Vector3f> iterKeys = data.keys();
		Vector3f vector;
		while (iterKeys.hasMoreElements())
		{
			vector = iterKeys.nextElement();
			ans += "<" + vector.toString() + " | " + data.get(vector).toString() + ">\n";
		}
		return ans;
	}	
}
