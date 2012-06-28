package recommendation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import recommendation.itemfeature.ItemFeature;
import GUI.RecommenderGUI;
import bean.Rating;

public class Matching {

	public static void CosineSimilarity(HashMap<String, Double> userProfile,
			HashMap<String, String> idtoURIHashMap, Vector<Rating> ratings,
			ItemFeature iF, RecommenderGUI gui) {

		for (int i = 0; i < ratings.size(); i++) {
			// compute dot Product
			double sumDotProduct = 0;
			double score = 0;
			String movieURI = idtoURIHashMap.get(ratings.get(i)
					.getMovie_lensID());
			Vector<String> movieFeatures = iF.getFeatureOfMovie(movieURI);
			// dot product multiply same features
			if (movieFeatures != null) {
				for (int j = 0; j < movieFeatures.size(); j++) {
					String f = movieFeatures.get(j);
					if (userProfile.containsKey(f)) {
						System.out.println(f + "is a common feature");
						sumDotProduct += userProfile.get(f)
								* iF.getScoreOf(movieURI, f);
					}
				}
				// compute ||A|| A[1]*A[1] + A[2]*A[2] + .... dann noch wurzel
				double sc = 0;
				Collection<Double> c = userProfile.values();
				for (Iterator<Double> it = c.iterator(); it.hasNext();) {
					double value = it.next();
					sc += value * value;
				}

				// compute ||B|| B[1]*B[1] + B[2]*B[2] + .... dann noch wurzel
				double sb = 0;
				for (int k = 0; k < movieFeatures.size(); k++) {
					String feature = movieFeatures.get(k);
					double value = iF.getScoreOf(movieURI, feature);
					sb += value * value;
				}
				score = sumDotProduct / (Math.sqrt(sc) * Math.sqrt(sb));
			}

			System.out.println("asdasdsadasdsadasdasdasdasd");
			String toDisplay = idtoURIHashMap.get(ratings.get(i)
					.getMovie_lensID());
			toDisplay += "(" + ratings.get(i).getMovie_lensID() + ") Score: "
					+ score + " (Rating " + ratings.get(i).getRating() + ")\n";
			gui.newMessage(toDisplay);
		}
	}

