package recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import recommendation.itemfeature.ItemFeature;
import recommendation.itemfeature.UnweightedItemFeature;
import recommendation.itemfeature.WeightedItemFeature;
import GUI.RecommenderGUI;
import GUI.TextFrame;
import bean.PrecisionRecall;
import bean.Rating;
import bean.User;

public class Mediator {

	public static final boolean USE_MY_OWN_DATA_SET = false;
	
	public static final int COSINE_SIM = 1;
	public static final int MYCOSINE_SIM = 0;
	public static final int LOGLIKELIHOOD_SIM = 2;
	public static final int PEARSON_SIM = 3;

	public static final int PROF_CONS_APP_2 = 2;
	public static final int PROF_CONS_APP_3 = 3;

	public static final int WEIGHTED = 1;
	public static final int UNWEIGHTED = 2;

	public static final boolean USE_MIN_PREDICATES = false;
	public static final int MIN_COMM_PREDICATES = 2;

	public static final double RATING_TRESHHOLD = 4;

	public static final boolean smallUserstoTest = false;

	private ItemFeatureMatrix ifM = null;
	private UserItemMatrix uiM = null;
	private RecommenderGUI gui = null;

	public static String ids2freebaseFile = "files/ids2freebase.dat"; // files
	// freebaseids
	public static String ids2dbpediaFile = "files/ids2dbpedia.dat"; // files

	private HashMap<String, String> idtoURIHashMap = null;

	public Mediator(RecommenderGUI gui) {
		this.gui = gui;
		ifM = new ItemFeatureMatrix(this.gui);
		uiM = new UserItemMatrix();
		uiM.initalizeDataFromFile();
		uiM.removeUserWithRatingsUnderTres(40);
	}

