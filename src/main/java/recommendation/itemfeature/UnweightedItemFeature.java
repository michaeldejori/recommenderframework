package recommendation.itemfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class UnweightedItemFeature extends ItemFeature {

	private HashMap<String, Vector<String>> hm_movieURI_featurelist = null;

	/**
	 * 
	 * @param source
	 * @param predFilter
	 *            if null all predicates are taken, otherwise only the triples with the predicates that are in the
	 *            predicateFilter are taken
	 */
	public void initializePredicateHashMap(String source, Vector<String> predFilter) {

		System.out.println("guguginitialize Predicate Hashmap: " + source);
		hm_movieURI_featurelist = new HashMap<String, Vector<String>>();
		BufferedReader reader = null;
		try {

			if (source.equals(ItemFeature.FREEBASE_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(UnweightedItemFeature.movieFreebasePredicatesFile)));
			else if (source.equals(ItemFeature.DBPEDIA_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(UnweightedItemFeature.movieDBPediaPredicatesFile)));

			if (reader != null) {
				String line = reader.readLine();

				while (line != null) {

					String[] parts = line.split("\t");
					// <sub> <pred> <ob>
					if (parts.length == 3) {
						String sub = parts[0];
						String pred = parts[1];
						String obj = parts[2].substring(0, parts[2].length());
						if (obj.contains("\"")) {
							obj = obj.replaceFirst("\"", "");
							if (obj.contains("\"")) {
								obj = obj.substring(0, obj.indexOf("\""));
							}
						}
						// cut the dot at the end
						String feature = pred + "::" + obj;

						if (!hm_movieURI_featurelist.containsKey(sub)) {
							hm_movieURI_featurelist.put(sub, new Vector<String>());
						}
						Vector<String> v = hm_movieURI_featurelist.get(sub);
						v.add(feature);
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

	/**
	 * 
	 * @return moviefeatures of all movies
	 */
	public HashMap<String, Vector<String>> getEntireMoviesAndFeature() {
		return this.hm_movieURI_featurelist;
	}

	public double getScoreOf(String uri, String feature) {
		Vector<String> vecStrng = this.hm_movieURI_featurelist.get(uri);
		if (vecStrng != null) {
			for (int i = 0; i < vecStrng.size(); i++) {
				String s = vecStrng.get(i);
				if (s.equals(feature)) {
					return 1;
				}
			}
		}
		return 0;
	}

	public Vector<String> getFeatureOfMovie(String movieURI) {
		return hm_movieURI_featurelist.get(movieURI);
	}

	public Vector<String> getDistinctPredicates() {
		Vector<String> ret = new Vector<String>();
		if (hm_movieURI_featurelist != null) {
			Set<String> set = hm_movieURI_featurelist.keySet();
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String movie = it.next();
				Vector<String> featureVec = hm_movieURI_featurelist.get(movie);
				for (int i = 0; i < featureVec.size(); i++) {
					String feature = featureVec.get(i);
					String predicate = feature.substring(0, feature.indexOf("::"));
					if (!ret.contains(predicate)) {
						ret.add(predicate);
					}
				}

			}
		}
		return ret;
	}

	/**
	 * filterPredicates will read actual hashmap and removi
	 * 
	 * @param predFilter
	 *            Vector<String>
	 */
	public void filterPredicates(Vector<String> predFilter) {
		if (this.hm_movieURI_featurelist != null) {
			Set<String> set = hm_movieURI_featurelist.keySet();
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String movie = it.next();
				Vector<String> featureVec = hm_movieURI_featurelist.get(movie);
				// remove shifts the position, therefore begin from the back
				for (int i = featureVec.size() - 1; i >= 0; i--) {
					String feature = featureVec.get(i);
					String predicate = feature.substring(0, feature.indexOf("::"));
					if (!predFilter.contains(predicate)) {
						featureVec.remove(i);
					}
				}
			}
		}
	}

	public int getCommonPredicates(String movieURI1, String movieURI2) {
		int ret = 0;
		Vector<String> movieFeatures1 = hm_movieURI_featurelist.get(movieURI1);
		Vector<String> movieFeatures2 = hm_movieURI_featurelist.get(movieURI2);
		for (int i = 0; i < movieFeatures1.size(); i++) {
			String feature = movieFeatures1.get(i);
			if (movieFeatures2.contains(feature)) {
				ret++;
			}
		}
		return ret;
	}
}
