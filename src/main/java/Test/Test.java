package Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.math.DenseVector;
import org.jfree.util.Configuration;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;


public class Test {

	public static void main(String[] args) throws IOException, TasteException {
	
		DenseVector dV = new DenseVector(new double[] {2.0,3.0,4.0});
		DenseVector dV2 = new DenseVector(new double[] {2.0,3.0,4.0});
		
		FastByIDMap<PreferenceArray> userData = new FastByIDMap<PreferenceArray>();
		//userData.put("a", new PreferenceArray);
		DataModel dm = new GenericDataModel(userData);
		
	}
}
