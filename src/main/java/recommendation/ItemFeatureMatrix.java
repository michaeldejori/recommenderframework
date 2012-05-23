package recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

import GUI.RecommenderGUI;

import bean.Feature;
import bean.Movie;
import bean.Rating;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.sse.Item;

public class ItemFeatureMatrix {

	private RecommenderGUI gui = null;

	public static final String DBPEDIA_UNWEIGHTED = "1";
	public static final String FREEBASE_UNWEIGHTED = "2";

	public static String ids2freebaseFile = "files/ids2freebase.dat"; // files
																		// freebaseids
	public static String ids2dbpediaFile = "files/ids2dbpedia.dat"; // files
																	// dbpedia
																	// ids

	// file moviePredicates, movieURI predicate object
	public static String movieFreebasePredicatesFile = "files/moviePredicates.dat";
	public static String movieDBPediaPredicatesFile = "files/moviePredicatesDBPedia.dat";

	// Predicate Hasmaps
	// matrix id -> <pred>:<ob>
	private HashMap<Integer, String> hm_predicate_int_to_uri = new HashMap<Integer, String>();
	private HashMap<String, Integer> hm_predicate_uri_to_int = new HashMap<String, Integer>();
	// 127 -> <uri>
	private HashMap<Long, String> idtoURIHashMap = null;

	// count accurence of predicates, useful to see which one to eliminate
	private HashMap<String, Integer> hm_predicates_count = null;

	private EndpointConnector endpointConnector;

	private List<String> filterlist = new ArrayList<String>();

