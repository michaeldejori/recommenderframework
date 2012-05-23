package recommendation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

import bean.Rating;


public class UserItemMatrix {

	private final String ratedMoviesFile="files/user_ratedmovies-timestamps.dat";
	private final String moviesFile="files/ids2freebase.dat";
		
	// User Hasmaps
	private HashMap<Integer, Integer> hm_user_matrix_to_lensid = null;
	private HashMap<Integer, Integer> hm_user_lensid_to_matrix = null;
	
	// Movie Hasmaps
	private HashMap<Integer, Integer> hm_movie_matrix_to_lensid = null;
	private HashMap<Integer, Integer> hm_movie_lensid_to_matrix = null;
	
	
	
	public int getCountUsers(){
		return this.hm_user_lensid_to_matrix == null ? -1 : hm_user_lensid_to_matrix.size();
	}
	
	public int getCountMovies(){
		return this.hm_movie_lensid_to_matrix == null ? -1 : hm_movie_lensid_to_matrix.size();
	}
	
	public void initialize(){
		fillHashMapUser();
		fillHashMapMovie();		
	}
	
	
	/**
	 * 
	 * @param countUsers as row count
	 * @param countMovies as column count
	 * @return
	 */
	public SimpleMatrix createUseritemMatrix() {
		SimpleMatrix sm = null;
		// create empty matrix
		sm = new SimpleMatrix(this.getCountUsers(), this.getCountMovies());
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(ratedMoviesFile)));

			String line = reader.readLine();
			while (line != null) {
				String [] parts = line.split("\t");
				if (parts.length == 4) {
					Integer user_lens_id = Integer.parseInt(parts[0]);
					Integer movie_id = Integer.parseInt(parts[1]);
					double rating = Double.parseDouble(parts[2]);
					
					Integer user_matrix_id = hm_user_lensid_to_matrix.get(user_lens_id);
					Integer movie_matrix_id = hm_movie_lensid_to_matrix.get(movie_id);
					
					if (movie_matrix_id != null) {
						// because some movie entries could not be linked
						// user_matix id start from 1
						sm.set(user_matrix_id - 1, movie_matrix_id - 1, rating);
					}
					
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sm;
	}
	
	/**
	 * Return all ratings done by a user
	 * 
	 * @param userid id of user
	 * @return Vector of Ratings class
	 */
	public Vector<Rating> getRatingsOfUser(String userid) {
		Vector<Rating> vecRating = new Vector();
		BufferedReader reader;
		try {

			reader = new BufferedReader(new FileReader(new File(
					"files/user_ratedmovies-timestamps.dat")));

			String line = reader.readLine();

			// index for hashmap and then matrix
			int i = 1;

			while (line != null) {
				// 75 296 5 1162160689000
				String[] parts = line.split("\t");
				if (parts.length == 4 && parts[0].equals(userid)) {
					String movieID = parts[1];
					String rating = parts[2];
					String timestamp = parts[3];
					Rating r = new Rating();
					r.setMovie_lensID(movieID);
					r.setTimestamp(Long.parseLong(timestamp));
					r.setRating(Double.parseDouble(rating));
					vecRating.add(r);

				}
				line = reader.readLine();
			}

			return vecRating;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void fillHashMapUser(){
		/*
		 * ids of user are not dense, so make a mapping from dense ids to movielens id 
		 * 1 -> 75
		 * and the reverse also
		 */
		
		hm_user_matrix_to_lensid = new HashMap<Integer, Integer>();
		hm_user_lensid_to_matrix = new HashMap<Integer, Integer>();
		
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(ratedMoviesFile)));

			String line = reader.readLine();
			int i = 1;
			while (line != null) {
				String [] parts = line.split("\t");
				if (parts.length == 4) {
					Integer user_id = Integer.parseInt(parts[0]);
					//Integer movie_id = Integer.parseInt(parts[1]);
					//double rating = Integer.parseInt(parts[2]);
					
					// test if user already is in map
					if ( !hm_user_lensid_to_matrix.containsKey(user_id)){
						// user not in map, make entry
						hm_user_lensid_to_matrix.put(user_id, i);
						hm_user_matrix_to_lensid.put(i, user_id);
						// System.out.println( i + " <=> " + user_id);
						i++;
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
	
	
	public void fillHashMapMovie(){
		
		hm_movie_matrix_to_lensid = new HashMap<Integer, Integer>();
		hm_movie_lensid_to_matrix = new HashMap<Integer, Integer>();
			
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(moviesFile)));

			String line = reader.readLine();
			int i = 1;
			while (line != null) {
				String [] parts = line.split("\t");
				if (parts.length == 2) {
					Integer movie_lens_id = Integer.parseInt(parts[0]);
					
					// test if user already is in map
					if ( !hm_movie_lensid_to_matrix.containsKey(movie_lens_id)){
						// movie not in map, make entry
						hm_movie_lensid_to_matrix.put(movie_lens_id, i);
						hm_movie_matrix_to_lensid.put(i, movie_lens_id);
						// System.out.println( i + " <=> " + movie_lens_id);
						i++;
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
	
}
