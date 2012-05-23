package recommendation;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.RowSimilarityJob;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CosineSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.LoglikelihoodSimilarity;
import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

import bean.*;

import GUI.RecommenderGUI;

public class Mediator {

	ItemFeatureMatrix ifM = null;
	UserItemMatrix uiM = null;
	RecommenderGUI gui = null;

	public Mediator(RecommenderGUI gui) {
		this.gui = gui;
		ifM = new ItemFeatureMatrix(this.gui);
		uiM = new UserItemMatrix();
	}

	public static void main(String[] args) {

	}

	public void initializeDataSource(String string) {
		if (string.equals("dbPedia")) {
			ifM.initialize(ItemFeatureMatrix.DBPEDIA_UNWEIGHTED);
		}

	}

	/**
	 * 
	 * @param userid 
	 */
	public void makeRecommendation(String userid) {
		userid = "325";
		User user = new User();
		user.setHetrec_id(userid);
		Vector<Rating> vectorRatings = uiM.getRatingsOfUser(userid);
		user.setRatings(vectorRatings);
		Vector<Rating> backData = user.getBackGroundData(0.85);
		HashMap<String, Integer> hm_predicates_for_users_uritoid = new HashMap<String, Integer>();
		HashMap<Integer, String> hm_predicates_for_users_idtouri = new HashMap<Integer, String>();
		Vector<Feature> featureAll;
		int iter = 0;
		if (backData != null){
			SimpleMatrix userItemMatrix = new SimpleMatrix(1, backData.size());
			for (int i = 0; i < backData.size(); i++) {
				System.out.println("Rating :" + backData.get(i).getRating());
				userItemMatrix.set(0, i, (backData.get(i)).getRating());
				Vector<Feature> featureVec = ifM.getFeaturesOfMovie(Integer.parseInt(backData.get(i).getMovie_lensID()));
				for (int j = 0; j < featureVec.size(); j++){
					Feature f = featureVec.get(j);
					if (!hm_predicates_for_users_uritoid.containsKey(f.toString())){
						hm_predicates_for_users_idtouri.put(iter,f.toString());
						hm_predicates_for_users_uritoid.put(f.toString(), iter);
						iter ++;
					}
				}
			}
			SimpleMatrix itemfeature = new SimpleMatrix(backData.size(), hm_predicates_for_users_idtouri.size());
			for (int i = 0; i < backData.size(); i++) {
				Vector<Feature> featureVec = ifM.getFeaturesOfMovie(Integer.parseInt(backData.get(i).getMovie_lensID()));
				for (int j = 0; j < featureVec.size(); j++){
					Feature f = featureVec.get(j);
					itemfeature.set(i, hm_predicates_for_users_uritoid.get(f.toString()), 1);
				}
			}
			//System.out.println("User item matrix");
			// System.out.println(userItemMatrix);
			//System.out.println("Item feaure");
			// System.out.println(itemfeature);
			SimpleMatrix sm = userItemMatrix.mult(itemfeature);
			System.out.println("MAX : " + sm.elementMaxAbs());
			double max = sm.elementMaxAbs();
			sm = sm.divide(max);
			
			System.out.println(sm);
			
			Vector<FeatureScore> userprofile = new Vector<FeatureScore>();
			

			for (int i = 0; i < sm.numCols(); i++) {
				FeatureScore fs = new FeatureScore();
				fs.setFeature(hm_predicates_for_users_idtouri.get(i));
				fs.setScore(sm.get(0, i));
				userprofile.add(fs);
				System.out.println(hm_predicates_for_users_idtouri.get(i) + "Score: " + sm.get(0, i));
			}
			user.setUserprofile(userprofile);
			
			System.out.println(ifM.getCountDistinctPredicates());
			DenseMatrix64F userProfileMatrx = new DenseMatrix64F(1, ifM.getCountDistinctPredicates());
			for (int i = 0; i <userprofile.size(); i++){
				String feature = userprofile.get(i).getFeature();
				double score = userprofile.get(i).getScore();
				int col = ifM.getMatrixIndexOf(feature);
				userProfileMatrx.set(0, col, score);
			}
			

			Vector<Rating> toEstimate = user.getRatingsToEstimate();
			for (int i = 0; i < toEstimate.size(); i++) {
				Rating r = toEstimate.get(i);
				DenseMatrix64F mat = ifM.getVectorOfMovieID(Integer.parseInt(r.getMovie_lensID()));
				double dotProduct = 0;
				for (int j = 0; j < mat.getNumCols(); j++) {
					dotProduct += mat.get(0, j) * userProfileMatrx.get(0, j);
				}
				double norm1 = 0;
				double norm2 = 0;
				for (int j = 0; j < mat.getNumCols(); j++) {
					norm1 += mat.get(0, j) * mat.get(0, j);
					norm2 += userProfileMatrx.get(0, j) * userProfileMatrx.get(0, j);
				}
				
				
				double cosineDistance = dotProduct / Math.sqrt(norm1 * norm2);
				System.out.println("Estimation for movie " + r.getMovie_lensID() + ": Calculated Score: " + cosineDistance + " (Rating: " + r.getRating() + ")");
			}
		}


	}
}