	public ItemFeatureMatrix(RecommenderGUI gui) {
		this.endpointConnector = new EndpointConnector();
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

	/**
	 * 
	 * @param source
	 *            dbpedia
	 * @param weightedfeatures
	 */
	public void initialize(String source) {
		this.initializeMovieHashmap(source);
		this.removeUnderTreshold();
		this.initializeFilter(source);
		this.initializePredicateHashMap(source);
	}

	private void initializeFilter(String source) {
		if (source.equals(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED)) {
				filterlist.add("<http://dbpedia.org/property/starring>");
				filterlist.add("<http://dbpedia.org/property/producer>");
				filterlist.add("<http://dbpedia.org/property/writer>");
				filterlist.add("<http://dbpedia.org/property/director>");
				filterlist.add("<http://dbpedia.org/ontology/musicComposer>");
				filterlist.add("<http://dbpedia.org/ontology/distributor>");
				filterlist.add("<http://dbpedia.org/property/screenplay>");
				filterlist.add("<http://dbpedia.org/property/story>");
			
		}
	}

	/**
	 * initializeMovieHashmap(source) initializes the Hasmap, from movie Lens ID
	 * to URI
	 * 
	 * @param source
	 *            data source (dbpedia or freebase)
	 */
	private void initializeMovieHashmap(String source) {
		this.gui.pushStatusMessage("initializing Movie Hash ID");
		try {
			this.idtoURIHashMap = new HashMap<Long, String>();

			BufferedReader reader = null;

			if (source.equals(ItemFeatureMatrix.FREEBASE_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						ItemFeatureMatrix.ids2freebaseFile)));
			else if (source.equals(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						ItemFeatureMatrix.ids2dbpediaFile)));
			if (reader != null) {
				String line = reader.readLine();

				while (line != null) {
					String[] parts = line.split("\t");
					if (parts.length == 2) {
						Long id = Long.parseLong(parts[0]);
						String uri = parts[1];
						this.idtoURIHashMap.put(id, "<" + uri + ">");
					}
					line = reader.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.gui.pushStatusMessage("Finished initializing Movie Hash ID");
	}

	/**
	 * initializePredicateHashMap initializes double Hashtable id for matrix ->
	 * pred:ob and vice versa
	 * 
	 * @param source
	 *            data source
	 */
	private void initializePredicateHashMap(String source) {
		this.gui.pushStatusMessage("Initializing predicates of movies");
		BufferedReader reader = null;
		try {

			if (source.equals(ItemFeatureMatrix.FREEBASE_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						ItemFeatureMatrix.movieFreebasePredicatesFile)));
			else if (source.equals(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						ItemFeatureMatrix.movieDBPediaPredicatesFile)));

			if (reader != null) {
				String line = reader.readLine();

				// index for hashmap and then matrix
				int i = 1;

				while (line != null) {

					String[] parts = line.split("\t");
					// <sub> <pred> <ob>
					if (parts.length == 3) {
						// cut the dot at the end
						String feature = parts[1] + ":"
								+ parts[2].substring(0, parts[2].length() - 1);

						// check if movie not filtered out, under treshhold
						if (!hm_predicate_uri_to_int.containsKey(feature)
								&& this.idtoURIHashMap.containsValue(parts[0])
								&& filterlist.contains(parts[1])) {
							// make entry
							hm_predicate_uri_to_int.put(feature, i);
							hm_predicate_int_to_uri.put(i, feature);
							i++;

						}

					}
					line = reader.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.gui.pushStatusMessage("Finished initializing predicates + " + this.getCountDistinctPredicates());
	}

	/*
	 * public void initializeFromRepository() { this.initialize(); HashMap<Long,
	 * String> hmids2freebase = getFreebaseIdsFromFile(ids2freebaseFile);
	 * removeUnderTreshold(hmids2freebase); Collection<String> col =
	 * hmids2freebase.values();
	 * 
	 * System.out.println("Creating file:"); try { FileWriter filewriter = new
	 * FileWriter("files/moviePredicates.dat"); BufferedWriter bf = new
	 * BufferedWriter(filewriter);
	 * 
	 * bf.write("movieURI\tpredicate\tobject"); bf.newLine();
	 * 
	 * int j = 1;
	 * 
	 * for (Iterator<String> iterator = col.iterator(); iterator.hasNext();) {
	 * String movieURI = (String) iterator.next();
	 * System.out.println("Processing: " + movieURI);
	 * System.out.println("Fortschritt: " + j + "/" +
	 * this.getCountMovieItems()); Vector<String> v = this.endpointConnector
	 * .retrievePredicatesOfMovie("<" + movieURI + ">"); for (int i = 0; i <
	 * v.size(); i++) { bf.write(v.get(i)); bf.newLine(); } j++; }
	 * 
	 * bf.close(); filewriter.close(); } catch (IOException e) { } }
	 */

	private void removeUnderTreshold() {
		this.gui.pushStatusMessage("Removie Movies under treshold");
		RemoveUsersThreshold rem = new RemoveUsersThreshold();
		Vector<Integer> remV = rem.getMoviesRatedUnderMinTreshold();
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

	

	public Vector<Feature> getFeaturesOfMovie(int parseInt) {
		String uri = this.idtoURIHashMap.get(new Long(parseInt));
		BufferedReader reader;
		Vector<Feature> featureVec = new Vector<Feature>();

		try {

			reader = new BufferedReader(new FileReader(new File(
					movieDBPediaPredicatesFile)));

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length == 3 && parts[0].equals(uri) && filterlist.contains(parts[1])) {
					Feature f = new Feature();
					f.setPredicate(parts[1]);
					f.setObject(parts[2].substring(0, parts[2].length() - 1));
					featureVec.add(f);
				}
				line = reader.readLine();
			}

			return featureVec;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getMatrixIndexOf(String feature) {
		return this.hm_predicate_uri_to_int.get(feature);
	}

	public DenseMatrix64F getVectorOfMovieID(int movie_lensID) {
		DenseMatrix64F userProfileMatrx = new DenseMatrix64F(1, this.getCountDistinctPredicates());
		Vector<Feature> feature = this.getFeaturesOfMovie(movie_lensID);
		for (int i = 0; i < feature.size(); i++) {
			Feature f = feature.get(i);
			userProfileMatrx.set(0, this.getMatrixIndexOf(f.getPredicate() + ":" + f.getObject()), 1);
		}
		return userProfileMatrx;
	}

}
