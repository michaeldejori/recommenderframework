package recommendation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import Test.Spearman;

public class SpearmansCorrelation {
	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1; // Special fix to preserve items with equal values
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	private static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		Set<T> keys = new HashSet<T>();
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	/**
	 * Computes the Spearman Correlation of to Users/Scores
	 * 
	 * @param m1
	 *            HashMap(String, Double) ranked scores 1
	 * @param m2
	 *            HashMap(String, Double) ranked scores 2
	 * @return value from [-1, 1] where <0 negativer Zusammenhang >0 positiver Zusammenhang, 0 kein Zusammenhang
	 */
	public static double computeSpearmanCorr(HashMap<String, Double> m1, HashMap<String, Double> m2) {

		HashMap<String, Integer> rankingHashMap = computeRanking(m1);
		HashMap<String, Double> normalizedRanking = computeRankingNormalized(m1, rankingHashMap);

		HashMap<String, Integer> rankingHashMap2 = computeRanking(m2);
		HashMap<String, Double> normalizedRanking2 = computeRankingNormalized(m2, rankingHashMap2);

		double sumsquaredDiff = computeSumOfSquaredDifference(normalizedRanking, normalizedRanking2);

		// Attention if only (1 and 1 element, then return NaN)
		double res = 1 - (6 * sumsquaredDiff / (rankingHashMap.size() * (Math.pow(rankingHashMap.size(), 2) - 1)));
		return res;
	}

	/**
	 * computes the sum of the squared difference of the to Rankings
	 * 
	 * @param normalizedRanking
	 *            normalized rankings with average rankings
	 * @param normalizedRanking2
	 *            normalized rankings with average rankings
	 * @return
	 */
	private static double computeSumOfSquaredDifference(HashMap<String, Double> normalizedRanking,
			HashMap<String, Double> normalizedRanking2) {
		double sum = 0;
		for (String s : normalizedRanking.keySet()) {
			sum += Math.pow((normalizedRanking.get(s) - normalizedRanking2.get(s)), 2);
		}
		return sum;
	}

	/**
	 * Computes the rankings according to the scores
	 * 
	 * @param m1
	 *            HashMap String, score
	 * @return HasMap String Ranking
	 */
	private static HashMap<String, Integer> computeRanking(HashMap<String, Double> m1) {
		int rank = 1;
		HashMap<String, Integer> rankedHashMap = new HashMap<String, Integer>();
		for (Entry<String, Double> entry : entriesSortedByValues(m1)) {
			rankedHashMap.put(entry.getKey(), rank);
			rank++;
		}
		return rankedHashMap;
	}

	/**
	 * computest the normalized Rankings with average if items have same score
	 * 
	 * @param m1
	 *            HashMap User Scores for determining if user/items have same scores
	 * @param rankingHashMap
	 *            HashMap User rankings
	 * @return normalized ranking with average
	 */
	private static HashMap<String, Double> computeRankingNormalized(HashMap<String, Double> m1,
			HashMap<String, Integer> rankingHashMap) {
		HashMap<String, Double> rangnorm = new HashMap<String, Double>();
		// iterate through items
		for (String s : m1.keySet()) {
			Double scoreOfItem = m1.get(s);
			Set<String> se1 = Spearman.getKeysByValue(m1, scoreOfItem);

			double sumup = 0;
			double count = 0;

			for (Iterator<String> it = se1.iterator(); it.hasNext();) {
				String movie = it.next();
				sumup += rankingHashMap.get(movie);
				count++;
			}
			double averageRang = sumup / count;

			for (Iterator<String> it = se1.iterator(); it.hasNext();) {
				String movie = it.next();
				if (!rangnorm.containsKey(movie)) {
					rangnorm.put(movie, averageRang);
				}
			}
		}
		return rangnorm;
	}

}
