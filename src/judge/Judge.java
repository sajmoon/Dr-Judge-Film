package judge;

import java.io.File;


public class Judge {
	private static final String BASE_PATH = "datasets/classified/";
	private static final String TEST_PATH = BASE_PATH+"test/";
	private static final String TRAIN_PATH = BASE_PATH+"train/";
	private static final String TEST_70 = BASE_PATH+"70/";
	private static final String TEST_60 = BASE_PATH+"60/"; 
	private static final String TEST_50 = BASE_PATH+"50/"; 
	private static final String TEST_40 = BASE_PATH+"40/"; 
	private static final String TEST_30 = BASE_PATH+"30/"; 
	private static final String TEST_20 = BASE_PATH+"20/"; 
	private static final String TEST_10 = BASE_PATH+"10/"; 
	private static ClassifierHandler handler;
	
	public static void main(String[] args){
		new Judge().runJudge();
	}

	public Judge(){
		
	}
	public void runJudge(){
		handler = new ClassifierHandler();
		handler.setTrainingData(handler.loadDataFromDir(TEST_PATH));
		handler.setClassifier(ClassifierHandler.CLASSIF_BAYES_MULTI);
		
		classify(7,TEST_70);
		classify(6,TEST_60);
		classify(5,TEST_50);
		classify(4,TEST_40);
		classify(3,TEST_30);
		classify(2,TEST_20);
		classify(1,TEST_10);
		
	}
	
	private void classify(double expectedScore, String path){
		p("Classifying reviews with expected score: "+expectedScore);
		handler.setTestingData(handler.loadDataFromDir(path));
		ClassificationResult result = handler.classify();
		double medianError = Math.abs((expectedScore-result.median));
		double meanError = Math.abs((expectedScore-result.mean));
		p("Mean: "+result.mean);
		p("Median: "+result.median);
		p("------------------");
		p("Error:");
		p("Mean: "+meanError);
		p("Median: "+medianError);
		p("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	private void p(String m){
		System.out.println(m);
	}

}
