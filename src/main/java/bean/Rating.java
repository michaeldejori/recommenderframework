package bean;

public class Rating {

	private Long movie_lensID;
	private double rating;
	private Long timestamp;
	
	public Long getMovie_lensID() {
		return movie_lensID;
	}
	public void setMovie_lensID(Long movie_lensID) {
		this.movie_lensID = movie_lensID;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