	public static void calculateMahoutUncenteredCosineSim(
			Vector<Rating> ratingsToEstimate, RecommenderGUI gui,
			HashMap<Long, String> idtoURIHashMap) {
		DataModel model;
		try {
			File f = new File("temp/file.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new UncenteredCosineSimilarity(model);
			gui.newMessage("\n\nMahout Uncentered Cosine Similarity:\n");
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				double score = usersim.userSimilarity(new Long(
						ratingsToEstimate.get(i).getMovie_lensID()), 9999999);
				String toDisplay = idtoURIHashMap.get(ratingsToEstimate.get(i)
						.getMovie_lensID());
				toDisplay += "(" + ratingsToEstimate.get(i).getMovie_lensID()
						+ ") Score: " + score + " (Rating "
						+ ratingsToEstimate.get(i).getRating() + ")\n";
				gui.newMessage(toDisplay);
			}
		} catch (NoSuchUserException e) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void calculateMahoutPearsonCorSim(
			Vector<Rating> ratingsToEstimate, RecommenderGUI gui,
			HashMap<Long, String> idtoURIHashMap) {
		DataModel model;
		try {
			File f = new File("temp/file.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new PearsonCorrelationSimilarity(model);
			gui.newMessage("\n\nMahout Pearson Correlation Similarity:\n");
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				System.out.println("Ich bin hier");
				double score = usersim.userSimilarity(new Long(
						ratingsToEstimate.get(i).getMovie_lensID()), 9999999);
				System.out.println(score);
				score = (score + 1) / 2;
				System.out.println(score);
				String toDisplay = "";
				toDisplay += idtoURIHashMap.get(ratingsToEstimate.get(i)
						.getMovie_lensID());
				toDisplay += "(" + ratingsToEstimate.get(i).getMovie_lensID()
						+ ") Score: " + score + " (Rating "
						+ ratingsToEstimate.get(i).getRating() + ")\n";
				gui.newMessage(toDisplay);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void calculateMahoutLogLikelihoodSim(
			Vector<Rating> ratingsToEstimate, RecommenderGUI gui,
			HashMap<Long, String> idtoURIHashMap) {
		DataModel model;
		try {
			File f = new File("temp/file.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new LogLikelihoodSimilarity(model);
			gui.newMessage("\n\nMahout Loglikelihood Similarity:\n");
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				double score = usersim.userSimilarity(new Long(
						ratingsToEstimate.get(i).getMovie_lensID()), 9999999);
				String toDisplay = idtoURIHashMap.get(ratingsToEstimate.get(i)
						.getMovie_lensID());
				toDisplay += "(" + ratingsToEstimate.get(i).getMovie_lensID()
						+ ") Score: " + score + " (Rating "
						+ ratingsToEstimate.get(i).getRating() + ")\n";
				gui.newMessage(toDisplay);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}

	public static Vector<Long> calculateMyCosineSimReturn(
			Vector<Rating> ratingsToEstimate, RecommenderGUI gui,
			HashMap<Long, String> idtoURIHashMap, double scoreTreshhold,
			ItemFeature iF, HashMap<String, Double> userProfile) {
		
		Vector<Long> vecint = new Vector<Long>();
		for (int i = 0; i < ratingsToEstimate.size(); i++) {
			// compute dot Product
			double sumDotProduct = 0;
			double score = 0;
			String movieURI = idtoURIHashMap.get(ratingsToEstimate.get(i)
					.getMovie_lensID());
			Vector<String> movieFeatures = iF.getFeatureOfMovie(movieURI);
			// dot product multiply same features
			if (movieFeatures != null) {
				for (int j = 0; j < movieFeatures.size(); j++) {
					String f = movieFeatures.get(j);
					if (userProfile.containsKey(f)) {
						sumDotProduct += userProfile.get(f)
								* iF.getScoreOf(movieURI, f);
					}
				}
				// compute ||A|| A[1]*A[1] + A[2]*A[2] + .... dann noch wurzel
				double sc = 0;
				Collection<Double> c = userProfile.values();
				for (Iterator<Double> it = c.iterator(); it.hasNext();) {
					double value = it.next();
					sc += value * value;
				}

				// compute ||B|| B[1]*B[1] + B[2]*B[2] + .... dann noch wurzel
				double sb = 0;
				for (int k = 0; k < movieFeatures.size(); k++) {
					String feature = movieFeatures.get(k);
					double value = iF.getScoreOf(movieURI, feature);
					sb += value * value;
				}
				score = sumDotProduct / (Math.sqrt(sc) * Math.sqrt(sb));
				if (score > scoreTreshhold){
					vecint.add(ratingsToEstimate
							.get(i).getMovie_lensID());
				}
			}
		}
		return vecint;
	}
	
	public static Vector<Long> calculateMahoutUncenteredCosineSimReturn(
			Vector<Rating> ratingsToEstimate, RecommenderGUI gui,
			HashMap<Long, String> idtoURIHashMap, double scoreTreshhold,
			ItemFeature iF, HashMap<String, Double> userProfile, int minSamePredicates) {
		Vector<Long> vecint = new Vector<Long>();
		DataModel model;
		try {
			File f = new File("temp/file.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new UncenteredCosineSimilarity(model);
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				if (minSamePredicates > 0) {
					// check if enough common Predicates
					String movie1URI = idtoURIHashMap.get(ratingsToEstimate
							.get(i).getMovie_lensID());
					int comPredicates = iF.getCommonPredicates(movie1URI,
							userProfile.keySet());
					if (comPredicates >= minSamePredicates) {
						try {
							double score = usersim.userSimilarity(
									new Long(ratingsToEstimate.get(i)
											.getMovie_lensID()), 9999999);
							
							
							/** print scores **/
							if (Mediator.smallUserstoTest) {
								System.out
										.println("Score estimated Treshhold : "
												+ scoreTreshhold
												+ " Movie("
												+ ratingsToEstimate.get(i).getMovie_lensID()
												+ ")    ScoreCalc:" + score);

							}
							if (score > scoreTreshhold) {
								vecint.add(ratingsToEstimate
										.get(i).getMovie_lensID());
							}
						} catch (NoSuchUserException e) {

						}
					} else {
						// System.out.println(movie1URI +
						// " and user do not have at least 2 common predicates");
					}
				} else {
					try {
						double score = usersim.userSimilarity(new Long(
								ratingsToEstimate.get(i).getMovie_lensID()),
								9999999);
						/** print scores **/
						if (Mediator.smallUserstoTest) {
							System.out
									.println("Score estimated Treshhold : "
											+ scoreTreshhold
											+ " Movie("
											+ ratingsToEstimate.get(i).getMovie_lensID()
											+ ")    ScoreCalc:" + score);

						}
						if (score > scoreTreshhold) {
							vecint.add(ratingsToEstimate
									.get(i).getMovie_lensID());
						}
						
					} catch (NoSuchUserException e) {

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
		return vecint;
	}

	public static Vector<Long> calculateMahoutPearsonCorSimReturn(
			Vector<Rating> ratingsToEstimate, RecommenderGUI gui,
			HashMap<Long, String> idtoURIHashMap, double scoreTreshhold,
			ItemFeature iF, HashMap<String, Double> userProfile, int minSamePredicates) {
		Vector<Long> vecint = new Vector<Long>();
		DataModel model;
		try {
			File f = new File("temp/file.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new PearsonCorrelationSimilarity(model);

			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				if (minSamePredicates > 0) {
					// check if enough common Predicates
					String movie1URI = idtoURIHashMap.get(ratingsToEstimate
							.get(i).getMovie_lensID());
					int comPredicates = iF.getCommonPredicates(movie1URI,
							userProfile.keySet());
					if (comPredicates >= minSamePredicates) {
						try {
							double score = usersim.userSimilarity(
									new Long(ratingsToEstimate.get(i)
											.getMovie_lensID()), 9999999);
							score = (score + 1) / 2;
							/** print scores **/
							if (Mediator.smallUserstoTest) {
								System.out
										.println("Score estimated Treshhold : "
												+ scoreTreshhold
												+ " Movie("
												+ ratingsToEstimate.get(i).getMovie_lensID()
												+ ")    ScoreCalc:" + score);

							}
							
							if (score > scoreTreshhold) {
								vecint.add(ratingsToEstimate
										.get(i).getMovie_lensID());
							}
						} catch (NoSuchUserException e) {

						}
					}
				} else {
					try {
						double score = usersim.userSimilarity(new Long(
								ratingsToEstimate.get(i).getMovie_lensID()),
								9999999);
						/** print scores **/
						if (Mediator.smallUserstoTest) {
							System.out
									.println("Score estimated Treshhold : "
											+ scoreTreshhold
											+ " Movie("
											+ ratingsToEstimate.get(i).getMovie_lensID()
											+ ")    ScoreCalc:" + score);

						}
						if (score > scoreTreshhold) {
							vecint.add(ratingsToEstimate
									.get(i).getMovie_lensID());
						}
					} catch (NoSuchUserException e) {

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
		return vecint;
	}

	public static Vector<Long> calculateMahoutLogLikelihoodSimReturn(
			Vector<Rating> ratingsToEstimate, RecommenderGUI gui,
			HashMap<Long, String> idtoURIHashMap, double scoreTreshhold,
			ItemFeature iF, HashMap<String, Double> userProfile, int minSamePredicates) {
		Vector<Long> vecint = new Vector<Long>();
		DataModel model;
		try {
			File f = new File("temp/file.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new LogLikelihoodSimilarity(model);
			for (int i = 0; i < ratingsToEstimate.size(); i++) {
				if (minSamePredicates > 0) {
					// check if enough common Predicates
					String movie1URI = idtoURIHashMap.get(ratingsToEstimate
							.get(i).getMovie_lensID());
					int comPredicates = iF.getCommonPredicates(movie1URI,
							userProfile.keySet());
					if (comPredicates >= minSamePredicates) {
						
						try {
							double score = usersim.userSimilarity(
									new Long(ratingsToEstimate.get(i)
											.getMovie_lensID()), 9999999);

							/** print scores **/
							if (Mediator.smallUserstoTest) {
								System.out
										.println("Score estimated Treshhold : "
												+ scoreTreshhold
												+ " Movie("
												+ ratingsToEstimate.get(i).getMovie_lensID()
												+ ")    ScoreCalc:" + score);

							}

							if (score > scoreTreshhold) {
								vecint.add(ratingsToEstimate
										.get(i).getMovie_lensID());
							}
						} catch (NoSuchUserException e) {

						}

					}
				} else {
					try {
						double score = usersim.userSimilarity(new Long(
								ratingsToEstimate.get(i).getMovie_lensID()),
								9999999);
						
						/** print scores **/
						if (Mediator.smallUserstoTest) {
							System.out
									.println("Score estimated Treshhold : "
											+ scoreTreshhold
											+ " Movie("
											+ ratingsToEstimate.get(i).getMovie_lensID()
											+ ")    ScoreCalc:" + score);

						}
						
						if (score > scoreTreshhold) {
							vecint.add(ratingsToEstimate
									.get(i).getMovie_lensID());

						}
					} catch (NoSuchUserException e) {

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
		return vecint;
	}
}
