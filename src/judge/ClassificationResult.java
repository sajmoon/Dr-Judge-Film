package judge;

public class ClassificationResult {
	public double high;
	public double low;
	public double mean;
	public double median;
	public double expected;
	public double imdbScore;
	public String title;
	
	public ClassificationResult(){
		
	}
	public ClassificationResult(double imdb, double mean, double median, String title){
		this.title = title;
		this.mean = mean;
		this.median = median;
		this.imdbScore = imdb;
	}
	
	public String toString(){
		String matrixRow = title.substring(0, 8) + ":\t\t"+
				Judge.doubleToOneDecimal(imdbScore)+"\t\t"+
				Judge.doubleToOneDecimal(mean)+"\t\t"+
				Judge.doubleToOneDecimal(median)+"\t\t"+
				Judge.doubleToOneDecimal(Math.abs(imdbScore-mean))+"\t\t"+
				Judge.doubleToOneDecimal(Math.abs(imdbScore-median))+"\t\t";
				
		return matrixRow;
				
	}
	
	public static String getMatrixHeader(){
		return "Title\t\tIMDB score\t\tMean\t\t" +
				"Median\t\tMeanError\t\tMedError\t\t";
	}

}
