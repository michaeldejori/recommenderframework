package itemWeightsCreationFB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

public class AddKNNStatements {
	private static final String SESAME_SERVER = "http://lokino.sti2.at:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "fb";
	private static Repository myRepository = null;

	public static void main(String[] args) {
		myRepository = new HTTPRepository(SESAME_SERVER, REPOSITORY_ID);
		ValueFactory f = myRepository.getValueFactory();
		FileReader fr;
		int i = 1;
		int j = 0;
		try {
			fr = new FileReader("files/nearestneighborfreebase.dat");
			BufferedReader br = new BufferedReader(fr);
			
			// write statements as history
			FileWriter filewriter = new FileWriter("files/log_writtenknnStatements", true);
			BufferedWriter bf = new BufferedWriter(filewriter);
			
			RepositoryConnection con = myRepository.getConnection();
			
			String s;
			while ((s = br.readLine()) != null) {
				String[] parts = s.split("\t");
				// <sub> <pred> <ob>
				if (parts.length == 3) {
					String sub = parts[0];
					String pre = parts[1];
					String obj = parts[2];
					URI subject = f.createURI(sub);
					URI predicate = f.createURI(pre);
					URI object = f.createURI(obj);

					con.add(subject, predicate, object);
					System.out.println(subject);
					bf.write(sub + "\t" + pre + "\t" + obj);
					bf.newLine();
				}
			}
			fr.close();
			bf.close();
			con.close();
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
