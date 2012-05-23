package bean;

public class Feature {

	private String predicate;
	private String object;
	
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	
	public String toString(){
		return predicate + ":" + object;
	}
	
}