	/**
	 * initializeMovieHashmap(source) initializes the Hasmap, from movie Lens ID
	 * to URI
	 * 
	 * @param source
	 *            data source (dbpedia or freebase)
	 */
	private void initializeMovieHashmap(String source) {
		try {
			this.idtoURIHashMap = new HashMap<String, String>();

			BufferedReader reader = null;

			if (source.equals(ItemFeatureMatrix.FREEBASE_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						ItemFeatureMatrix.ids2freebaseFile)));
			else if (source.equals(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED))
				reader = new BufferedReader(new FileReader(new File(
						ItemFeatureMatrix.ids2dbpediaFile)));
			if (reader != null) {
				String line = reader.readLine();

				while (line != null) {
					String[] parts = line.split("\t");
					if (parts.length == 2) {
						String id = parts[0];
						String uri = parts[1];
						this.idtoURIHashMap.put(id, uri);
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

	public static void main(String[] args) {

	}

	public void initializeDataSource(String string) {
		if (string.equals("dbPedia")) {
			ifM.initialize(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED);
		}

	}

	/**
	 * Small Test Recommendation
	 * 
	 * 
	 * @param ifapproach
	 *            0 unweighted, 1 weighted
	 * @param ufapproach
	 *            0 matrix mult, 1 above treshhold, 2 userprofile from ratings
	 * @param matchApproach
	 *            0 cosine sim, 1 loglikelihood,
	 */
	public void makesmallTestRecommendation(int ifapproach, int ufapproach,
			int matchApproach, String userid) {
		if (Mediator.USE_MY_OWN_DATA_SET){
			uiM.initializeMyTestData();
		}
		if (idtoURIHashMap == null) {
			this.initializeMovieHashmap(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED);
		}
		User user = new User();
		user.setHetrec_id(userid);
		Vector<Rating> ratingsOfUser = uiM.getRatingsOfUser(userid);
		user.setRatings(ratingsOfUser);
		Vector<Rating> backData = user.getBackGroundData(0.75);
		HashMap<String, Double> userProfile = null;
		ItemFeature iF = null;
		if (ifapproach == Mediator.WEIGHTED) {
			// Prepare Weighted Item Feature Set UP
			iF = new WeightedItemFeature();
			System.out.println("Initialize weighted Scores");
			((WeightedItemFeature) iF).initializeFeatureScoresHasmap(gui
					.getSelectedPredicateFilterValues());
			System.out.println("Finished initilize weighted Scores");
		} else if (ifapproach == Mediator.UNWEIGHTED) {
			iF = new UnweightedItemFeature();
			((UnweightedItemFeature) iF).initializePredicateHashMap(
					ItemFeatureMatrix.DBPEDIA_UNWEIGHTED,
					gui.getSelectedPredicateFilterValues());
		}

		if (ufapproach == Mediator.PROF_CONS_APP_3) {
			userProfile = UserProfileContruction.approach3(backData, iF,
					ifapproach, this.idtoURIHashMap);
			printUserProfile(userProfile);
		} else if (ufapproach == Mediator.PROF_CONS_APP_2) {
			userProfile = UserProfileContruction.approach2(backData, iF,
					ifapproach, this.idtoURIHashMap);
			printUserProfile(userProfile);
		}
		if (matchApproach == Mediator.MYCOSINE_SIM) {
			Matching.CosineSimilarity(userProfile, idtoURIHashMap,
					user.getRatingsToEstimate(), iF, this.gui);
		} else if (matchApproach == Mediator.COSINE_SIM) {
			prepareFile(user.getRatingsToEstimate(), userProfile, iF);
			Matching.calculateMahoutUncenteredCosineSim(
					user.getRatingsToEstimate(), gui, this.idtoURIHashMap);
		} else if (matchApproach == Mediator.PEARSON_SIM) {
			prepareFile(user.getRatingsToEstimate(), userProfile, iF);
			Matching.calculateMahoutPearsonCorSim(user.getRatingsToEstimate(),
					gui, this.idtoURIHashMap);
		} else if (matchApproach == Mediator.LOGLIKELIHOOD_SIM) {
			prepareFile(user.getRatingsToEstimate(), userProfile, iF);
			Matching.calculateMahoutLogLikelihoodSim(
					user.getRatingsToEstimate(), gui, this.idtoURIHashMap);
		}

	}

	/**
	 * 
	 * @param ifapproach
	 *            0 unweighted, 1 weighted
	 * @param ufapproach
	 *            0 matrix mult, 1 above treshhold, 2 userprofile from ratings
	 * @param matchApproach
	 *            0 cosine sim, 1 loglikelihood,
	 */
	public void makeRecommendation(int ifapproach, int ufapproach,
			int matchApproach, String userid) {

		if (idtoURIHashMap == null) {
			this.initializeMovieHashmap(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED);
		}
		User user = new User();
		user.setHetrec_id(userid);
		Vector<Rating> ratingsOfUser = uiM.getRatingsOfUser(userid);
		user.setRatings(ratingsOfUser);
		Vector<Rating> backData = user.getBackGroundData(0.85);
		HashMap<String, Double> userProfile = null;
		ItemFeature iF = null;
		if (ifapproach == Mediator.WEIGHTED) {
			// Prepare Weighted Item Feature Set UP
			iF = new WeightedItemFeature();
			System.out.println("Initialize weighted Scores");
			((WeightedItemFeature) iF).initializeFeatureScoresHasmap(gui
					.getSelectedPredicateFilterValues());
			System.out.println("Finished initilize weighted Scores");
		} else if (ifapproach == Mediator.UNWEIGHTED) {
			iF = new UnweightedItemFeature();
			((UnweightedItemFeature) iF).initializePredicateHashMap(
					ItemFeatureMatrix.DBPEDIA_UNWEIGHTED,
					gui.getSelectedPredicateFilterValues());
		}

		if (ufapproach == Mediator.PROF_CONS_APP_3) {
			userProfile = UserProfileContruction.approach3(backData, iF,
					ifapproach, this.idtoURIHashMap);
			printUserProfile(userProfile);
		} else if (ufapproach == Mediator.PROF_CONS_APP_2) {
			userProfile = UserProfileContruction.approach2(backData, iF,
					ifapproach, this.idtoURIHashMap);
		}
		if (matchApproach == Mediator.MYCOSINE_SIM) {
			Matching.CosineSimilarity(userProfile, idtoURIHashMap,
					user.getRatingsToEstimate(), iF, this.gui);
		} else if (matchApproach == Mediator.COSINE_SIM) {
			prepareFile(user.getRatingsToEstimate(), userProfile, iF);
			Matching.calculateMahoutUncenteredCosineSim(
					user.getRatingsToEstimate(), gui, this.idtoURIHashMap);
		} else if (matchApproach == Mediator.PEARSON_SIM) {
			prepareFile(user.getRatingsToEstimate(), userProfile, iF);
			Matching.calculateMahoutPearsonCorSim(user.getRatingsToEstimate(),
					gui, this.idtoURIHashMap);
		} else if (matchApproach == Mediator.LOGLIKELIHOOD_SIM) {
			prepareFile(user.getRatingsToEstimate(), userProfile, iF);
			Matching.calculateMahoutLogLikelihoodSim(
					user.getRatingsToEstimate(), gui, this.idtoURIHashMap);
		}

	}

	public void prepareFile(Vector<Rating> ratingsToEstimate,
			HashMap<String, Double> userProfile, ItemFeature iF) {

		// create spaltenIndex
		HashMap<Integer, String> hm_index_feature = new HashMap<Integer, String>();
		HashMap<String, Integer> hm_feature_index = new HashMap<String, Integer>();
		int index = 0;
		// browse through userProfile
		Set<String> set = userProfile.keySet();
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String feature = it.next();
			if (!hm_feature_index.containsKey(feature)) {
				// System.out.println(index + ": " + feature);
				hm_index_feature.put(index, feature);
				hm_feature_index.put(feature, index);
				index++;
			}
		}

		for (int i = 0; i < ratingsToEstimate.size(); i++) {
			Vector<String> featureVec = iF.getFeatureOfMovie(idtoURIHashMap
					.get(ratingsToEstimate.get(i).getMovie_lensID()));
			if (featureVec != null) {
				for (int j = 0; j < featureVec.size(); j++) {
					String feature = featureVec.get(j);
					if (!hm_feature_index.containsKey(feature)) {
						// System.out.println(index + ": " + feature);
						hm_index_feature.put(index, feature);
						hm_feature_index.put(feature, index);
						index++;
					}
				}
			}
		}

		// Create file
		FileWriter fstream;

		try {
			File f = new File("temp/file.dat");
			f.delete();
			fstream = new FileWriter(f);
	
			BufferedWriter out = new BufferedWriter(fstream);

			// make ratings in file
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				Vector<String> featureVec = iF.getFeatureOfMovie(idtoURIHashMap
						.get(ratingsToEstimate.get(i).getMovie_lensID()));
				if (featureVec != null) {
					for (int j = 0; j < featureVec.size(); j++) {
						String feature = featureVec.get(j);
						double score = iF.getScoreOf(
								this.idtoURIHashMap.get(ratingsToEstimate
										.get(i).getMovie_lensID()), feature);
						out.write(ratingsToEstimate.get(i).getMovie_lensID()
								+ "\t" + hm_feature_index.get(feature) + "\t"
								+ score);

						out.newLine();
					}
				}
			}
			// browse through userProfile
			Set<String> set2 = userProfile.keySet();
			for (Iterator<String> it = set2.iterator(); it.hasNext();) {
				String feature = it.next();
				double score = userProfile.get(feature);

				out.write("9999999\t" + hm_feature_index.get(feature) + "\t"
						+ score);
				/*
				 * out.write("9999999\t" + hm_feature_index.get(feature) + "\t"
				 * + "1");
				 */
				out.newLine();
				out.flush();
			}

			/*** printUserMatrix ***/

			if (Mediator.USE_MY_OWN_DATA_SET) {
				for (int i = 0; i < ratingsToEstimate.size(); i++) {
					Vector<String> featureVec = iF.getFeatureOfMovie(idtoURIHashMap
							.get(ratingsToEstimate.get(i).getMovie_lensID()));
					System.out.print(ratingsToEstimate.get(i).getMovie_lensID()
							+ ": [");
					if (featureVec != null) {
						Set<Integer> indexes = hm_index_feature.keySet();
						for (Iterator<Integer> it = indexes.iterator(); it
								.hasNext();) {
							int col = it.next();
							// check if feature of indexes are contained
							if (featureVec.contains(hm_index_feature.get(col))) {
								if (col < featureVec.size()) {
									String feature = featureVec.get(col);
									double score = iF.getScoreOf(
											this.idtoURIHashMap
													.get(ratingsToEstimate.get(i)
															.getMovie_lensID()),
											feature);
									System.out.print(Math.round( score * 100. ) / 100. + ", ");
								} else
									System.out.print(0 + ", ");
							} else {
								System.out.print(0 + ", ");
							}
						}
						System.out.println("]");
					}
				}
	
				System.out.print("UserProf: [");
				// browse through userProfile
				Set<String> userProf = userProfile.keySet();
				Set<Integer> indexes = hm_index_feature.keySet();
				for (Iterator<Integer> it = indexes.iterator(); it.hasNext();) {
					int col = it.next();
					// check if feature of indexes are contained
					if (userProf.contains(hm_index_feature.get(col))) {
						double score = userProfile.get(hm_index_feature.get(col));
						System.out.print(Math.round( score * 100. ) / 100. + ", ");
					} else {
						System.out.print(0 + ", ");
					}
				}
				System.out.println("]");
	
				Set<Integer> indexes2 = hm_index_feature.keySet();
				for (Iterator<Integer> it = indexes2.iterator(); it.hasNext();) {
					int col = it.next();
					System.out.println(col + " - " + hm_index_feature.get(col));
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printUserProfile(HashMap<String, Double> userProfile) {
		Set<String> set = userProfile.keySet();
		TextFrame tf = new TextFrame("User Profile");
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String feature = it.next();
			tf.newMessage(feature + ": " + userProfile.get(feature) + "\n");
		}
	}

	public void determineTreshhold(int ifapproach, int ufapproach,
			int matchapproach) {

		Vector<String> selPredicates= gui.getSelectedPredicateFilterValues();
		System.out.println("Selected Predicates: \n   " );
		for (int i = 0; i < selPredicates.size(); i++) {
			System.out.print(selPredicates.get(i) + "; ");
		}
		System.out.println("IF (" + ifapproach + "); UF(" + ufapproach + "); MATCH(" + matchapproach + ")");
		
		double ratingTreshold = Mediator.RATING_TRESHHOLD;
		double startTres = 0.3;

		// make initaliizations
		if (idtoURIHashMap == null) {
			this.initializeMovieHashmap(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED);
		}

		ItemFeature iF = null;
		if (ifapproach == Mediator.WEIGHTED) {
			// Prepare Weighted Item Feature Set UP
			iF = new WeightedItemFeature();
			System.out.println("Initialize weighted Scores");
			((WeightedItemFeature) iF).initializeFeatureScoresHasmap(gui
					.getSelectedPredicateFilterValues());
			System.out.println("Finished initilize weighted Scores");
		} else if (ifapproach == Mediator.UNWEIGHTED) {
			iF = new UnweightedItemFeature();
			((UnweightedItemFeature) iF).initializePredicateHashMap(
					ItemFeatureMatrix.DBPEDIA_UNWEIGHTED,
					gui.getSelectedPredicateFilterValues());
		}

		if (uiM != null) {

			while (startTres <= 1) {

				Set<Integer> users;

				if (Mediator.smallUserstoTest == false)
					users = uiM.getUsersWithRatingsInFile();
				else {
					users = new TreeSet<Integer>();
					users.add(28317);
					users.add(35622);
					users.add(75);
				}

				System.out
						.println("Count users that are inspected to determine precision and recall: "
								+ users.size());
				int count = 0;
				double avgprecision = 0;
				double avgrecall = 0;
				for (Iterator<Integer> it = users.iterator(); it.hasNext();) {
					Integer userid = it.next();
					PrecisionRecall pr = makeRecommendationforEvaluation(
							ifapproach, ufapproach, matchapproach,
							String.valueOf(userid), iF, startTres,
							ratingTreshold);
					if (pr != null) {
						/**
						 * System.out.print("Precision Recall " + userid +
						 * " Precision: " + String.valueOf(pr.getPrecision()) +
						 * " Recall: " + pr.getRecall());
						 * System.out.println(pr.getPrecision());
						 */
						avgprecision += pr.getPrecision();
						avgrecall += pr.getRecall();
						count++;
					}
				}
				System.out.println("Rating Treshhold: " + ratingTreshold);
				System.out.println("Score Treshhold: " + startTres);
				System.out.println("Total Precision: " + avgprecision / count);
				System.out.println("Total Recall: " + avgrecall / count);
				System.out.println("#########################################");
				startTres += 0.15;
			}
		}
	}

	/**
	 * 
	 * @param ifapproach
	 *            0 unweighted, 1 weighted
	 * @param ufapproach
	 *            0 matrix mult, 1 above treshhold, 2 userprofile from ratings
	 * @param matchApproach
	 *            0 cosine sim, 1 loglikelihood,
	 * @param ratingTreshold
	 */
	public PrecisionRecall makeRecommendationforEvaluation(int ifapproach,
			int ufapproach, int matchApproach, String userid, ItemFeature iF,
			double startTres, double ratingTreshold) {
		User user = new User();
		user.setHetrec_id(userid);
		Vector<Rating> ratingsOfUser = uiM.getRatingsOfUser(userid);
		user.setRatings(ratingsOfUser);
		Vector<Rating> backData = user.getBackGroundData(0.6);

		/*******/
		if (Mediator.smallUserstoTest) {
			Vector<Rating> vecRatings = user.getRatingsToEstimate();
			System.out.println("UserID:" + user.getHetrec_id());
			for (int ind = 0; ind < vecRatings.size(); ind++) {
				Rating r = vecRatings.get(ind);
				System.out.println("movieID: " + r.getMovie_lensID()
						+ ", rating: " + r.getRating());
			}
			System.out.println("");
		}

		HashMap<String, Double> userProfile = null;
		if (ufapproach == Mediator.PROF_CONS_APP_3) {
			userProfile = UserProfileContruction.approach3(backData, iF,
					ifapproach, this.idtoURIHashMap);
		} else if (ufapproach == Mediator.PROF_CONS_APP_2) {
			userProfile = UserProfileContruction.approach2(backData, iF,
					ifapproach, this.idtoURIHashMap);
		}

		// // check if ratings to estimate not only containes only 5
		/**
		 * boolean doit = false; int k = 0; for (int ind = 0; (ind <
		 * user.getRatingsToEstimate().size()) && (doit == false); ind++) {
		 * Rating r = user.getRatingsToEstimate().get(ind); if (r.getRating() <
		 * 10){ k++; if (k >= 3){ doit = true; } } }
		 */
		boolean doit = true;
		if (doit) {
			Vector<Integer> estimatedIDVec = null;

			if (matchApproach == Mediator.MYCOSINE_SIM) {
				Matching.CosineSimilarity(userProfile, idtoURIHashMap,
						user.getRatingsToEstimate(), iF, this.gui);
			} else if (matchApproach == Mediator.COSINE_SIM) {
				prepareFile(user.getRatingsToEstimate(), userProfile, iF);
				estimatedIDVec = Matching
						.calculateMahoutUncenteredCosineSimReturn(
								user.getRatingsToEstimate(), gui,
								this.idtoURIHashMap, startTres, iF, userProfile);
			} else if (matchApproach == Mediator.PEARSON_SIM) {
				prepareFile(user.getRatingsToEstimate(), userProfile, iF);
				estimatedIDVec = Matching.calculateMahoutPearsonCorSimReturn(
						user.getRatingsToEstimate(), gui, this.idtoURIHashMap,
						startTres, iF, userProfile);
			} else if (matchApproach == Mediator.LOGLIKELIHOOD_SIM) {
				prepareFile(user.getRatingsToEstimate(), userProfile, iF);
				estimatedIDVec = Matching
						.calculateMahoutLogLikelihoodSimReturn(
								user.getRatingsToEstimate(), gui,
								this.idtoURIHashMap, startTres, iF, userProfile);
			}

			Vector<Integer> relevantInCollection = new Vector<Integer>();
			for (int i = 0; i < user.getRatingsToEstimate().size(); i++) {
				Rating rat = user.getRatingsToEstimate().get(i);
				if (rat.getRating() >= ratingTreshold) {
					relevantInCollection.add(Integer.parseInt(rat
							.getMovie_lensID()));
				}
			}

			// dermine precision
			double relevantretrieved = 0;
			for (int i = 0; i < estimatedIDVec.size(); i++) {
				int id = estimatedIDVec.get(i);
				if (relevantInCollection.contains(new Integer(id)))
					relevantretrieved = relevantretrieved + 1;
			}

			if (estimatedIDVec.size() > 0 && relevantInCollection.size() > 0) {
				// System.out.print(relevantretrieved + "   /   " +
				// estimatedIDVec.size() + "   =    ");
				double precision = relevantretrieved / estimatedIDVec.size();
				// System.out.println(precision + " (Userid: " + userid + ")");
				double recall = relevantretrieved / relevantInCollection.size();

				PrecisionRecall pr = new PrecisionRecall(precision, recall);
				return pr;
			}
		}

		return null;

	}

	public void top5list(int ifapproach, int ufapproach, int matchapproach) {
		double ratingTreshold = Mediator.RATING_TRESHHOLD;
		double startTres = 0.4;

		// make initaliizations
		if (idtoURIHashMap == null) {
			this.initializeMovieHashmap(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED);
		}

		ItemFeature iF = null;
		if (ifapproach == Mediator.WEIGHTED) {
			// Prepare Weighted Item Feature Set UP
			iF = new WeightedItemFeature();
			System.out.println("Initialize weighted Scores");
			((WeightedItemFeature) iF).initializeFeatureScoresHasmap(gui
					.getSelectedPredicateFilterValues());
			System.out.println("Finished initilize weighted Scores");
		} else if (ifapproach == Mediator.UNWEIGHTED) {
			iF = new UnweightedItemFeature();
			((UnweightedItemFeature) iF).initializePredicateHashMap(
					ItemFeatureMatrix.DBPEDIA_UNWEIGHTED,
					gui.getSelectedPredicateFilterValues());
		}

		if (uiM != null) {

			while (startTres <= 1) {

				Set<Integer> users = uiM.getUsersWithRatingsInFile();
				System.out
						.println("Count users that are inspected to determine precision and recall: "
								+ users.size());
				int count = 0;
				double avgprecision = 0;
				double avgrecall = 0;
				for (Iterator<Integer> it = users.iterator(); it.hasNext();) {
					Integer userid = it.next();
					PrecisionRecall pr = makeRecommendationforEvaluation(
							ifapproach, ufapproach, matchapproach,
							String.valueOf(userid), iF, startTres,
							ratingTreshold);
					if (pr != null) {
						System.out.println("Precision Recall " + userid
								+ " Precision: "
								+ String.valueOf(pr.getPrecision())
								+ " Recall: " + pr.getRecall());
						avgprecision += pr.getPrecision();
						avgrecall += pr.getRecall();
						count++;
					}
				}
				System.out.println("Rating Treshhold: " + ratingTreshold);
				System.out.println("Score Treshhold: " + startTres);
				System.out.println("Total Precision: " + avgprecision / count);
				System.out.println("Total Recall: " + avgrecall / count);
				System.out.println("#########################################");
				startTres += 0.1;
			}
		}

	}

	double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
}
