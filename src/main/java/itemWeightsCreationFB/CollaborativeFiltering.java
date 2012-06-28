package itemWeightsCreationFB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.commons.math.util.MultidimensionalCounter.Iterator;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

public class CollaborativeFiltering {

	// Parameters
	// movieIDpublic static long itemId = 595;
	public static int NN = 20;
	public static int minNumberOfRatings = 0;
	public static String dataFileName = "files/user_ratedmovies-timestamps.dat";
	public static String moviesFileName = "files/movies.dat";
	public static String dbpediaIds = "files/ids2freebase.dat";

	// Repository
	// private final static String FREE_BASE_URI =
	// "http://rdf.freebase.com/rdf";
	private static final String SESAME_SERVER = "http://lokino.sti2.at:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "fb";
	private static Repository myRepository = null;

	public static HashMap<Long, String> movies = null;
	public static HashMap<Long, String> freebase = null;

	public static void main(String[] args) throws IOException, TasteException {
		myRepository = new HTTPRepository(SESAME_SERVER, REPOSITORY_ID);
		ValueFactory f = myRepository.getValueFactory();
		movies = readMovies(moviesFileName);
		freebase = getFreebaseIds(dbpediaIds);
		DataModel model = new FileDataModel(new File(dataFileName), true,
				FileDataModel.DEFAULT_MIN_RELOAD_INTERVAL_MS);// new
																// RemoveUsersThreshold(new
																// File(dataFileName),
																// minNumberOfRatings,
																// true);
		UserSimilarity itemSimilarity = new LogLikelihoodSimilarity(model);

		// itemneighborhood
		UserNeighborhood itemNeighborhood = new NearestNUserNeighborhood(NN,
				itemSimilarity, model);

		// for all movies
		Set<Long> movieSet = freebase.keySet();
		int count = 0;
		for (java.util.Iterator<Long> it = movieSet.iterator(); it.hasNext();) {
			System.out.println(++count);
			Long movieID = it.next();

			long[] neighbors = itemNeighborhood.getUserNeighborhood(movieID);

			try {
				
				// prepare File
				FileWriter filewriter = new FileWriter("files/nearestneighborfreebase2.dat", true);
				BufferedWriter bf = new BufferedWriter(filewriter);
				
				System.out.println("Movie: " + movies.get(movieID) + ";\t" + NN
						+ " nearest neighbors;");
				for (int i = 0; i < model.getPreferencesFromUser(movieID)
						.length(); i++) {
					System.out.print(model.getPreferencesFromUser(movieID)
							.get(i).getValue()
							+ ", ");
				}
				String uri = freebase.get(movieID);
				URI subject = f.createURI(uri);
				URI predicate = f.createURI("http://knn.idea/20");
				boolean added = false;
				System.out.println(uri);
				// System.out.println();
				// System.out.println("--------------------------------------------------");
				for (long l : neighbors) {
					// System.out.println(l + "\t" +movies.get(l) + "\t" +
					// itemSimilarity.userSimilarity(itemId, l) + "\t" +
					// freebase.get(l));
					// for (int i = 0; i <
					// model.getPreferencesFromUser(l).length();
					// i++) {
					// System.out.print(model.getPreferencesFromUser(l).get(i).getValue()
					// + ", ");
					// }
					// System.out.println();

					// write information into the triple store
					try {
						RepositoryConnection con = myRepository.getConnection();

						// weil in dbpedia nicht alle neighbors linked sein
						// müssen
						if (freebase.get(l) != null) {
							URI object = f.createURI(freebase.get(l));
							System.out.println(Math.round(itemSimilarity
									.userSimilarity(movieID, l) * 1000)
									/ 1000.0 + "\t & \t" + object.toString());
							try {
								 System.out.println(subject + " " + predicate
								 + " " + object);
								
								/**
								 * writing on Repository
								 */
								// con.add(subject, predicate, object);
							


								int j = 1;
								bf.write("<" + subject + ">\t<" + predicate + ">\t<" + object + ">.");
								bf.newLine();

								System.out.println(j++);
									

	
								/**
								 * writing on file
								 */
							
							
							} finally {
								con.close();
							}
						}
					} catch (OpenRDFException e) {
						e.printStackTrace();// handle exception
					}

				}
				bf.close();
				
			} catch (org.apache.mahout.cf.taste.common.NoSuchUserException e) {
				System.out.println("No such user");
			} catch (Exception e){
				System.out.println(e.getStackTrace());
			}
		}

	}

	public static HashMap<Long, String> readMovies(String fileName)
			throws IOException {
		HashMap<Long, String> map = new HashMap<Long, String>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(
				fileName)));
		reader.readLine();
		String line = reader.readLine();

		while (line != null) {
			String[] parts = line.split("\t");
			Long id = Long.parseLong(parts[0]);
			String title = parts[1];
			map.put(id, title);

			line = reader.readLine();
		}
		return map;
	}

	public static HashMap<Long, String> getFreebaseIds(String fileName)
			throws IOException {
		HashMap<Long, String> map = new HashMap<Long, String>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(
				fileName)));
		// reader.readLine();
		String line = reader.readLine();

		while (line != null) {
			String[] parts = line.split("\t");
			if (parts.length == 2) {
				Long id = Long.parseLong(parts[0]);
				String freebase = parts[1];
				map.put(id, freebase);
			}
			line = reader.readLine();
		}
		System.out.println("Count movies: " + map.size());
		return map;
	}
}
