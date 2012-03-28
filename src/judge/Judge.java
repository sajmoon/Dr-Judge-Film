package judge;


public class Judge {
	private static final String BASE_PATH = "datasets/classified/";
	private static final String TEST_PATH = BASE_PATH+"test/";
	private static final String TRAIN_PATH = BASE_PATH+"train/";
	private static ClassifierHandler handler;
	
	public static void main(String[] args){
		new Judge().runJudge();
	}

	public Judge(){
		
	}
	public void runJudge(){
		handler = new ClassifierHandler();
		handler.setTrainingData(handler.loadDataFromDir(TRAIN_PATH));
		handler.setTestingData(handler.loadDataFromDir(TEST_PATH));
		handler.setClassifier(ClassifierHandler.CLASSIF_BAYES_MULTI);
		handler.classify();
		
	}

}
