package prepareFreeBaseData;

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
import java.util.Vector;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class CreateMoviePredicatesFile {

	private static final String SESAME_SERVER = "http://lokino.sti2.at:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "fb";
	private static Repository myRepository = null;
	public static String dbpediaIdsFile = "files/ids2freebase.dat"; // files
																	// dbpedia
																	// ids
	// file moviePredicates, movieURI predicate object
	public static String moviePredicatesFileFreebase = "files/moviePredicatesFreebase2.dat";
	private static Vector<String> dbpediaEntriesContainedinFile = null;
	private static HashMap<Long, String> idtoDBPediaHashMap = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initializeidstoDBPediaHashmap();
		createDBPediaMovieFeaturesFile();

	}

	/**
	 * Hashmap movie IDs to dbpedia IDs is initialized
	 */
	public static void initializeidstoDBPediaHashmap() {
		try {
			idtoDBPediaHashMap = new HashMap<Long, String>();

			BufferedReader reader;

			reader = new BufferedReader(new FileReader(new File(dbpediaIdsFile)));

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length == 2) {
					// System.out.println(parts[0] + " " + parts[1]);
					Long id = Long.parseLong(parts[0]);
					String freebase = parts[1];
					idtoDBPediaHashMap.put(id, freebase);
				}
				line = reader.readLine();
			}
			System.out.println("Finished loading ids uri HashMap");
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void createDBPediaMovieFeaturesFile() {
		System.out.println("Creating file:");
		initializeVectorOfExistingFeatures();
		try {
			FileWriter filewriter = new FileWriter(moviePredicatesFileFreebase, true);
			BufferedWriter bf = new BufferedWriter(filewriter);

			// bf.write("movieURI\tpredicate\tobject");
			// bf.newLine();

			int j = 1;
			int total = idtoDBPediaHashMap.size();
			Collection<String> col = idtoDBPediaHashMap.values();

			for (Iterator<String> iterator = col.iterator(); iterator.hasNext();) {
				String movieURI = (String) iterator.next();

				if (!isContainedInMoviePredicates("<" + movieURI + ">")) {
					System.out.println("Processing: " + movieURI);
					System.out.println("Fortschritt: " + j + "/" + total);
					Vector<String> v = getFeaturesOfMovie(movieURI);
					if (v == null) {
						bf.write("<" + movieURI + ">\t<no:entry>\t<no:entry>.\n");
					} else {
						for (int i = 0; i < v.size(); i++) {
							bf.write("<" + movieURI + ">\t" + v.get(i));
						}
					}
				} else {
					System.out.println("movie " + movieURI + " already contained");
				}

				j++;
			}

			bf.close();
			filewriter.close();
		} catch (IOException e) {
		}
	}

	public static void initializeVectorOfExistingFeatures() {
		System.out.println("initializeVectorOfExistingFeatures");
		if (dbpediaEntriesContainedinFile == null)
			dbpediaEntriesContainedinFile = new Vector<String>();
		try {

			BufferedReader reader;

			reader = new BufferedReader(new FileReader(new File(moviePredicatesFileFreebase)));

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length >= 1) {
					String uri = parts[0];
					if (!dbpediaEntriesContainedinFile.contains(uri))
						dbpediaEntriesContainedinFile.add(uri);
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isContainedInMoviePredicates(String movieURI) {
		if (dbpediaEntriesContainedinFile != null)
			if (dbpediaEntriesContainedinFile.contains(movieURI))
				return true;
		return false;
	}

	/*
	 * 
	 * SELECT DISTINCT ?p ?o { <http://dbpedia.org/resource/Toy_Story> ?p ?o. filter (?p !=
	 * <http://www.w3.org/2000/01/rdf-schema#comment> && ?p != <http://www.w3.org/2000/01/rdf-schema#label> && ?p !=
	 * <http://dbpedia.org/ontology/abstract>) }
	 */
	public static Vector<String> getFeaturesOfMovie(String movieURL) {
		myRepository = new HTTPRepository(SESAME_SERVER, REPOSITORY_ID);
		RepositoryConnection con;
		try {
			con = myRepository.getConnection();

		Vector<String> v = new Vector<String>();

		String sparqlQueryString1 = "SELECT DISTINCT ?p ?o {" + " <" + movieURL + "> ?p ?o." + "}";

		System.out.println(sparqlQueryString1);
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQueryString1);
		TupleQueryResult result = tupleQuery.evaluate();


		while (result.hasNext()) {
			BindingSet set = result.next();
			String p = set.getValue("p").stringValue();
			String o = set.getValue("o").stringValue();

			v.add(p + "\t" + o + ".\n");

		}

		return v;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
