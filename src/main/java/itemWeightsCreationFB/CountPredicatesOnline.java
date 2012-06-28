package itemWeightsCreationFB;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;

public class CountPredicatesOnline {

	private static double movies_in_store = 10078.0;

	// Repository
	private static final String SESAME_SERVER = "http://lokino.sti2.at:8080/openrdf-sesame";
	// private static final String SESAME_SERVER = "http://localhost:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "fb";
	private static Repository myRepository = null;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		myRepository = new HTTPRepository(SESAME_SERVER, REPOSITORY_ID);

//		Map<String, Integer> objectcount = new HashMap<String, Integer>();
//		Map<String, Integer> predicatecount = new HashMap<String, Integer>();
		Map<String, Integer> predicateobjectcount = new HashMap<String, Integer>();
		Map<String, Integer> predicateobjectcount1 = new HashMap<String, Integer>();

		try {
            RepositoryConnection con = myRepository.getConnection();
            try {
                String queryString = "PREFIX xhtml:<http://www.w3.org/1999/xhtml/vocab#>" +
                		"PREFIX owl:<http://www.w3.org/2002/07/owl#>" +
                		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                		"PREFIX fb:<http://rdf.freebase.com/ns/>" +
                		"PREFIX cc:<http://creativecommons.org/ns#>" +
                		"PREFIX knn:<http://knn.michael2/>" + 
                		"select ?s ?p ?o where {" +
                		"<http://rdf.freebase.com/ns/en.toy_story> ?p ?o. " +
                		"<http://rdf.freebase.com/ns/en.toy_story> knn:20 ?s." + 
                		"?s ?p ?o. " +
                		"?s rdf:type fb:film.film. " +
                		"FILTER((?s != fb:en.beauty_and_the_beast_1991) && (?p != knn:20) && (?p != rdf:type)" +
                		" && (?p != <http://knn.idea/20>)" +
                		")" +
                		"}";
                System.out.println(queryString);
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = tupleQuery.evaluate();
                try {
                    while (result.hasNext()) {
                        BindingSet set = result.next();
                        String s = set.getValue("s").stringValue();
                        String p = set.getValue("p").stringValue();
                        String o = set.getValue("o").stringValue();
                        //System.out.println(s.toString() + "," + p.toString() + "," + o.toString());
 //                  	 Integer objcount = objectcount.get(o);
//                   	 objectcount.put(o, objcount == null ? 1 : objcount + 1);
//                   	 Integer predcount = predicatecount.get(p);
//                     predicatecount.put(p, predcount == null ? 1 : predcount + 1);
                   	 Integer predobjcount = predicateobjectcount.get(p + "\t" + o);
                     predicateobjectcount.put(p + "\t" + o, predobjcount == null ? 1 : predobjcount + 1);
                    }
                } finally {
                    result.close();
                }
            } finally {
                con.close();
            }
            try {
                String queryString = "PREFIX xhtml:<http://www.w3.org/1999/xhtml/vocab#>" +
                		"PREFIX owl:<http://www.w3.org/2002/07/owl#>" +
                		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                		"PREFIX fb:<http://rdf.freebase.com/ns/>" +
                		"PREFIX cc:<http://creativecommons.org/ns#>" +
                		"PREFIX knn:<http://knn.michael2/>" + 
                		"select ?s ?p ?o where {" +
                		"<http://rdf.freebase.com/ns/en.toy_story> ?p ?o. " +
                		"?s ?p ?o. " +
                		"?s rdf:type fb:film.film. " +
                		"FILTER((?s != <http://rdf.freebase.com/ns/en.toy_story>) && (?p != knn:20) && (?p != rdf:type)" +
                		" && (?p != <http://knn.idea/20>)" +
                		")}";
                System.out.println(queryString);
                TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = tupleQuery.evaluate();
                try {
                    while (result.hasNext()) {
                        BindingSet set = result.next();
                        String s = set.getValue("s").stringValue();
                        String p = set.getValue("p").stringValue();
                        String o = set.getValue("o").stringValue();
                        //System.out.println(s.toString() + "," + p.toString() + "," + o.toString());
 //                  	 Integer objcount = objectcount.get(o);
//                   	 objectcount.put(o, objcount == null ? 1 : objcount + 1);
//                   	 Integer predcount = predicatecount.get(p);
//                     predicatecount.put(p, predcount == null ? 1 : predcount + 1);
                   	 Integer predobjcount1 = predicateobjectcount1.get(p + "\t" + o);
                     predicateobjectcount1.put(p + "\t" + o, predobjcount1 == null ? 1 : predobjcount1 + 1);
                    }
                } catch (Exception e) {
                	e.printStackTrace();
                } finally {
                    result.close();
                }

            } catch (Exception e) {
            	e.printStackTrace();
            	
            } finally {
                con.close();
            }
        } catch (OpenRDFException e) {
            e.printStackTrace();
        }
        
//      Set<String> keys = objectcount.keySet();
//      for (String string : keys) {
//		int i = objectcount.get(string);
//		{ System.out.println(i + "\t" + string); }
//      }
//      System.out.println("#############################################");
//      Set<String> keys2 = predicatecount.keySet();
//      for (String string : keys2) {
//		int i = predicatecount.get(string);
//		{ System.out.println(i + "\t" + string); }
//      }
      System.out.println("#############################################");
      Set<String> keys3 = predicateobjectcount.keySet();
      TreeMap<Double, String> map = new TreeMap<Double, String>();
      for (String string : keys3) {
		int i = predicateobjectcount.get(string);
		int j = predicateobjectcount1.get(string);
		double value = i * Math.log(movies_in_store / ((double) j));
		map.put(value, string);
      }
      Set<Double> key = map.descendingKeySet();
      for (Double double1 : key) {
    	double double2 = (double) Math.round(double1 * 100) / 100.0;
		System.out.println(double2 + "\t"+ map.get(double1));
	}
      
      
	}

}
