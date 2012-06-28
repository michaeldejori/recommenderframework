package itemWeightsCreationFB;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

public class DeleteWrongStatements {
	private static final String SESAME_SERVER = "http://lokino.sti2.at:8080/openrdf-sesame";
	private static final String REPOSITORY_ID = "fb";
	private static Repository myRepository = null;
	
	public static void main(String[] args){
		myRepository = new HTTPRepository(SESAME_SERVER, REPOSITORY_ID);
		try {
			RepositoryConnection con = myRepository.getConnection();
			Graph myGraph = new org.openrdf.model.impl.GraphImpl();
			ValueFactory myFactory = myGraph.getValueFactory();

			URI myPredicate = myFactory.createURI("<http://knn.michael/20>");

			myGraph.add(null, myPredicate, null);
			
			con.remove(myGraph);
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
