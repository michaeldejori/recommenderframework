package recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.Matrix;
import org.ejml.data.DenseMatrix64F;

import recommendation.itemfeature.ItemFeature;
import recommendation.itemfeature.UnweightedItemFeature;
import recommendation.itemfeature.WeightedItemFeature;
import GUI.RecommenderGUI;
import bean.Feature;
import bean.Rating;
import bean.User;

public class Mediator {

	public static final int COSINE_SIM = 1;
	public static final int MYCOSINE_SIM = 0;
	public static final int LOGLIKELIHOOD_SIM = 2;
	public static final int PEARSON_SIM = 3;
	
	public static final int WEIGHTED = 1;
	public static final int UNWEIGHTED = 2;

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
	 * approach 2
	 * 
	 * @param userid
	 */
	public void makeRecommendation2(String userid) {
		userid = "325";
		User user = new User();
		user.setHetrec_id(userid);
		Vector<Rating> ratingsOfUser = uiM.getRatingsOfUser(userid);
		user.setRatings(ratingsOfUser);
		Vector<Rating> backData = user.getBackGroundData(0.85);

		if (backData != null) {
			for (int i = 0; i < backData.size(); i++) {

			}
		}
	}

	/**
	 * approach 3
	 * 
	 * @param userid
	 */
	public void makeRecommendation3(String userid) {
		/*
		 * userid = "325"; User user = new User(); user.setHetrec_id(userid);
		 * Vector<Rating> ratingsOfUser = uiM.getRatingsOfUser(userid);
		 * user.setRatings(ratingsOfUser); Vector<Rating> backData =
		 * user.getBackGroundData(0.85);
		 * 
		 * HashMap<String, Double> hm_feature_value = new HashMap<String,
		 * Double>(); if (backData != null){ for (int i = 0; i <
		 * backData.size(); i++) { Rating r = backData.get(i); Vector<Feature>
		 * featureVector =
		 * ifM.getFeaturesOfMovie(Integer.parseInt(r.getMovie_lensID())); for
		 * (int j = 0; j < featureVector.size(); j++) { if
		 * (!hm_feature_value.containsKey(featureVector.get(j))){
		 * hm_feature_value.put(featureVector.get(j).toString(), (double)0); }
		 * double a = hm_feature_value.get(featureVector.get(j).toString()); a
		 * += r.getRating()/5;
		 * hm_feature_value.put(featureVector.get(j).toString(), new Double(a));
		 * } } // divide all by N rated elements Set<String> keySet =
		 * hm_feature_value.keySet(); for (Iterator<String> it =
		 * keySet.iterator(); it.hasNext();){ String f = it.next();
		 * hm_feature_value.put(f, hm_feature_value.get(f)/5);
		 * System.out.println(f + " Score: " + hm_feature_value.get(f)); } }
		 */
	}

