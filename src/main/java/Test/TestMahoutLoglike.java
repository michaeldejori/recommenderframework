package Test;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.stats.LogLikelihood;

public class TestMahoutLoglike {

	public static void main(String[] args) {
		Vector<Long> vecint = new Vector<Long>();
		DataModel model;
		try {
			File f = new File("testfiles/cosineTest.dat");
			model = new FileDataModel(f);
			 UserSimilarity usersim = new UncenteredCosineSimilarity(model);
			double score = usersim.userSimilarity(3, 4);
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
