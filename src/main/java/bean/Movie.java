package bean;

import java.util.Vector;

public class Movie {

	private Vector<Feature> features;
	private String uri;
	private Long movielens_id;
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Long getMovielens_id() {
		return movielens_id;
	}

	public void setMovielens_id(Long movielens_id) {
		this.movielens_id = movielens_id;
	}

	public Vector<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Vector<Feature> features) {
		this.features = features;
	}
	
	

}