	/**
	 * approach 1
	 * 
	 * @param userid
	 */
	public void makeRecommendation(String userid) {
		/*
		 * userid = "325"; User user = new User(); user.setHetrec_id(userid);
		 * Vector<Rating> vectorRatings = uiM.getRatingsOfUser(userid);
		 * user.setRatings(vectorRatings); Vector<Rating> backData =
		 * user.getBackGroundData(0.85); HashMap<String, Integer>
		 * hm_predicates_for_users_uritoid = new HashMap<String, Integer>();
		 * HashMap<Integer, String> hm_predicates_for_users_idtouri = new
		 * HashMap<Integer, String>(); Vector<Feature> featureAll; int iter = 0;
		 * if (backData != null){ SimpleMatrix userItemMatrix = new
		 * SimpleMatrix(1, backData.size()); for (int i = 0; i <
		 * backData.size(); i++) { System.out.println("Rating :" +
		 * backData.get(i).getRating()); userItemMatrix.set(0, i,
		 * (backData.get(i)).getRating()); Vector<Feature> featureVec =
		 * ifM.getFeaturesOfMovie
		 * (Integer.parseInt(backData.get(i).getMovie_lensID())); for (int j =
		 * 0; j < featureVec.size(); j++){ Feature f = featureVec.get(j); if
		 * (!hm_predicates_for_users_uritoid.containsKey(f.toString())){
		 * hm_predicates_for_users_idtouri.put(iter,f.toString());
		 * hm_predicates_for_users_uritoid.put(f.toString(), iter); iter ++; } }
		 * } SimpleMatrix itemfeature = new SimpleMatrix(backData.size(),
		 * hm_predicates_for_users_idtouri.size()); for (int i = 0; i <
		 * backData.size(); i++) { Vector<Feature> featureVec =
		 * ifM.getFeaturesOfMovie
		 * (Integer.parseInt(backData.get(i).getMovie_lensID())); for (int j =
		 * 0; j < featureVec.size(); j++){ Feature f = featureVec.get(j);
		 * itemfeature.set(i, hm_predicates_for_users_uritoid.get(f.toString()),
		 * 1); } } //System.out.println("User item matrix"); //
		 * System.out.println(userItemMatrix);
		 * //System.out.println("Item feaure"); //
		 * System.out.println(itemfeature); SimpleMatrix sm =
		 * userItemMatrix.mult(itemfeature); System.out.println("MAX : " +
		 * sm.elementMaxAbs()); double max = sm.elementMaxAbs(); sm =
		 * sm.divide(max);
		 * 
		 * System.out.println(sm);
		 * 
		 * Vector<FeatureScore> userprofile = new Vector<FeatureScore>();
		 * 
		 * 
		 * for (int i = 0; i < sm.numCols(); i++) { FeatureScore fs = new
		 * FeatureScore();
		 * fs.setFeature(hm_predicates_for_users_idtouri.get(i));
		 * fs.setScore(sm.get(0, i)); userprofile.add(fs);
		 * System.out.println(hm_predicates_for_users_idtouri.get(i) + "Score: "
		 * + sm.get(0, i)); } user.setUserprofile(userprofile);
		 * 
		 * System.out.println(ifM.getCountDistinctPredicates()); DenseMatrix64F
		 * userProfileMatrx = new DenseMatrix64F(1,
		 * ifM.getCountDistinctPredicates()); for (int i = 0; i
		 * <userprofile.size(); i++){ String feature =
		 * userprofile.get(i).getFeature(); double score =
		 * userprofile.get(i).getScore(); int col =
		 * ifM.getMatrixIndexOf(feature); userProfileMatrx.set(0, col, score); }
		 * 
		 * 
		 * Vector<Rating> toEstimate = user.getRatingsToEstimate(); for (int i =
		 * 0; i < toEstimate.size(); i++) { Rating r = toEstimate.get(i);
		 * DenseMatrix64F mat =
		 * ifM.getVectorOfMovieID(Integer.parseInt(r.getMovie_lensID())); double
		 * dotProduct = 0; for (int j = 0; j < mat.getNumCols(); j++) {
		 * dotProduct += mat.get(0, j) * userProfileMatrx.get(0, j); } double
		 * norm1 = 0; double norm2 = 0; for (int j = 0; j < mat.getNumCols();
		 * j++) { norm1 += mat.get(0, j) * mat.get(0, j); norm2 +=
		 * userProfileMatrx.get(0, j) * userProfileMatrx.get(0, j); }
		 * 
		 * 
		 * double cosineDistance = dotProduct / Math.sqrt(norm1 * norm2);
		 * System.out.println("Estimation for movie " + r.getMovie_lensID() +
		 * ": Calculated Score: " + cosineDistance + " (Rating: " +
		 * r.getRating() + ")"); } }
		 */

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
			((WeightedItemFeature) iF).initializeFeatureScoresHasmap();
			System.out.println("Finished initilize weighted Scores");
		} else if (ifapproach == Mediator.UNWEIGHTED) {
			iF = new UnweightedItemFeature();
			((UnweightedItemFeature) iF).initializePredicateHashMap(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED);
		}

		if (ufapproach == 2) {
			userProfile = UserProfileContruction.approach3(backData, iF, ifapproach,
					this.idtoURIHashMap);
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
				System.out.println(index + ": " + feature);
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
						System.out.println(index + ": " + feature);
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
			fstream = new FileWriter("temp/file.dat");
			BufferedWriter out = new BufferedWriter(fstream);

			// make ratings in file
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				Vector<String> featureVec = iF.getFeatureOfMovie(idtoURIHashMap
						.get(ratingsToEstimate.get(i).getMovie_lensID()));
				if (featureVec != null) {
					for (int j = 0; j < featureVec.size(); j++) {
						String feature = featureVec.get(j);
						double score = iF
								.getScoreOf(this.idtoURIHashMap.get(ratingsToEstimate
										.get(i).getMovie_lensID()), feature);
						out.write(ratingsToEstimate.get(i).getMovie_lensID() + "\t"
								+ hm_feature_index.get(feature) + "\t" + score);
						out.newLine();
					}
				}
			}
			// browse through userProfile
			Set<String> set2 = userProfile.keySet();
			for (Iterator<String> it = set2.iterator(); it.hasNext();) {
				String feature = it.next();
				double score = userProfile.get(feature);
				System.out.println("9999999\t" + hm_feature_index.get(feature) + "\t"
						+ score);
				out.write("9999999\t" + hm_feature_index.get(feature) + "\t"
						+ score);
				out.newLine();
				out.flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printUserProfile(HashMap<String, Double> userProfile) {
		Set<String> set = userProfile.keySet();
		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String feature = it.next();
			System.out.println(feature + ": " + userProfile.get(feature));
		}

	}
}
