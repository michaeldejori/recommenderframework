package recommendation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import bean.Rating;

/**
 * class User Item matrix.
 * Manages the user item ratings.
 * Connector to the file "files/user_ratedmovies-timestamps.dat"
 * 
 * 
 * @author michael
 *
 */
public class UserItemMatrix {

	private final String ratedMoviesFile="files/user_ratedmovies-timestamps.dat";
	// private final String ratedMoviesFile="testfiles/testrat.dat";
	
	private HashMap<Integer, Vector<Rating>> hm_user_ratings = null;


	/**
	 * initializes the the file user ratings in the Hashmap
	 */
	public void initalizeDataFromFile(){
		hm_user_ratings =  new HashMap<Integer, Vector<Rating>>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(ratedMoviesFile)));

			String line = reader.readLine();

			while (line != null) {
				// 75 296 5 1162160689000
				String[] parts = line.split("\t");
				if (parts.length == 4) {
					String userID = parts[0];
					if (Integer.parseInt(userID) < Mediator.UPPER_LIMIT_USERID){		
						String movieID = parts[1];
						String rating = parts[2];
						String timestamp = parts[3];
						Rating r = new Rating();
						r.setMovie_lensID(Long.parseLong(movieID));
						r.setTimestamp(Long.parseLong(timestamp));
						r.setRating(Double.parseDouble(rating));
						if (this.hm_user_ratings.get(new Integer(Integer.parseInt(userID))) == null){
							this.hm_user_ratings.put(new Integer(Integer.parseInt(userID)), new Vector<Rating>());
						}
						Vector<Rating> ratings = this.hm_user_ratings.get(new Integer(Integer.parseInt(userID)));
						ratings.add(r);
					}
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initializeMyTestData(){
		hm_user_ratings = new HashMap<Integer, Vector<Rating>>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File("testfiles/user_test_ratings.dat")));

			String line = reader.readLine();

			while (line != null) {
				// 75 296 5 1162160689000
				String[] parts = line.split("\t");
				if (parts.length >= 4) {
					String userID = parts[0];
					String movieID = parts[1];
					String rating = parts[2];
					String timestamp = parts[3];
					Rating r = new Rating();
					r.setMovie_lensID(Long.parseLong(movieID));
					r.setTimestamp(Long.parseLong(timestamp));
					r.setRating(Double.parseDouble(rating));
					if (this.hm_user_ratings.get(new Integer(Integer.parseInt(userID))) == null){
						this.hm_user_ratings.put(new Integer(Integer.parseInt(userID)), new Vector<Rating>());
					}
					Vector<Rating> ratings = this.hm_user_ratings.get(new Integer(Integer.parseInt(userID)));
					ratings.add(r);
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Return all ratings done by a user
	 * 
	 * @param userid id of user
	 * @return Vector of Ratings class
	 */
	public Vector<Rating> getRatingsOfUser(String userid) {
		if (this.hm_user_ratings == null){ 
			System.out.println("hm_ratings Hasmap is null");
			return null;
		} else {
			return hm_user_ratings.get(new Integer(Integer.parseInt(userid)));
		}
	}
	
	/**
	 * 
	 * 
	 * @param tres
	 * @return
	 */
	public int removeUserWithRatingsUnderTres(int tres){
		Vector<Integer> remV = new Vector<Integer>();
		// determine which elements to remove
		if (this.hm_user_ratings != null){
			Set<Integer> kset = hm_user_ratings.keySet();
			for (Iterator<Integer> it = kset.iterator(); it.hasNext(); ) {
				Integer key = it.next();
				if (hm_user_ratings.get(key).size() < tres){
					remV.add(key);
				}
			}
		}
		// remove elements
		for (int i = 0; i < remV.size(); i++) {
			hm_user_ratings.remove(remV.get(i));
		}
		System.out.println("UserItemMatrix: Removed User under treshold: " + remV.size());
		return remV.size();
	}
	
	public Set<Integer> getUsersWithRatingsInFile(){
		return hm_user_ratings.keySet();
	}
	
}
