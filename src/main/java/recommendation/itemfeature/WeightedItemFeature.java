package recommendation.itemfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class WeightedItemFeature extends ItemFeature {
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
		this.hm_featureScore = new HashMap<String, Vector<FeatureScore>>();
		BufferedReader reader = null;
		try {
			if (source.equals(ItemFeature.FREEBASE_WEIGHTED)) {
				reader = new BufferedReader(new FileReader(new File(WeightedItemFeature.freebaseFeatureScoresFile)));
			} else if (source.equals(ItemFeature.DBPEDIA_WEIGHTED)) {
				reader = new BufferedReader(new FileReader(new File(WeightedItemFeature.dbpediaFeatureScoresFile)));
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
						FeatureScore f = new FeatureScore();
						f.setFeature(pred + "::" + obj);
						f.setScore(score);
						if (!this.hm_featureScore.containsKey(movieURI)) {
							this.hm_featureScore.put(movieURI, new Vector<WeightedItemFeature.FeatureScore>());
						}
						Vector<WeightedItemFeature.FeatureScore> v = this.hm_featureScore.get(movieURI);
						v.add(f);

					}
					line = reader.readLine();
				}
			}
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
	 * @param predFilter Vector<String>
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
				for (int i = featureVec.size() - 1; i >= 0 ; i--) {
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
