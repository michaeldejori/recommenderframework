package recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import GUI.RecommenderGUI;

public class ItemFeatureMatrix {

	private RecommenderGUI gui = null;

	public static String ids2freebaseFile = "files/ids2freebase.dat"; // files
																	// freebaseids
	public static String ids2dbpediaFile = "files/ids2dbpedia.dat"; // files
																	// dbpedia
																	// ids

	private Mediator m = null;
	
	// Predicate Hasmaps
	// matrix id -> <pred>:<ob>
	private HashMap<Integer, String> hm_predicate_int_to_uri = new HashMap<Integer, String>();
	private HashMap<String, Integer> hm_predicate_uri_to_int = new HashMap<String, Integer>();
	// 127 -> <uri>
	private HashMap<Long, String> idtoURIHashMap = null;

	// count accurence of predicates, useful to see which one to eliminate
	private HashMap<String, Integer> hm_predicates_count = null;

	private List<String> filterlist = new ArrayList<String>();

	public ItemFeatureMatrix(RecommenderGUI gui) {
		this.gui = gui;
	}

	/**
	 * countPredicatesOccurence() counts how often the different predicates
	 * occur counts in hm_predicate_uri_to_int
	 */
	public void countPredicatesOccurence() {
		hm_predicates_count = new HashMap<String, Integer>();
		Set<String> set = hm_predicate_uri_to_int.keySet();
		for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			String predicate = s.substring(0, s.indexOf(">:<") + 1);
			if (!hm_predicates_count.containsKey(predicate)) {
				hm_predicates_count.put(predicate, 0);
			}
			Integer l = hm_predicates_count.get(predicate);
			hm_predicates_count.put(predicate, new Integer(l + 1));
		}
	}

	public void printPredicatesOccurences() {
		Set<String> s = hm_predicates_count.keySet();
		for (Iterator<String> iterator = s.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();

			System.out.println(string + " -> "
					+ hm_predicates_count.get(string));

		}
	}



	private void removeUnderTreshold() {
		if (this.gui != null)
			this.gui.pushStatusMessage("Removie Movies under treshold");
		RemoveUsersThreshold rem = new RemoveUsersThreshold();
		Vector<Integer> remV = rem.getMoviesRatedUnderMinTreshold();
		if (this.gui != null)
			this.gui.pushStatusMessage("Removing " + remV.size() + " movies");
		for (int i = 0; i < remV.size(); i++) {
			this.idtoURIHashMap.remove(remV.get(i));
		}
	}

	public int getCountMovieItems() {
		return this.idtoURIHashMap != null ? this.idtoURIHashMap.size() : -1;
	}

	public int getCountDistinctPredicates() {
		return hm_predicate_int_to_uri != null ? hm_predicate_int_to_uri.size()
				: -1;
	}


	public int getMatrixIndexOf(String feature) {
		return this.hm_predicate_uri_to_int.get(feature);
	}



}
