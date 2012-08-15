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

public class CombinedApproach1 extends ItemFeature {
	// file feature Scores
	public static String dbpediaFeatureScoresFile = "files/dbpediafeaturescores.dat";
	public static String freebaseFeatureScoresFile = "files/freebasefeaturescores.dat";

	private HashMap<String, Vector<FeatureScore>> hm_featureScore = null;

	private class FeatureScore {
		private String feature;
		private double score;

		public String getFeature() {
			return feature;
		}

		public void setFeature(String feature) {
			this.feature = feature;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}
	}

	/**
	 * initializes local Hashmap movieURI -> FeatureSore (feature , score) from the file
	 * 
	 * @param predFilter
	 *            , if null all predicates are taken, otherwise only the triples with the predicates that are in the
	 *            predicateFilter are taken
	 */
	public void initializeFeatureScoresHasmap(String source, Vector<String> predFilter) {

		HashMap<String, HashMap<String, Double>> hashMap_feature_weightes;

		hashMap_feature_weightes = new HashMap<String, HashMap<String, Double>>();

		// initializes unweighted (all) attributes
		initializeUnweightedPredicates(source, predFilter, hashMap_feature_weightes);

		// overwrite the attributes with the weightes
		initializeWeightedPredicates(source, predFilter, hashMap_feature_weightes);

		transformHashMapToVector(hashMap_feature_weightes);

	}

	private void transformHashMapToVector(HashMap<String, HashMap<String, Double>> hashMap_feature_weightes) {
		this.hm_featureScore = new HashMap<String, Vector<FeatureScore>>();
		if (hashMap_feature_weightes != null) {
			
			// run through movies in hashmap
			
			Iterator<String> it = hashMap_feature_weightes.keySet().iterator();
			while (it.hasNext()) {
				String movie = it.next();
				HashMap<String, Double> hm = hashMap_feature_weightes.get(movie);
				
				// run through feature of movie
				Iterator<String> it2 = hm.keySet().iterator();
				while (it2.hasNext()){
					String feature = it2.next();
					double score = hm.get(feature);
					
					if (!hm_featureScore.containsKey(movie)){
						hm_featureScore.put(movie, new Vector<FeatureScore>());
					}
					
					FeatureScore fs = new FeatureScore();
					fs.setFeature(feature);
					fs.setScore(score);
					Vector<FeatureScore> vec = hm_featureScore.get(movie);
					vec.add(fs);
				}
			}
		}

	}

	private void initializeWeightedPredicates(String source, Vector<String> predFilter,
			HashMap<String, HashMap<String, Double>> hm_featureScore2) {

		BufferedReader reader = null;
		try {

			if (source.equals(ItemFeature.FREEBASE_COMB_WEI_UNW)) {
				reader = new BufferedReader(new FileReader(new File(CombinedApproach1.freebaseFeatureScoresFile)));
			} else if (source.equals(ItemFeature.DBPEDIA_COMB_WEI_UNW)) {
				reader = new BufferedReader(new FileReader(new File(CombinedApproach1.dbpediaFeatureScoresFile)));
			}
			if (reader != null) {
				String line = reader.readLine();
				while (line != null) {
					String[] parts = line.split("\t");
					if (parts.length == 4) {
						// score movie pred obj
						double score = Double.parseDouble(parts[0]);
						String movieURI = parts[1];
						String pred = parts[2];
						String obj = parts[3];
						String feature = pred + "::" + obj;
						if (!hm_featureScore2.containsKey(movieURI)) {
							hm_featureScore2.put(movieURI, new HashMap<String, Double>());
						}
						HashMap<String, Double> hm = hm_featureScore2.get(movieURI);
						hm.put(feature, score);
					}
					line = reader.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeUnweightedPredicates(String source, Vector<String> predFilter,
			HashMap<String, HashMap<String, Double>> hm_feature_score) {
		BufferedReader reader = null;
		try {
			if (source.equals(ItemFeature.FREEBASE_COMB_WEI_UNW))
				reader = new BufferedReader(new FileReader(new File(UnweightedItemFeature.movieFreebasePredicatesFile)));
			else if (source.equals(ItemFeature.DBPEDIA_COMB_WEI_UNW))
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

						if (!hm_feature_score.containsKey(sub)) {
							hm_feature_score.put(sub, new HashMap<String, Double>());
						}
						HashMap<String, Double> hm = hm_feature_score.get(sub);

						hm.put(feature, new Double(1));

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
	 * @param movieURI
	 *            example http://dbpedia.org/resource/Cutthroat_Island
	 * @return vector of feature scores of the movieURI
	 */
	public Vector<FeatureScore> getFeatureScoresOfMovie(String movieURI) {
		return this.hm_featureScore.get(movieURI);
	}

	public Vector<String> getFeatureOfMovie(String movieURI) {
		Vector<FeatureScore> v = hm_featureScore.get(movieURI);
		// bei einigen hat das feature score creation nicht funktioniert, lasse
		// die weg
		if (v != null) {
			Vector<String> vecString = new Vector<String>();
			for (int i = 0; i < v.size(); i++) {
				vecString.add(v.get(i).getFeature());
			}
			return vecString;
		}
		return null;
	}

	public double getScoreOf(String uri, String feature) {
		Vector<FeatureScore> v = this.hm_featureScore.get(uri);
		for (int i = 0; i < v.size(); i++) {
			FeatureScore fs = v.get(i);
			if (fs.getFeature().equals(feature)) {
				return fs.getScore();
			}
		}
		return 0;
	}

	public int getCommonPredicates(String movieURI1, Set<String> set) {
		int ret = 0;
		Vector<FeatureScore> movieFeatures1 = hm_featureScore.get(movieURI1);
		if (movieFeatures1 != null) {
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String feature = it.next();
				for (int j = 0; j < movieFeatures1.size(); j++) {
					FeatureScore f = movieFeatures1.get(j);
					if (feature.equals(f.getFeature())) {
						ret++;
					}
				}
			}
			return ret;
		} else
			return 0;
	}

	public Vector<String> getDistinctPredicates() {
		Vector<String> ret = new Vector<String>();
		if (hm_featureScore != null) {
			Set<String> set = hm_featureScore.keySet();
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String movie = it.next();
				Vector<FeatureScore> featureVec = hm_featureScore.get(movie);
				for (int i = 0; i < featureVec.size(); i++) {
					FeatureScore fs = featureVec.get(i);
					String feature = fs.getFeature();
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
		if (hm_featureScore != null) {
			System.out.println("1");
			Set<String> set = hm_featureScore.keySet();
			System.out.println("hier");
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String movie = it.next();
				Vector<FeatureScore> featureVec = hm_featureScore.get(movie);
				// remove shifts the position, therefore begin from the back
				for (int i = featureVec.size() - 1; i >= 0; i--) {
					FeatureScore fs = featureVec.get(i);
					String feature = fs.getFeature();
					String predicate = feature.substring(0, feature.indexOf("::"));
					if (!predFilter.contains(predicate)) {
						featureVec.remove(i);
					}
				}
			}
		}
	}

}
