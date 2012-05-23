package Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;


public class Test {

	public static void main(String[] args) throws IOException, TasteException {
		//DataModel model = new FileDataModel(new File("files/user_ratedmovies-timestamps.dat"), false, FileDataModel.DEFAULT_MIN_RELOAD_INTERVAL_MS);
		Freebase freebase = Freebase.getFreebase();
		HashMap<Long, String> id2freebase = getFreebaseIds("files/ids2freebase.dat");
		HashMap<String, Long> imdb2id = readMovies("files/movies.dat");
		
		BufferedReader reader = new BufferedReader(new FileReader(new File("imdbtop250.txt")));
		String line = reader.readLine();
		while (line != null) {
			String [] parts = line.split("\\t");
			
			Long id = imdb2id.get(parts[1]);
			if (id != null) {
				// int numberOfRatings = model.getPreferencesForItem(id).length();
				
				String freebaseId = id2freebase.get(id);
				String originalid = freebaseId;
				String dbpediaId = null;
				if (freebaseId != null) {
					freebaseId = freebaseId.replaceAll("http://rdf.freebase.com/ns/", "");
					JSON query = JSON.o("id", "/" + freebaseId.replaceAll("\\.", "/"), "key",
							JSON.a(JSON.o("namespace", "/wikipedia/en", "value", null, "optional", true)));
					JSON js = freebase.mqlread(query);
					js = js.get("result");
					
					
					if (js != null) {
						if (!js.get("key").array().isEmpty()) {
										dbpediaId = getDBPediaLink(js.get("key").get(0).get("value").string());
						}
					}
					
				}
				/**
				if (numberOfRatings >=800) {
					System.out.println(originalid + "\t" + dbpediaId);
				}
				*/
				
			}
			line = reader.readLine();
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
		
		out = out.replaceAll("0028", "%28");
		out = out.replaceAll("0029", "%29");
		return "http://dbpedia.org/resource/" + out;			
	}
	
	public static HashMap<String, Long> readMovies(String fileName) throws IOException {
		HashMap<String, Long> map = new HashMap<String, Long>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		reader.readLine();
		String line = reader.readLine();
		
		while (line != null) {
			String [] parts = line.split("\t");
			Long id = Long.parseLong(parts[0]);
			String title = parts[2];
			map.put("tt" + title, id);
			
			line = reader.readLine();
		}
		return map;
	}
	
	public static HashMap<Long, String> getFreebaseIds(String fileName) throws IOException {
		HashMap<Long, String> map = new HashMap<Long, String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String line = reader.readLine();
		
		while (line != null) {
			String [] parts = line.split("\t");
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
