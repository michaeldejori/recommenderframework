package recommendation.itemfeature;

import java.util.Set;
import java.util.Vector;

public class ItemFeature {

	public static final String DBPEDIA_UNWEIGHTED = "1";
	public static final String DBPEDIA_WEIGHTED = "3";
	public static final String DBPEDIA_COMB_WEI_UNW = "6";
	public static final String FREEBASE_UNWEIGHTED = "2";
	public static final String FREEBASE_WEIGHTED = "4";
	public static final String FREEBASE_COMB_WEI_UNW = "5";
	

	public static String movieDBPediaPredicatesFile = "files/moviePredicatesDBPedia.dat";
	// file moviePredicates, movieURI predicate object
	public static String movieFreebasePredicatesFile = "files/moviePredicatesFB.dat";
	// public static String movieFreebasePredicatesFile = "testfiles/testmov.dat";

	public double getScoreOf(String uri, String feature) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Vector<String> getFeatureOfMovie(String movieURI) {
		System.out.println("hier sollte ich nicht sein");
		return null;
	}

	public int getCommonPredicates(String movieURI1, Set<String> set) {
		return 0;
	}
	
	public Vector<String> getDistinctPredicates() {
		return null;
	}
}
