package recommendation;
import java.util.Vector;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;


public class EndpointConnector {
	// repository
	private static final String SESAME_SERVER = "http://rdf.sti2.at:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "fb";
	// private static final String SESAME_SERVER = "http://localhost:8080/openrdf-sesame";
	// private static final String REPOSITORY_ID = "lmovfreebase";
	
	private Repository repository;
	
	public EndpointConnector(){
		this.repository = new HTTPRepository(EndpointConnector.SESAME_SERVER, EndpointConnector.REPOSITORY_ID);
	}
	
	private String createFilterRule(){
		String filter = "?pred != fb:type.object.key && ";
		filter += "?pred != <http://rdf.freebase.com/ns/base.greatfilms.topic.webpage> && ";
		filter += "?pred != <http://rdf.freebase.com/ns/common.topic.topical_webpage> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/film.film.other_crew> &&";
		filter += "?pred != <http://creativecommons.org/ns#attributionURL> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/user.mikeshwe.default_domain.videosurf_card.videosurf_link_text> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/base.schemastaging.topic_extra.review_webpage> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/common.topic.image> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/common.topic.official_website> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/common.topic.webpage> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/film.film.starring> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/type.object.name> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/film.film.initial_release_date> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/film.film.sequel> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/user.alust.default_domain.processed_with_review_queue.internal_question_id> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/dataworld.gardening_hint.split_to> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/film.film.tagline> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/film.film.runtime> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/film.film.release_date_s> &&";
		filter += "?pred != <http://rdf.freebase.com/ns/base.wfilmbase.film.w_id> &&";
		filter += "?pred != <http://www.w3.org/2002/07/owl#sameAs>";
		
		
		
		return filter;
	}
	
	
	/**
	 * 
	 * 
	 * @param movieURI for which the predicates will be retrieved
	 * @return String Vector with entry <movieURI> \t <predicate> \t <object>
	 */
	public Vector<String> retrievePredicatesOfMovie(String movieURI) {

		Vector<String> v = null;
		RepositoryConnection con;
		try {
			con = this.repository.getConnection();
			String queryString = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX fb:<http://rdf.freebase.com/ns/>"
					+ "Select distinct ?pred ?ob "
					+ "where {"
					+ movieURI
					+ " ?pred ?ob." + " filter(" + this.createFilterRule() + ")}";

			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			v = new Vector<String>();

			while (result.hasNext()) {
				BindingSet set = result.next();
				String pred = set.getValue("pred").stringValue();
				String ob = set.getValue("ob").stringValue();
				v.add(movieURI + "\t" + "<" + pred + ">" + "\t" + "<"+ ob + ">");
			}
			result.close();
			con.close();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		System.out.println("Finished processing: " + movieURI);
		return v;
	}
	
	/**
	 * retrieve entire feature set of all movies
	 * Note!!!! Last attempt, it was to slow, after 3 hours I stopped 
	 *
	 */
	private void retrievefeatureSetOfMovies() {
		RepositoryConnection con;
		try {
			con = this.repository.getConnection();
			String queryString = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX fb:<http://rdf.freebase.com/ns/>"
					+ "Select distinct ?pred ?ob "
					+ "where {"
					+ " ?film rdf:type fb:film.film."
					+ " ?film ?pred ?ob."
					+ " filter(fb:type.object.key != ?pred)}";

			System.out.println(queryString);
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			int i = 0;
			while (result.hasNext()) {
				BindingSet set = result.next();
				String pred = set.getValue("pred").stringValue();
				String ob = set.getValue("ob").stringValue();
				System.out.println(i++ + ": " + pred.toString() + ","
						+ ob.toString());
			}
			result.close();
			con.close();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
}
