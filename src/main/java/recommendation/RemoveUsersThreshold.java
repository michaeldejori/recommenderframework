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


public class RemoveUsersThreshold {
	public static final int MIN_RATINGSTRESHHOLD = 10;
	private final String ratedMoviesFile = "files/user_ratedmovies-timestamps.dat";
	
	
	public Vector<Integer> getMoviesRatedUnderMinTreshold(){
		// Hashmap movieid -> count ratings
		HashMap<Integer, Integer> hm_movie_ratins = new HashMap<Integer, Integer>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(ratedMoviesFile)));

			String line = reader.readLine();
			int i = 1;
			while (line != null) {
				String [] parts = line.split("\t");
				if (parts.length == 4) {
					// Integer user_id = Integer.parseInt(parts[0]);
					Integer movie_id = Integer.parseInt(parts[1]);
					//double rating = Integer.parseInt(parts[2]);
					
					if ( !hm_movie_ratins.containsKey(movie_id)){
						// movie not in map, make entry
						hm_movie_ratins.put(movie_id, 0);
					}
					hm_movie_ratins.put(movie_id, hm_movie_ratins.get(movie_id) + 1);
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Vector<Integer> moviesUnderTreshold = new Vector<Integer>();
		
		Set<Integer> s = hm_movie_ratins.keySet();
		for (Iterator<Integer> iterator = s.iterator(); iterator.hasNext();) {
			int movieID = iterator.next();
			int count = hm_movie_ratins.get(movieID);
			
			if (count < RemoveUsersThreshold.MIN_RATINGSTRESHHOLD){
				moviesUnderTreshold.add(movieID);
			}	
		}
		
		System.out.println("In total movies under threshold: " + moviesUnderTreshold.size());
		
		return moviesUnderTreshold;
	}
	
}
