package recommendation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.mahout.cf.taste.common.TasteException;
import org.openrdf.repository.Repository;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;


public class LinkMoviesToDBPedia {

	
	public static void main(String[] args) throws IOException, TasteException {
		Freebase freebase = Freebase.getFreebase();
		HashMap<Long, String> id2freebase = getFreebaseIds("files/ids2freebase.dat");
		//HashMap<String, Long> imdb2id = readMovies("files/movies.dat");

		try {
			FileWriter filewriter = new FileWriter("files/ids2dbpedia.dat");
			BufferedWriter bf = new BufferedWriter(filewriter);


			Set<Long> set = id2freebase.keySet();
			int i = 0;
			for (Iterator<Long> iterator = set.iterator(); iterator.hasNext();) {
				Long id = (Long) iterator.next();
				String freebaseId = id2freebase.get(id);
				String dbpediaId = null;
				if (freebaseId != null) {
					freebaseId = freebaseId.replaceAll(
							"http://rdf.freebase.com/ns/", "");
					JSON query = JSON.o("id",
							"/" + freebaseId.replaceAll("\\.", "/"), "key", JSON
									.a(JSON.o("namespace", "/wikipedia/en",
											"value", null, "optional", true)));
					JSON js = freebase.mqlread(query);
					js = js.get("result");

					if (js != null) {
						if (!js.get("key").array().isEmpty()) {
							dbpediaId = getDBPediaLink(js.get("key").get(0)
									.get("value").string());
							bf.write(id + "\t" + dbpediaId);
							bf.newLine();
							System.out.println("Written: " + i + " - " + dbpediaId);
						}
					}
				}
				i++;
			}

			bf.close();
			filewriter.close();
		} catch (IOException e) {
		}
	}

	private static String getDBPediaLink(String source) {
		char[] uri2 = source.toCharArray();
		String out = "";
		for (char c : uri2) {
			byte k = (byte) c;
			if (k != 36) {
				out += c;
			}
		}

		// reference : http://graphemica.com
		
		out = out.replaceAll("0028", "%28");
		out = out.replaceAll("0029", "%29");
		out = out.replaceAll("0027", "%27");
		out = out.replaceAll("0026", "%26");
		out = out.replaceAll("0021", "%21");
		out = out.replaceAll("002C", ",");
		out = out.replaceAll("002E", ".");
		out = out.replaceAll("00E9", "%C3%A9");
		out = out.replaceAll("00EA", "%C3%AA");
		out = out.replaceAll("00E0", "%C3%A0");
		out = out.replaceAll("003F", "%3F");
		out = out.replaceAll("00A2", "%C2%A2");
		out = out.replaceAll("002F", "/");
		out = out.replaceAll("00E7", "%C3%A7");
		out = out.replaceAll("00EC", "%C3%AC");
		out = out.replaceAll("00E8", "%C3%A8");
		out = out.replaceAll("002B", "%2B");
		out = out.replaceAll("00BD", "%C2%BD");	
		out = out.replaceAll("00E6", "%C3%A6");
		out = out.replaceAll("00F8", "%C3%B8");
		out = out.replaceAll("002A", "%2A");
		out = out.replaceAll("00F3", "%C3%B3");
		out = out.replaceAll("00A1", "%C2%A1");
		out = out.replaceAll("00ED", "%C3%AD");
		out = out.replaceAll("00EE", "%C3%AE");
		out = out.replaceAll("00F4", "%C3%B4");
		out = out.replaceAll("0024", "%24");
		out = out.replaceAll("00F1", "%C3%B1");
		out = out.replaceAll("00E1", "%C3%A1");
		out = out.replaceAll("0040", "%40");
		out = out.replaceAll("00F6", "%C3%B6");
		out = out.replaceAll("00C0", "%C3%80");
		out = out.replaceAll("00C0", "%C3%80");
		out = out.replaceAll("00E4", "%C3%A4");
		out = out.replaceAll("00FC", "%C3%BC");
		out = out.replaceAll("00E3", "%C3%A3");
		out = out.replaceAll("00C6", "%C3%86");
		out = out.replaceAll("00B0", "%C2%B0");
		out = out.replaceAll("00F2", "%C3%B2");		
		out = out.replaceAll("00C5", "%C3%85");
		out = out.replaceAll("00E5", "%C3%A5");		
		out = out.replaceAll("00CA", "%C3%8A");
		out = out.replaceAll("00EF", "%C3%AF");
		out = out.replaceAll("00C4", "%C3%84");
		out = out.replaceAll("00BF", "%C2%BF");
		out = out.replaceAll("00C2", "%C3%82");
		out = out.replaceAll("00DC", "%C3%9C");
		out = out.replaceAll("0022", "%22");
		
		out = out.replaceAll("00C2", "%C3%82");
		out = out.replaceAll("00C2", "%C3%82");
		
		
		

		

		return "http://dbpedia.org/resource/" + out;
	}

	/**
	public static HashMap<String, Long> readMovies(String fileName)
			throws IOException {
		HashMap<String, Long> map = new HashMap<String, Long>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(
				fileName)));
		reader.readLine();
		String line = reader.readLine();

		while (line != null) {
			String[] parts = line.split("\t");
			Long id = Long.parseLong(parts[0]);
			String title = parts[2];
			map.put("tt" + title, id);

			line = reader.readLine();
		}
		return map;
	}
*/

	public static HashMap<Long, String> getFreebaseIds(String fileName)
			throws IOException {
		HashMap<Long, String> map = new HashMap<Long, String>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(
				fileName)));
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
