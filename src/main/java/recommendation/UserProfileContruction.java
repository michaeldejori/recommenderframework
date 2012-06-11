package recommendation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import recommendation.itemfeature.UnweightedItemFeature;
import recommendation.itemfeature.WeightedItemFeature;
import bean.Rating;
import bean.User;

public class UserProfileContruction {

	public static final double APPROACH_2_POS_TRESHHOLD = 3.5;
	
	/**
	 * Approach 3 - Generating User Profiles from Ratings.To build the user pro
	 * le [3] proposes the following: Let I be a set if items (N items) rated by
	 * a user. Then the rating values are divided by 5 to get the scores between
	 * 0 and 1. To measure the relevance of the different movie features they
	 * sum up the weights of movies wm in which these features appear according
	 * to the formula
	 * 
	 * 
	 * @param backData
	 *            Data
	 * @param ifM
	 *            ItemFeature Matrix
	 * @param idtoURIHashMap
	 * @return
	 */
	public static HashMap<String, Double> approach3(Vector<Rating> backData,
			Object ifM, int weighted, HashMap<String, String> idtoURIHashMap) {
		// hashmap that Collects the user Profile Data
		HashMap<String, Double> hm_feature_value = new HashMap<String, Double>();
		// if backGround Data is set
		if (backData != null) {
			// run through the background Data
			for (int i = 0; i < backData.size(); i++) {
				Rating r = backData.get(i);
				Vector<String> featureVector = null;
				if (weighted == Mediator.WEIGHTED) {
					featureVector = ((WeightedItemFeature) ifM)
							.getFeatureOfMovie(idtoURIHashMap.get(r
									.getMovie_lensID()));
				} else if (weighted == Mediator.UNWEIGHTED) {
					featureVector = ((UnweightedItemFeature) ifM)
							.getFeatureOfMovie(idtoURIHashMap.get(r
									.getMovie_lensID()));
				}
				// feature vecot kann null sein, wenn feature score für diesen
				// Film nicht gefunden werden konnten
				if (featureVector != null) {
					for (int j = 0; j < featureVector.size(); j++) {
						// initialize feature in hashmap if not exists
						if (!hm_feature_value.containsKey(featureVector.get(j))) {
							hm_feature_value.put(featureVector.get(j)
									.toString(), (double) 0);
						}
						// sum up the feature values
						double a = hm_feature_value.get(featureVector.get(j)
								.toString());
						a += r.getRating() / 5;
						hm_feature_value.put(featureVector.get(j).toString(),
								new Double(a));
					}
				}
			}
			// divide all by N rated elements
			Set<String> keySet = hm_feature_value.keySet();
			for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
				String f = it.next();
				hm_feature_value.put(f, hm_feature_value.get(f) / 5);
			}
		}
		return hm_feature_value;
	}

	public static HashMap<String, Double> approach2(Vector<Rating> backData,
			Object ifM, int weighted, HashMap<String, String> idtoURIHashMap) {
		// hashmap that Collects the user Profile Data
		HashMap<String, Double> hm_feature_value = new HashMap<String, Double>();
		// if backGround Data is set
		if (backData != null) {
			// run through the background Data
			for (int i = 0; i < backData.size(); i++) {
				Rating r = backData.get(i);
				// check if rating is above some positive Treshhold
				if (r.getRating() >= UserProfileContruction.APPROACH_2_POS_TRESHHOLD) {
					Vector<String> featureVector = null;
					if (weighted == Mediator.WEIGHTED) {
						featureVector = ((WeightedItemFeature) ifM)
								.getFeatureOfMovie(idtoURIHashMap.get(r
										.getMovie_lensID()));
					} else if (weighted == Mediator.UNWEIGHTED) {
						featureVector = ((UnweightedItemFeature) ifM)
								.getFeatureOfMovie(idtoURIHashMap.get(r
										.getMovie_lensID()));
					}
					// feature vecot kann null sein, wenn feature score für
					// diesen Film nicht gefunden werden konnten
					if (featureVector != null) {
						for (int j = 0; j < featureVector.size(); j++) {
							// initialize feature in hashmap if not exists
							if (!hm_feature_value.containsKey(featureVector
									.get(j))) {
								hm_feature_value.put(featureVector.get(j)
										.toString(), (double) 0);
							}
							// sum up the feature values
							double a = hm_feature_value.get(featureVector
									.get(j).toString());
							a += r.getRating() / 5;
							hm_feature_value.put(featureVector.get(j)
									.toString(), new Double(a));
						}
					}
				}
			}
			// divide all by N rated elements
			Set<String> keySet = hm_feature_value.keySet();
			for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
				String f = it.next();
				hm_feature_value.put(f, hm_feature_value.get(f) / 5);
			}
		}
		return hm_feature_value;

	}

}
