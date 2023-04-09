//Konstantinos Andreou AM:4316
public class Range {
	
	private double lowerLimit;
	private double upperLimit;
	private int numtuples;
	
	public Range(double lowerLimit, double upperLimit, int numtuples) {
		super();
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.numtuples = numtuples;
	}

	public double getUpperLimit() {
		return upperLimit;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}
	
	public int getNumtuples() {
		return numtuples;
	}
	
	public double getInterval() {
		return upperLimit - lowerLimit;
	}
	
	public String toString() {
		return "range: [" + String.format(java.util.Locale.US, "%.2f", lowerLimit) + "," + String.format(java.util.Locale.US, "%.2f", upperLimit) + "), numtuples: " + numtuples;
	}
}
