import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Test wheter no element in filemodel is same as element with 0
 * 
 * @author michael
 * 
 */
public class TestCosineMahout {
	public static void main(String[] args) {
		DataModel model;
		try {
			File f = new File("testfiles/cosineTest.dat");
			model = new FileDataModel(f);
			UserSimilarity usersim = new UncenteredCosineSimilarity(model);
			double score = usersim.userSimilarity(1, 2);
			System.out.println(score);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
