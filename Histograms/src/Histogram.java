//Konstantinos Andreou AM:4316
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Histogram {

	private static final String field = "Income";
	private static final String csvFile = "acs2015_census_tract_data.csv";
	private int bins = 100;
	
	public void produceHistograms(double a, double b) {
		try {  
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			String[] firstLine = br.readLine().split(",");
			int fieldPosition = findFieldPosition(firstLine);
			String line;
			String[] lineValues;
			ArrayList<Double> fieldValues = new ArrayList<Double>(0); 
			while ((line = br.readLine()) != null) {
				lineValues = line.split(",");
				if (isDouble(lineValues[fieldPosition])) {
					fieldValues.add(Double.parseDouble(lineValues[fieldPosition]));
				}
			}
			System.out.println(fieldValues.size() + " valid " + field + " values");
			Collections.sort(fieldValues);
			ArrayList<Range> widthRanges = produceEquiWidth(fieldValues);
			ArrayList<Range> depthRanges = produceEquiDepth(fieldValues);
			System.out.println("\nequiwidth estimated results: " + computeEstimationResults(widthRanges, a, b));
			System.out.println("equidepth estimated results: " + computeEstimationResults(depthRanges, a, b));
			System.out.println("actual results: " + computeActualResults(fieldValues, a, b));
			benchmark(widthRanges, depthRanges, fieldValues);
			br.close();
		} catch (IOException e) {  
			e.printStackTrace();
		}
	}
	
	public int findFieldPosition(String[] firstLine) {
		for (int i = 0; i < firstLine.length; i++) {
			if (firstLine[i].equals(field))
				return i;
		}
		System.out.println("There is no field with the given field name");
		return -1;
	}
	
	public boolean isDouble(String value) {
	    try {
	        Double.parseDouble(value);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	public ArrayList<Range> produceEquiWidth(ArrayList<Double> fieldValues){
		ArrayList<Range> ranges = new ArrayList<Range>(0);
		double minValue = fieldValues.get(0);
		double maxValue = fieldValues.get(fieldValues.size()-1);
		double interval = (maxValue - minValue) / bins;
		System.out.println("minimum " + field + " = " + minValue + " maximum " + field + " = " + maxValue);
		System.out.println("equiwidth:");
		for (int i = 0; i < bins; i++) {
			int numtuples = 0;
			for (int j = 0; j < fieldValues.size(); j++) {
				if ((minValue <= fieldValues.get(j)) && (fieldValues.get(j) < minValue+interval)) {
					numtuples++;
				}
			}
			Range range = new Range(minValue, minValue+interval, numtuples);
			ranges.add(range);
			System.out.println(range.toString());
			minValue = minValue+interval;
		}	
		return ranges;
	}
	
	public ArrayList<Range> produceEquiDepth(ArrayList<Double> fieldValues) {
		ArrayList<Range> ranges = new ArrayList<Range>(0);
		System.out.println("\nequidepth:");
		int numtuples = fieldValues.size() / bins;
		int i;
		for (i = 0; i < fieldValues.size()-numtuples; i+=numtuples) {
			Range range = new Range(fieldValues.get(i), fieldValues.get(i+numtuples), numtuples);
			ranges.add(range);
			System.out.println(range.toString());
		}
		Range range = new Range(fieldValues.get(i), fieldValues.get(fieldValues.size()-1), fieldValues.size()-i);
		ranges.add(range);
		System.out.println(range.toString());
		return ranges;
	}
	
	public int computeActualResults(ArrayList<Double> fieldValues, double a, double b) {
		int counter = 0;
		for (int i = 0; i < fieldValues.size(); i++) {
			if ((fieldValues.get(i) >= a) && (fieldValues.get(i) < b)) {
				counter++;
			}
		}
		return counter;
	}
	
	public double computeEstimationResults(ArrayList<Range> ranges, double a, double b) {
		double results = 0;
		for (int i = 0; i < ranges.size(); i++) {
			if (ranges.get(i).getLowerLimit() <= a && a < ranges.get(i).getUpperLimit()) {
	            results += ((ranges.get(i).getUpperLimit()-a) / ranges.get(i).getInterval()) * ranges.get(i).getNumtuples();
	        } else if (ranges.get(i).getLowerLimit() <= b && b < ranges.get(i).getUpperLimit()) {
	            results += ((b - ranges.get(i).getLowerLimit()) / ranges.get(i).getInterval()) * ranges.get(i).getNumtuples();	 
	        } else if ((ranges.get(i).getLowerLimit()) >= a && (ranges.get(i).getUpperLimit() <= b)) {
				results += ranges.get(i).getNumtuples();
			}
		}
		return results;
	}
	
	public void benchmark(ArrayList<Range> widthRanges, ArrayList<Range> depthRanges, ArrayList<Double> fieldValues) {
		int equiWidthWins = 0;
		int equiDepthWins = 0;
		Random r = new Random();
		for (int i = 0; i < 100; i++) {
			double a = 2611.0 + (248750.0 - 2611.0) * r.nextDouble();
			double b = a + (248750.0 - a) * r.nextDouble();
			double actualResult = computeActualResults(fieldValues, a, b);
			double equiWidthDistance = Math.abs(computeEstimationResults(widthRanges, a, b) - actualResult);
			double equiDepthDistance = Math.abs(computeEstimationResults(depthRanges, a, b) - actualResult);
			if (equiWidthDistance < equiDepthDistance) {
				equiWidthWins++;
			} else if (equiWidthDistance > equiDepthDistance) {
				equiDepthWins++;
			}
		}
		System.out.println("\nBenchmarking the two different Histograms(100 Iterations):");
		if (equiWidthWins > equiDepthWins) {
			System.out.println("EquiWidth Histogram was the winner!Wins: " + equiWidthWins);
		} else {
			System.out.println("EquiDepth Histogram was the winner!Wins: " + equiDepthWins);
		}	
	}
	
	public static void main(String args[]) {
		Histogram histogram = new Histogram();
		histogram.produceHistograms(19000, 55000);
	}
}
