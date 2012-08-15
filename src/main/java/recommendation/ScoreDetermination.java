package recommendation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recommendation.itemfeature.ItemFeature;
import GUI.RecommenderGUI;
import bean.Rating;

public class ScoreDetermination {

	/**
	 * my own cosine similarity: mahout is very slow when putting the 0 in the file, therefore try my own implementation
	 * 
	 * ACHTUNG implementierung: wenn feature nicht vorkommt in user Profile, dann nicht ignorieren sondern 0 entspricht
	 * wenn ich mahout überall 0 welches nicht vorkommt
	 * 
	 * @param ratingsToEstimate
	 * @param gui
	 * @param idtoURIHashMap
	 * @param scoreTreshhold
	 * @param iF
	 * @param userProfile
	 * @param minSamePredicates
	 * @return HashMap with Score values (MovieID -> Score)
	 */
	public static HashMap<String, Double> calculateMyCosineScores_I(Vector<Rating> ratingsToEstimate,
			RecommenderGUI gui, HashMap<Long, String> idtoURIHashMap, ItemFeature iF,
			HashMap<String, Double> userProfile, int minSamePredicates, int weightedUserProfile, boolean random) {

		HashMap<String, Double> hm_movie_scores = new HashMap<String, Double>();

		for (int i = 0; i < ratingsToEstimate.size(); i++) {
			// compute the dot product
			double sumDotProduct = 0;
			double score = 0;
			String movieURI = idtoURIHashMap.get(ratingsToEstimate.get(i).getMovie_lensID());

			// heute
			// System.out.println(movieURI);

			Vector<String> movieFeatures = iF.getFeatureOfMovie(movieURI);

			if (movieFeatures != null) {
				int sum_same_features = 0;
				// dot product multiply same features
				for (int j = 0; j < movieFeatures.size(); j++) {
					String f = movieFeatures.get(j);
					if (userProfile.containsKey(f)) {
						sum_same_features++;
						// heute
						// System.out.println("Same features: " + f);
						if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED)
							sumDotProduct += userProfile.get(f) * iF.getScoreOf(movieURI, f);
						else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED)
							sumDotProduct += 1 * iF.getScoreOf(movieURI, f);
					}
				}

				if (sum_same_features >= minSamePredicates){
					// compute ||A|| A[1]*A[1] + A[2]*A[2] + .... dann noch wurzel
					double betragA = 0;
					Collection<Double> c = userProfile.values();
					for (Iterator<Double> it = c.iterator(); it.hasNext();) {
						double value = it.next();
						if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED)
							betragA += value * value;
						else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED)
							betragA += 1 * 1;
					}
	
					// compute ||B|| B[1]*B[1] + B[2]*B[2] + .... dann noch wurzel
					double betragB = 0;
					for (int k = 0; k < movieFeatures.size(); k++) {
						String feature = movieFeatures.get(k);
	
						double value = iF.getScoreOf(movieURI, feature);
						betragB += value * value;
					}
	
					if (!random){
						// compute cosine similarity of the parts
						score = sumDotProduct / (Math.sqrt(betragA) * Math.sqrt(betragB));
					} else {
						score = Math.random();
					}
	
