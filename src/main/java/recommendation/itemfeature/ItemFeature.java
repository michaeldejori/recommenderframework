package recommendation.itemfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import recommendation.ItemFeatureMatrix;

public class ItemFeature {
	
	public static String movieDBPediaPredicatesFile = "files/moviePredicatesDBPedia.dat";
	// file moviePredicates, movieURI predicate object
	public static String movieFreebasePredicatesFile = "files/moviePredicates.dat";
	
	
	public double getScoreOf(String uri, String feature) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Vector<String> getFeatureOfMovie(String movieURI){
		System.out.println("hier sollte ich nicht sein");
		return null;
	}

	public static Vector<String> getDistinctPredicates(){
		Vector<String> vec = new Vector<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(
						UnweightedItemFeature.movieDBPediaPredicatesFile)));

			if (reader != null) {
				String line = reader.readLine();

				// index for hashmap and then matrix
				int i = 1;

				while (line != null) {

					String[] parts = line.split("\t");
					// <sub> <pred> <ob>
					if (parts.length == 3) {
						String p = parts[1];

						String newP = p.replace("<", "");
						newP = newP.replace(">", "");

						if (!vec.contains(newP))
							vec.add(newP);
					}
					line = reader.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vec;
	}
}
