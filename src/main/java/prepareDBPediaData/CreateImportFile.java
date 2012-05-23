package prepareDBPediaData;

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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class CreateImportFile {

	public static String dbpediaIdsFile = "files/ids2dbpedia.dat"; // files
																	// dbpedia
																	// ids
	// file moviePredicates, movieURI predicate object
	public static String moviePredicatesFileDBPedia = "files/moviePredicatesDBPedia.dat";
	private Vector<String> dbpediaEntriesContainedinFile = null;
	private HashMap<Long, String> idtoDBPediaHashMap = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CreateImportFile cIF = new CreateImportFile();
		cIF.initializeidstoDBPediaHashmap();
		cIF.createDBPediaMovieFeaturesFile();

	}

	/**
	 * Hashmap movie IDs to dbpedia IDs is initialized
	 */
	public void initializeidstoDBPediaHashmap() {
		try {
			this.idtoDBPediaHashMap = new HashMap<Long, String>();

			BufferedReader reader;

			reader = new BufferedReader(new FileReader(new File(
					this.dbpediaIdsFile)));

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length == 2) {
					// System.out.println(parts[0] + " " + parts[1]);
					Long id = Long.parseLong(parts[0]);
					String freebase = parts[1];
					this.idtoDBPediaHashMap.put(id, freebase);
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

	public void createDBPediaMovieFeaturesFile() {
		System.out.println("Creating file:");
		initializeVectorOfExistingFeatures();
		try {
			FileWriter filewriter = new FileWriter(
					this.moviePredicatesFileDBPedia, true);
			BufferedWriter bf = new BufferedWriter(filewriter);

			// bf.write("movieURI\tpredicate\tobject");
			// bf.newLine();

			int j = 1;
			int total = idtoDBPediaHashMap.size();
			Collection<String> col = this.idtoDBPediaHashMap.values();

			for (Iterator<String> iterator = col.iterator(); iterator.hasNext();) {
				String movieURI = (String) iterator.next();

				if (!isContainedInMoviePredicates("<" + movieURI + ">")) {
					System.out.println("Processing: " + movieURI);
					System.out.println("Fortschritt: " + j + "/" + total);
					Vector<String> v = getFeaturesOfMovie(movieURI);
					if (v == null) {
						bf.write("<" + movieURI
								+ ">\t<no:entry>\t<no:entry>.\n");
					} else {
						for (int i = 0; i < v.size(); i++) {
							bf.write("<" + movieURI + ">\t" + v.get(i));
						}
					}
				} else {
					System.out.println("movie " + movieURI
							+ " already contained");
				}

				j++;
			}

			bf.close();
			filewriter.close();
		} catch (IOException e) {
		}
	}

	private void initializeVectorOfExistingFeatures() {
		System.out.println("initializeVectorOfExistingFeatures");
		if (this.dbpediaEntriesContainedinFile == null)
			this.dbpediaEntriesContainedinFile = new Vector<String>();
		try {

			BufferedReader reader;

			reader = new BufferedReader(new FileReader(new File(
					this.moviePredicatesFileDBPedia)));

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length >= 1) {
					String uri = parts[0];
					if (!this.dbpediaEntriesContainedinFile.contains(uri))
						this.dbpediaEntriesContainedinFile.add(uri);
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean isContainedInMoviePredicates(String movieURI) {
		if (this.dbpediaEntriesContainedinFile != null)
			if (this.dbpediaEntriesContainedinFile.contains(movieURI))
				return true;
		return false;
	}

	/*
	 * 
	 * SELECT DISTINCT ?p ?o { <http://dbpedia.org/resource/Toy_Story> ?p ?o.
	 * filter (?p != <http://www.w3.org/2000/01/rdf-schema#comment> && ?p !=
	 * <http://www.w3.org/2000/01/rdf-schema#label> && ?p !=
	 * <http://dbpedia.org/ontology/abstract>) }
	 */
	public Vector<String> getFeaturesOfMovie(String movieURL) {
		Vector<String> v = new Vector<String>();

		String sparqlQueryString1 = "SELECT DISTINCT  ?p ?o {"
				+ " <"
				+ movieURL
				+ "> ?p ?o."
				+ "filter (?p != <http://www.w3.org/2000/01/rdf-schema#comment> && "
				+ "?p != <http://www.w3.org/2000/01/rdf-schema#label> && "
				+ "?p != <http://dbpedia.org/ontology/abstract>  && "
				+ "?p != <http://dbpedia.org/property/quote>)" + "}";

		Query query = QueryFactory.create(sparqlQueryString1);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				"http://dbpedia.org/sparql", query);

		ResultSet results = qexec.execSelect();

		if (!results.hasNext()) {
			System.out.println("Im sorry, i have no entry: " + movieURL);
			return null;
		} else {
			do {
				// ACHTUNG WENN http://dbpedia.org/ontology/wikiPageRedirects
				// dann dort nachschauen
				QuerySolution qs = results.next();
				// System.out.println("vorher" + qs.get("p") + " <-> " +
				// qs.get("o"));
				if ((qs.get("p").toString())
						.equals("http://dbpedia.org/ontology/wikiPageRedirects")) {
					return this.getFeaturesOfMovie(qs.get("o").toString());
				} else {
					RDFNode p = qs.get("p");
					RDFNode o = qs.get("o");
					String pstring = "";
					String ostring = "";
					if (p.isResource()) {
						pstring = "<" + p.toString() + ">";
					}
					if (o.isLiteral()) {
						Literal l = o.asLiteral();
						if (l.getDatatypeURI() != null) {
							ostring = "\"" + l.getLexicalForm() + "\"^^<"
									+ l.getDatatypeURI() + ">";
						} else {
							ostring = "\"" + l.toString().replace("\"", "") + "\"";
						}
						// ostring = l.toString();
					} else if (o.isResource()) {
						ostring = "<" + o.toString() + ">";
					}
					v.add(pstring + "\t" + ostring + ".\n");
				}

			} while (results.hasNext());
		}
		qexec.close();

		return v;
	}

}
