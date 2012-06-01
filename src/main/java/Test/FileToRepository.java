package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

public class FileToRepository {
	// private static final String SESAME_SERVER = "http://localhost:8080/openrdf-sesame";
	private static final String SESAME_SERVER = "http://rdf.sti2.at:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "dbpedia";
	private static Repository myRepository = null;
	
	
	public static final String inputN3File = "files/testfile.dat";

	public static void main(String[] args) {
		myRepository = new HTTPRepository(SESAME_SERVER, REPOSITORY_ID);
		ValueFactory f = myRepository.getValueFactory();
		
		BufferedReader reader;
		try {
			RepositoryConnection con = myRepository.getConnection();
			reader = new BufferedReader(new FileReader(new File(inputN3File)));
			String line = reader.readLine();
			while (line != null) {
				String[] parts = line.split("\t");
				if (parts.length == 3) {
					URI subject = f.createURI(parts[0]);
					URI predicate = f.createURI(parts[1]);
					URI object = f.createURI(parts[2].substring(0, parts[2].length() - 1));
					con.add(subject, predicate, object);
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
