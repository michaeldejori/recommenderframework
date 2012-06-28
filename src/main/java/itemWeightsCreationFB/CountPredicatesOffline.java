package itemWeightsCreationFB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

import bean.Feature;
import bean.Triple;

import recommendation.ItemFeatureMatrix;

public class CountPredicatesOffline {

	private static double movies_in_store = 10078.0;
	// private static double movies_in_store = 9329.0;

	// Repository
	private static final String SESAME_SERVER = "http://lokino.sti2.at:8080/openrdf-sesame";
	// private static final String SESAME_SERVER =
	// "http://localhost:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "fb";
	private static Repository myRepository = null;

	public static String movieDBPediaPredicatesFile = "files/moviePredicatesFB.dat";
	public static HashMap<Long, String> idtoURIHashMap = null;
	public static Vector<Triple> tripleStore = null;
	public static Vector<String> existingURI = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// movie Hashmap
		initIdHashMap();
		initTripleStore();
		initExistingUriVector();
		Collection<String> col = idtoURIHashMap.values();
		FileWriter filewriter;
		try {
			filewriter = new FileWriter("files/freebasefeaturescores.dat", true);

			BufferedWriter bf = new BufferedWriter(filewriter);
			int i = 0;
			for (Iterator<String> it = col.iterator(); it.hasNext();) {
				String uri = it.next();
				if (!existingURI.contains(uri)){
					
					TreeMap<Double, String> treemap = calculateScores(uri);
					Set<Double> key = treemap.descendingKeySet();
					for (Double double1 : key) {
						double double2 = (double) Math.round(double1 * 100) / 100.0;
						bf.write(double2 +"\t" + uri + "\t" + treemap.get(double1));
						// System.out.println(double2 +"\t" + uri + "\t" + treemap.get(double1));
						bf.newLine();
						bf.flush();
					}
				} else {
					System.out.println(uri + " already contained");
				}
				System.out.println(i++);

			}
			bf.close();
			filewriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void initExistingUriVector() {
		System.out.println("initializeVectorOfExistingFeatures");
		if (existingURI == null)
			existingURI = new Vector<String>();
		try {

			BufferedReader reader;

			reader = new BufferedReader(new FileReader(new File("files/freebasefeaturescores.dat")));

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length >= 2) {
					String uri = parts[1];
					if (!existingURI.contains(uri))
						existingURI.add(uri);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void initTripleStore() {
		tripleStore = new Vector<Triple>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(
					CountPredicatesOffline.movieDBPediaPredicatesFile)));

			if (reader != null) {
				String line = reader.readLine();

				while (line != null) {

					String[] parts = line.split("\t");
					// <sub> <pred> <ob>
					if (parts.length == 3) {
						String s = parts[0];
						String p = parts[1];
						String o = parts[2];
						// this predicates nodes are the same as hasActor and hasRole
						if (!p.equals("node599") && !p.equals("node322831")){
							Triple t = new Triple();
							t.setO(o);
							t.setP(p);
							t.setS(s);
							tripleStore.add(t);
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
		
	}

	public static TreeMap<Double, String> calculateScores(String uri) {
		System.out.println(uri);
		myRepository = new HTTPRepository(SESAME_SERVER, REPOSITORY_ID);
		// Map<String, Integer> objectcount = new HashMap<String, Integer>();
		// Map<String, Integer> predicatecount = new HashMap<String, Integer>();
		Map<String, Integer> predicateobjectcount = new HashMap<String, Integer>();
		Map<String, Integer> predicateobjectcount1 = new HashMap<String, Integer>();

		try {
			RepositoryConnection con = myRepository.getConnection();
			try {

				final String queryString3 = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
						+ " PREFIX knn:<http://knn.michael2/> PREFIX fb:<http://rdf.freebase.com/ns/>"
						+ " select ?s ?p ?o where { <"
						+ uri
						+ "> ?p ?o. <"
						+ uri
						+ "> knn:20 ?s."
						+ " ?s ?p ?o."
						+ " ?s rdf:type fb:film.film."
						+ " FILTER((?s != <"
						+ uri
						+ ">) && (?p != knn:20) && (?p != rdf:type) && (?p != <http://knn.michael/20>)" +
						" && (?p != <http://knn.idea/20>))" + "}";
				System.out.println(queryString3);
				
				TupleQuery tupleQuery = con.prepareTupleQuery(
						QueryLanguage.SPARQL, queryString3);
				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while (result.hasNext()) {
						BindingSet set = result.next();
						String s = set.getValue("s").stringValue();
						String p = set.getValue("p").stringValue();
						String o = set.getValue("o").stringValue();
						Integer predobjcount = predicateobjectcount.get(p
								+ "\t" + o);
						predicateobjectcount.put(p + "\t" + o,
								predobjcount == null ? 1 : predobjcount + 1);
					}
					System.out.println("fertig");
				} finally {
					result.close();
				}
			} finally {
				con.close();
			}
			Vector<Triple> resultSet = makeSecondQuery(uri);
			for (int i = 0; i < resultSet.size(); i++) {
				Triple t = resultSet.get(i);
				Integer predobjcount1 = predicateobjectcount1.get(t.getP()
						+ "\t" + t.getO());
				predicateobjectcount1.put(t.getP() + "\t" + t.getO(),
						predobjcount1 == null ? 1 : predobjcount1 + 1);
			}

		} catch (OpenRDFException e) {
			e.printStackTrace();
		}

		// Set<String> keys = objectcount.keySet();
		// for (String string : keys) {
		// int i = objectcount.get(string);
		// { System.out.println(i + "\t" + string); }
		// }
		// System.out.println("#############################################");
		// Set<String> keys2 = predicatecount.keySet();
		// for (String string : keys2) {
		// int i = predicatecount.get(string);
		// { System.out.println(i + "\t" + string); }
		// }
		System.out.println("#############################################");
		Set<String> keys3 = predicateobjectcount.keySet();
		TreeMap<Double, String> map = new TreeMap<Double, String>();
		for (String string : keys3) {
			// System.out.println(string);
			int i = predicateobjectcount.get(string);
			Integer inte = predicateobjectcount1.get(string);
			if (inte != null) {
				int j = inte.intValue();
				double value = i * Math.log(movies_in_store / ((double) j));
				map.put(value, string);
			} else {
				System.out.println("ACTUNG: " + string);
			}
		}
		return map;
	}

	private static Vector<Triple> makeSecondQuery(String uri) {
		/*
		 * String queryString2 =
		 * "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		 * "PREFIX knn:<http://knn.idea/>" + " select ?s ?p ?o where { " +
		 * " <http://dbpedia.org/resource/Toy_Story> ?p ?o." + " ?s ?p ?o." +
		 * " ?s rdf:type <http://schema.org/Movie>." +
		 * " FILTER((?s != <http://dbpedia.org/resource/Toy_Story>) && (?p != knn:20) && (?p != rdf:type))"
		 * + "}";
		 */

		Vector<Triple> res = new Vector<Triple>();
		
		for (int i = 0; i < tripleStore.size(); i++) {
			Triple t = tripleStore.get(i);
			// filterbedingung
			if (!(t.getS()).equals(uri)
					&& !t.getP().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
					&& !t.getP().equals("<http://knn.idea/20>")
					&& !t.getP().equals("<http://knn.michael2/20>")) {
				Triple rest = new Triple();
				// System.out.println(t.getS() + t.getP() + t.getO());
				rest.setS(t.getS());
				rest.setP(t.getP());
				rest.setO(t.getO());
				res.add(rest);
			}
		}
		
		
		return res;
	}

	public static void initIdHashMap() {
		try {
			idtoURIHashMap = new HashMap<Long, String>();

			BufferedReader reader = null;

			reader = new BufferedReader(new FileReader(new File(
					ItemFeatureMatrix.ids2freebaseFile)));

			if (reader != null) {
				String line = reader.readLine();

				while (line != null) {
					String[] parts = line.split("\t");
					if (parts.length == 2) {
						Long id = Long.parseLong(parts[0]);
						String uri = parts[1];
						idtoURIHashMap.put(id, uri );
					}
					line = reader.readLine();
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
