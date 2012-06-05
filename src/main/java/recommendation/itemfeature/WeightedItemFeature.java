package recommendation.itemfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import recommendation.ItemFeatureMatrix;

public class WeightedItemFeature extends ItemFeature {
	// file feature Scores
	public static String dbpediaFeatureScoresFile = "files/dbpediafeaturescores.dat";

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

	private HashMap<String, Vector<FeatureScore>> hm_featureScore = null;

	/**
	 * initializes local Hashmap movieURI -> FeatureSore (feature , score) from
	 * the file
	 * 
	 * @param predFilter, if null all predicates are taken, otherwise only the triples with
	 * the predicates that are in the predicateFilter are taken
	 */
	public void initializeFeatureScoresHasmap(Vector<String> predFilter) {
		this.hm_featureScore = new HashMap<String, Vector<FeatureScore>>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(
					WeightedItemFeature.dbpediaFeatureScoresFile)));
			if (reader != null) {
				String line = reader.readLine();
				while (line != null) {
					String[] parts = line.split("\t");
					if (parts.length == 4) {
						if (predFilter == null || predFilter.contains(parts[2])) {
							// score movie pred obj
							double score = Double.parseDouble(parts[0]);
							String movieURI = parts[1];
							String pred = parts[2];
							String obj = parts[3];
							FeatureScore f = new FeatureScore();
							f.setFeature(pred + "::" + obj);
							f.setScore(score);
							if (!this.hm_featureScore.containsKey(movieURI)) {
								this.hm_featureScore
										.put(movieURI,
												new Vector<WeightedItemFeature.FeatureScore>());
							}
							Vector<WeightedItemFeature.FeatureScore> v = this.hm_featureScore
									.get(movieURI);
							v.add(f);
						}
						else
							System.out.println("filter out: " + parts[2]);
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
		System.out.println(movieURI);
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
				System.out.println("Weighted Item Feature " + feature
						+ " are the same");
				return fs.getScore();
			}
		}
		System.out.println("ACHUTUNG WeightedItemFeature no equal feature");
		return 0;
	}

}
