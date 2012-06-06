package bean;

public class PrecisionRecall {
	private double precision;
	private double recall;

	public PrecisionRecall(double precision2, double recall2) {
		this.precision = precision2;
		this.recall = recall2;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

}
