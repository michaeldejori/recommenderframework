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

public class FromRepositoryToFile {
	// repository
	public static final String SESAME_SERVER = "http://lokino.sti2.at:8080/openrdf-sesame";
	public static final String REPOSITORY_ID = "fb";
	public static String ids2freebaseFile = "files/ids2freebase.dat";
	public static Vector<String> dbpediaEntriesContainedinFile;
	
	public static Repository repository;

	public static void main(String[] args) {
		readMovieFeatureFromRepo();
	}

	public static void initializeVectorOfExistingFeatures() {
		System.out.println("initializeVectorOfExistingFeatures");
		if (FromRepositoryToFile.dbpediaEntriesContainedinFile == null)
			FromRepositoryToFile.dbpediaEntriesContainedinFile = new Vector<String>();
		try {

			BufferedReader reader;

			reader = new BufferedReader(new FileReader(new File("files/moviePredicatesFB.dat")));

			String line = reader.readLine();

			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length >= 1) {
					String uri = parts[0];
					if (!FromRepositoryToFile.dbpediaEntriesContainedinFile.contains(uri))
						FromRepositoryToFile.dbpediaEntriesContainedinFile.add(uri);
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("contained: " + FromRepositoryToFile.dbpediaEntriesContainedinFile.size());
	}
	
	
	private static void readMovieFeatureFromRepo() {
		initializeVectorOfExistingFeatures();
		FromRepositoryToFile.repository = new HTTPRepository(FromRepositoryToFile.SESAME_SERVER,
				FromRepositoryToFile.REPOSITORY_ID);
		HashMap<Long, String> hmids2freebase = null;
		try {
			hmids2freebase = FromRepositoryToFile.getFreebaseIdsFromFile(FromRepositoryToFile.ids2freebaseFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Collection<String> col = hmids2freebase.values();

		System.out.println("Creating file:");
		try {
			FileWriter filewriter = new FileWriter("files/moviePredicatesFB.dat", true);
			BufferedWriter bf = new BufferedWriter(filewriter);

			int j = 1;

			for (Iterator<String> iterator = col.iterator(); iterator.hasNext();) {
				String movieURI = (String) iterator.next();
				if (!FromRepositoryToFile.dbpediaEntriesContainedinFile.contains(movieURI)){
					System.out.println("Processing: " + movieURI + " (" + j + ")");
					Vector<String> v = FromRepositoryToFile.retrievePredicatesOfMovie(movieURI);
					for (int i = 0; i < v.size(); i++) {
						bf.write(v.get(i));
						bf.newLine();
						bf.flush();
					}
				} else {
					System.out.println(movieURI + " already contained");
				}
				j++;
			}

			bf.close();
			filewriter.close();
		} catch (IOException e) {
		}
	}

	public static Vector<String> retrievePredicatesOfMovie(String movieURI) {

		Vector<String> v = null;
		RepositoryConnection con;
		try {
			con = FromRepositoryToFile.repository.getConnection();
			String queryString = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
					+ "PREFIX fb:<http://rdf.freebase.com/ns/> " + "Select distinct ?pred ?ob " + "where {<" + movieURI
					+ "> ?pred ?ob." + "<" + movieURI + "> rdf:type fb:film.film.}";

			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			v = new Vector<String>();

			while (result.hasNext()) {
				BindingSet set = result.next();
				String pred = set.getValue("pred").stringValue();
				String ob = set.getValue("ob").stringValue();
				v.add(movieURI + "\t" + pred + "\t" + ob);
			}
			result.close();
			con.close();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		System.out.println("Finished processing: " + movieURI);
		return v;
	}

	public static HashMap<Long, String> getFreebaseIdsFromFile(String fileName) throws IOException {
		HashMap<Long, String> map = new HashMap<Long, String>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		// reader.readLine();
		String line = reader.readLine();

		while (line != null) {
			String[] parts = line.split("\t");
			if (parts.length == 2) {
				Long id = Long.parseLong(parts[0]);
				String freebase = parts[1];
				map.put(id, freebase);
			}
			line = reader.readLine();
		}
		return map;
	}
	

	
}
