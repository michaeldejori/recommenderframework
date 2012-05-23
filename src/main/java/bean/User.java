package bean;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

public class User {

	private String uri;
	private String hetrec_id;
	private Vector<FeatureScore> userprofile = null;
	private Vector<Rating> ratings = null;
	private int amountBackground;
	
	public void sortRatingsToTimestamp(){
		Collections.sort(ratings, new Comparator<Rating>() {
			public int compare(Rating o1, Rating o2) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}
		});
	}
	
	public Vector<Rating> getBackGroundData(double alpha){
		Vector<Rating> backData = new Vector<Rating>();
		double a = alpha * this.ratings.size();
		this.amountBackground = (int)Math.round(a);
		for (int i = 0; i < this.amountBackground; i++) {
			backData.add(this.ratings.get(i));
		}
		return backData;
	}

	public Vector<Rating> getRatingsToEstimate(){
		Vector<Rating> toEstimate = new Vector<Rating>();
		for (int i = this.amountBackground; i < this.ratings.size(); i++){
			toEstimate.add(this.ratings.get(i));
		}
		return toEstimate;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getHetrec_id() {
		return hetrec_id;
	}

	public void setHetrec_id(String hetrec_id) {
		this.hetrec_id = hetrec_id;
	}

	public Vector<FeatureScore> getUserprofile() {
		return userprofile;
	}

	public void setUserprofile(Vector<FeatureScore> userprofile) {
		this.userprofile = userprofile;
	}

	public Vector<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(Vector<Rating> ratings) {
		this.ratings = ratings;
		this.sortRatingsToTimestamp();
	}

}
