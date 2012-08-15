package Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

public class Spearman {

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

	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		Set<T> keys = new HashSet<T>();
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	public static void main(String[] args) {
		
		/*
		 * m1.put("1", new Double(5)); m1.put("2", new Double(5)); m1.put("3", new Double(4)); m1.put("4", new
		 * Double(3)); m1.put("5", new Double(2)); m1.put("6", new Double(4)); m1.put("7", new Double(5)); m1.put("8",
		 * new Double(1));
		 */
		
		HashMap<String, Double> m1 = new HashMap<String, Double>();
		m1.put("1", new Double(2));
		m1.put("2", new Double(3));
		m1.put("3", new Double(3));
		m1.put("4", new Double(4));
		
		HashMap<String, Double> m2 = new HashMap<String, Double>();
		m2.put("1", new Double(0.16));
		m2.put("2", new Double(0.22));
		m2.put("3", new Double(0.55));
		m2.put("4", new Double(0.6));

		double spearman = computeSpearmanCorr(m1, m2);
		System.out.println(spearman);

	}

	private static double computeSpearmanCorr(HashMap<String, Double> m1, HashMap<String, Double> m2) {

		HashMap<String, Integer> rankingHashMap = computeRanking(m1);
		HashMap<String, Double> normalizedRanking = computeRankingNormalized(m1, rankingHashMap);
		
		HashMap<String, Integer> rankingHashMap2 = computeRanking(m2);
		HashMap<String, Double> normalizedRanking2 = computeRankingNormalized(m2, rankingHashMap2);
		
		double sumsquaredDiff = computeSumOfSquaredDifference(normalizedRanking, normalizedRanking2);
		
		System.out.println("sumsqaredDiff: " +sumsquaredDiff);
		
		double res = 1 - (6*sumsquaredDiff/(rankingHashMap.size()*(Math.pow(rankingHashMap.size(), 2) - 1)));
		return res;

	}

	private static double computeSumOfSquaredDifference(HashMap<String, Double> normalizedRanking,
			HashMap<String, Double> normalizedRanking2) {

		double sum = 0;
		for (String s : normalizedRanking.keySet()) {
			sum += Math.pow((normalizedRanking.get(s) - normalizedRanking2.get(s)), 2);
		}
		
		return sum;
	}

	private static HashMap<String, Integer> computeRanking(HashMap<String, Double> m1) {
		int rank= 1;
		HashMap<String, Integer> rankedHashMap = new HashMap<String, Integer>();
		

		for (Entry<String, Double> entry : entriesSortedByValues(m1)) {
			rankedHashMap.put(entry.getKey(), rank);
			rank++;
		}
		return rankedHashMap;
	}

	/**
	 * computes for a Hashmap item-score ()
	 * 
	 */
	private static HashMap<String, Double> computeRankingNormalized(HashMap<String, Double> m1, HashMap<String, Integer> rankingHashMap) {
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
					System.out.println("NormNorm: " + movie + "  ->  " + averageRang);
				}
			}

		}

		return rangnorm;
	}

}
