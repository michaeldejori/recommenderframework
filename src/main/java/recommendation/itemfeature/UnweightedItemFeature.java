package recommendation.itemfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import recommendation.ItemFeatureMatrix;

public class UnweightedItemFeature extends ItemFeature {
	
	public static final String DBPEDIA_UNWEIGHTED = "1";
	public static final String DBPEDIA_WEIGHTED = "3";
	public static final String FREEBASE_UNWEIGHTED = "2";

	private HashMap<String, Vector<String>> hm_movieURI_featurelist = null;

	/**
	 * 
	 * @param source
	 * @param predFilter if null all predicates are taken, otherwise only the triples with
	 * the predicates that are in the predicateFilter are taken
	 */
	public void initializePredicateHashMap(String source, Vector<String> predFilter) {
		hm_movieURI_featurelist = new HashMap<String, Vector<String>>();
		BufferedReader reader = null;
		try {

			if (source.equals(UnweightedItemFeature.FREEBASE_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						UnweightedItemFeature.movieFreebasePredicatesFile)));
			else if (source.equals(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						UnweightedItemFeature.movieDBPediaPredicatesFile)));

			if (reader != null) {
				String line = reader.readLine();

				// index for hashmap and then matrix
				int i = 1;

				while (line != null) {

					String[] parts = line.split("\t");
					// <sub> <pred> <ob>
					if (parts.length == 3) {
						String s = parts[0];
						String p = parts[1];
						String o = parts[2].substring(0, parts[2].length() - 1);
						String newS = s.replace("<", "");
						newS = newS.replace(">", "");
						String newP = p.replace("<", "");
						newP = newP.replace(">", "");
						String newO = o.replace("<", "");
						newO = newO.replace(">", "");
						if (newO.contains("\"")) {
							newO = newO.replaceFirst("\"", "");
							if (newO.contains("\"")) {
								newO = newO
										.substring(0, newO.indexOf("\""));
							}
						}
						
						if (predFilter == null || predFilter.contains(newP)){
							
							// cut the dot at the end
							String feature = newP + "::" + newO;
	
							if (!hm_movieURI_featurelist.containsKey(newS)){
								hm_movieURI_featurelist.put(newS, new Vector<String>());
							}
							Vector<String> v = hm_movieURI_featurelist.get(newS);
							v.add(feature);
						} else
							System.out.println("filter out: " + newP);
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
	
	public HashMap<String, Vector<String>> getEntireMoviesAndFeature(){
		return this.hm_movieURI_featurelist;
	}
	
	
	
	public double getScoreOf(String uri, String feature) {
		Vector<String> vecStrng = this.hm_movieURI_featurelist.get(uri);
		if (vecStrng != null){
			for (int i = 0; i < vecStrng.size(); i++){
				String s = vecStrng.get(i);
				if (s.equals(feature)){
					return 1;
				}
			}
		}
		return 0;
	}
	
	public Vector<String> getFeatureOfMovie(String movieURI){
		return hm_movieURI_featurelist.get(movieURI);
	}
}