					hm_movie_scores.put(movieURI, score);
				}
			}
		}
		return hm_movie_scores;
	}

	/**
	 * my own cosine similarity II: another version
	 * 
	 * ACHTUNG implementierung: entspricht dass wenn feature in user profile nicht vorkommt, dann weiss man nicht ob er
	 * es mag oder nicht. daher wird bei movie ausummieren unter dem bruch dann nicht berücksichtigt
	 * 
	 * @param ratingsToEstimate
	 * @param gui
	 * @param idtoURIHashMap
	 * @param scoreTreshhold
	 * @param iF
	 * @param userProfile
	 * @param minSamePredicates
	 * @return
	 */
	public static HashMap<String, Double> calculateMyCosineScores_II(Vector<Rating> ratingsToEstimate,
			RecommenderGUI gui, HashMap<Long, String> idtoURIHashMap, ItemFeature iF,
			HashMap<String, Double> userProfile, int minSamePredicates, int weightedUserProfile, boolean random) {

		HashMap<String, Double> hm_movie_scores = new HashMap<String, Double>();

		for (int i = 0; i < ratingsToEstimate.size(); i++) {
			// compute the dot product
			double sumDotProduct = 0;
			double score = 0;
			String movieURI = idtoURIHashMap.get(ratingsToEstimate.get(i).getMovie_lensID());

			// heute
			// System.out.println("\n" + movieURI);

			Vector<String> movieFeatures = iF.getFeatureOfMovie(movieURI);

			if (movieFeatures != null) {
				int sum_same_features = 0;
				// dot product multiply same features
				for (int j = 0; j < movieFeatures.size(); j++) {
					String f = movieFeatures.get(j);
					if (userProfile.containsKey(f)) {
						sum_same_features++;
						// heute
						// System.out.println("Same features: " + f);

						if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED)
							sumDotProduct += userProfile.get(f) * iF.getScoreOf(movieURI, f);
						else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED)
							sumDotProduct += 1 * iF.getScoreOf(movieURI, f);
					}
				}

				if (sum_same_features >= minSamePredicates){
					
					// compute ||A|| A[1]*A[1] + A[2]*A[2] + .... dann noch wurzel
					double betragA = 0;
					Collection<Double> c = userProfile.values();
					for (Iterator<Double> it = c.iterator(); it.hasNext();) {
						double value = it.next();
						if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED)
							betragA += value * value;
						else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED)
							betragA += 1 * 1;
					}
	
					// compute ||B|| B[1]*B[1] + B[2]*B[2] + .... dann noch wurzel
					double betragB = 0;
					for (int k = 0; k < movieFeatures.size(); k++) {
						String feature = movieFeatures.get(k);
	
						// wenn 1 in movie, dann betrachte ich nur falls auch in profile vorkommt
						if (userProfile.containsKey(feature)) {
							double value = iF.getScoreOf(movieURI, feature);
							betragB += value * value;
						}
					}
	
					if (!random){
						score = sumDotProduct / (Math.sqrt(betragA) * Math.sqrt(betragB));
					} else {
						score = Math.random();
					}
					
					hm_movie_scores.put(movieURI, score);
				}

			}
		}
		return hm_movie_scores;
	}

	/**
	 * calculcate mahout uncentered cosine similarity
	 * 
	 * @param ratingsToEstimate
	 * @param gui
	 * @param idtoURIHashMap
	 * @param scoreTreshhold
	 * @param iF
	 * @param userProfile
	 * @param minSamePredicates
	 * @return
	 */
	public static HashMap<String, Double> calculateMahoutUncenteredCosineSimReturn(Vector<Rating> ratingsToEstimate,
			RecommenderGUI gui, HashMap<Long, String> idtoURIHashMap, ItemFeature iF,
			HashMap<String, Double> userProfile, int minSamePredicates, boolean random) {

		HashMap<String, Double> hm_movie_scores = new HashMap<String, Double>();

		DataModel model;
		try {
			File f = new File("temp/file.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new UncenteredCosineSimilarity(model);
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				String movieURI = idtoURIHashMap.get(ratingsToEstimate.get(i).getMovie_lensID());
				Vector<String> movieFeatures = iF.getFeatureOfMovie(movieURI);

				if (movieFeatures != null) {
					int sum_same_features = 0;
					for (int j = 0; j < movieFeatures.size(); j++) {
						String feat = movieFeatures.get(j);
						if (userProfile.containsKey(feat)) {
							sum_same_features++;
						}
					}
					
					if (sum_same_features >= minSamePredicates){
						try {
							
							double score;
							if (!random){
								 score = usersim
										.userSimilarity(new Long(ratingsToEstimate.get(i).getMovie_lensID()), 9999999);
							} else {
								score = Math.random();
							}
							
							String movieURI2 = idtoURIHashMap.get(ratingsToEstimate.get(i).getMovie_lensID());
							hm_movie_scores.put(movieURI2, score);
		
						} catch (NoSuchUserException e) {
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hm_movie_scores;
	}

	
	/**
	 * my own cosine similarity II: another version
	 * 
	 * ACHTUNG implementierung: entspricht dass wenn feature in user profile nicht vorkommt, dann weiss man nicht ob er
	 * es mag oder nicht. daher wird bei movie ausummieren unter dem bruch dann nicht berücksichtigt
	 * 
	 * @param ratingsToEstimate
	 * @param gui
	 * @param idtoURIHashMap
	 * @param scoreTreshhold
	 * @param iF
	 * @param userProfile
	 * @param minSamePredicates
	 * @return
	 */
	public static HashMap<String, Double> calculateMyCosineScores_III(Vector<Rating> ratingsToEstimate,
			RecommenderGUI gui, HashMap<Long, String> idtoURIHashMap, ItemFeature iF,
			HashMap<String, Double> userProfile, int minSamePredicates, int weightedUserProfile, boolean random) {

		HashMap<String, Double> hm_movie_scores = new HashMap<String, Double>();

		for (int i = 0; i < ratingsToEstimate.size(); i++) {
			// compute the dot product
			double sumDotProduct = 0;
			double score = 0;
			String movieURI = idtoURIHashMap.get(ratingsToEstimate.get(i).getMovie_lensID());

			// heute
			// System.out.println("\n" + movieURI);

			Vector<String> movieFeatures = iF.getFeatureOfMovie(movieURI);

			if (movieFeatures != null) {
				int sum_same_features = 0;
				// dot product multiply same features
				for (int j = 0; j < movieFeatures.size(); j++) {
					String f = movieFeatures.get(j);
					if (userProfile.containsKey(f)) {
						sum_same_features++;

						if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED)
							sumDotProduct += userProfile.get(f) * iF.getScoreOf(movieURI, f);
						else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED)
							sumDotProduct += 1 * iF.getScoreOf(movieURI, f);
					}
				}

				if (sum_same_features >= minSamePredicates){
					
					// compute ||A|| A[1]*A[1] + A[2]*A[2] + .... dann noch wurzel
					double betragA = 0;
					Set<String> set = userProfile.keySet();
					for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
						String feature = it.next();
						if (movieFeatures.contains(feature)){
							double value = userProfile.get(feature);
							if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED)
								betragA += value * value;
							else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED)
								betragA += 1 * 1;
						}
					}
	
					// compute ||B|| B[1]*B[1] + B[2]*B[2] + .... dann noch wurzel
					double betragB = 0;
					for (int k = 0; k < movieFeatures.size(); k++) {
						String feature = movieFeatures.get(k);
	
						// wenn 1 in movie, dann betrachte ich nur falls auch in profile vorkommt
						if (userProfile.containsKey(feature)) {
							double value = iF.getScoreOf(movieURI, feature);
							betragB += value * value;
						}
					}

					if (!random){
						score = sumDotProduct / (Math.sqrt(betragA) * Math.sqrt(betragB));
					} else {
						score = Math.random();
					}
					
					
					hm_movie_scores.put(movieURI, score);
				}

			}
		}
		return hm_movie_scores;
	}
	
	/*
	
	public static HashMap<String, Double> pearsonCalculationReturn(Vector<Rating> ratingsToEstimate,
			RecommenderGUI gui, HashMap<Long, String> idtoURIHashMap, ItemFeature iF,
			HashMap<String, Double> userProfile, int minSamePredicates, int weightedUserProfile) {

		HashMap<String, Double> hm_movie_scores = new HashMap<String, Double>();

		System.out.println("hier");
		
		for (int i = 0; i < ratingsToEstimate.size(); i++) {

			String movieURI = idtoURIHashMap.get(ratingsToEstimate.get(i).getMovie_lensID());
			System.out.println(movieURI);
			Vector<String> movieFeatures = iF.getFeatureOfMovie(movieURI);

			if (movieFeatures != null) {
				// Summation over all attributes for both objects
				// compute ||A|| A[1] + A[2] + A[3]
				double summation_a = 0;
				double summation_a_squares = 0;
				Set<String> s = userProfile.keySet();
				for (Iterator<String> it = s.iterator(); it.hasNext();) {
					String feat = it.next();
					// if (movieFeatures.contains(feat)){
						System.out.println("zusammen: " + feat);
						double value = userProfile.get(feat);
						if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED) {
							summation_a += value;
							summation_a_squares += value * value;
						} else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED) {
							summation_a += 1;
							summation_a_squares += 1 * 1;
						}
					// }
				}

				// compute B[1] + B[2] + B[3]
				double summation_b = 0;
				double summation_b_squares = 0;
				for (int k = 0; k < movieFeatures.size(); k++) {
					String feature = movieFeatures.get(k);
					//if (userProfile.containsKey(feature)){
						double value = iF.getScoreOf(movieURI, feature);
						System.out.println("hier value" + value);
						summation_b += value;
						summation_b_squares += value * value;
					//}
				}

				// dot product multiply same features
				double sumDotProduct = 0;
				int N = 0;
				for (int j = 0; j < movieFeatures.size(); j++) {
					String f = movieFeatures.get(j);
					if (userProfile.containsKey(f)) {
						if (weightedUserProfile == Mediator.USER_PROFILE_WEIGHTED)
							sumDotProduct += userProfile.get(f) * iF.getScoreOf(movieURI, f);
						else if (weightedUserProfile == Mediator.USER_PROFILE_UNWEIGHTED)
							sumDotProduct += 1 * iF.getScoreOf(movieURI, f);
						N++;
					}
				}

				System.out.println("Dot product: " + sumDotProduct);
				System.out.println("Summation a: " + summation_a);
				System.out.println("Summ a sqared: " + summation_a_squares);
				System.out.println("Summation b: " + summation_b);
				System.out.println("Summation b squared: " + summation_b_squares);
				
				double numerator = sumDotProduct - (summation_a * summation_b / N);
				double denominator = Math.sqrt((summation_a_squares - (summation_a * summation_a / N))
						* (summation_b_squares - (summation_b * summation_b / N)));


				System.out.println(numerator);
				System.out.println(denominator);
				
				if (denominator != 0) {
					double score;
					score = numerator / denominator;

					System.out.println(score);
					
					hm_movie_scores.put(movieURI, score + 1);

				}
			}
		}
		return hm_movie_scores;
	}
*/
}
