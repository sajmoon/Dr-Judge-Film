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
	private static final String OPT_TWEET = "-t";
	private static final String OPT_CLASSIFIER = "-cl";
	private static final String OPT_HELP = "-help";
	private static final String[] OPTS = {OPT_TWEET,OPT_CLASSIFIER,OPT_HELP};
	private static final String CONTENT_TWEET = "tweets";
	private static final String CONTENT_BLOG = "blog reviews";
	private static final String CONTENT_IMDB = "IMDB comments";
	private static final String CONTENT_EMPTY = "";
	private static final String div = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
	private static final boolean DEBUG = true;
	private static ClassifierHandler handler;

	public static void main(String[] args){
		if(DEBUG){
			testTweets();
		}else{
			String option = "";
			Judge judge = new Judge();
			if(args.length <= 0)
				judge.runJudge();
			else{
				option = args[0];
				if(option.equals(OPT_TWEET)){
					if(args.length > 1)
						judge.judgeTweets(args[1]);
					else{
						p("No tweet path specified. go to hell");
						return;
					}
				}
				if(option.equals(OPT_HELP)){
					p("Avaliable classifiers:");
					for(String s : handler.USABLE_ALGOS){
						p(s);
					}
					p("Availiable options:");
					for(String s : OPTS){
						p(s);
					}

				}



			}
		}


	}

	private static void testTweets() {
		String path = "data/tweets/";
		new Judge().judgeTweets(path);

	}

	public Judge(){
		handler = new ClassifierHandler();
		handler.setTrainingData(handler.loadDataFromDir(TEST_PATH));
		handler.setClassifier(ClassifierHandler.CLASSIF_BAYES_MULTI);
	}

	public void judgeTweets(String tweetspath){
		File tweetDir = new File(tweetspath);
		if(!tweetDir.isDirectory()){
			p(tweetspath+" is not a directory");
			return;
		}
		File[] films = tweetDir.listFiles();
		p("Judging the following films:");
		for(File f : films)
			p(f.getName());
		for(File dir : films){
			p("Judging film: "+dir.getName());
			classify(-1,dir.getAbsolutePath(),CONTENT_TWEET);
		}



	}
	public void runJudge(){
		p("No arguments given. Running tests:");
		classify(7,TEST_70,CONTENT_IMDB);
		classify(6,TEST_60,CONTENT_IMDB);
		classify(5,TEST_50,CONTENT_IMDB);
		classify(4,TEST_40,CONTENT_IMDB);
		classify(3,TEST_30,CONTENT_IMDB);
		classify(2,TEST_20,CONTENT_IMDB);
		classify(1,TEST_10,CONTENT_IMDB);

	}

	private void classify(double expectedScore, String path, String content){
		boolean hasExpectedScore = expectedScore>0;
		String title = "";
		String filmName = path.substring(path.lastIndexOf(File.separatorChar)+1);
		if(hasExpectedScore){
			title = "Classifying "+content+" about "+filmName+" with expected score: "+expectedScore;
		}else{
			title = "Classifying "+content+" about "+filmName+":";
		}
		p(title);
		handler.setTestingData(handler.loadDataFromDir(path));
		ClassificationResult result = handler.classify();
		double medianError = Math.abs((expectedScore-result.median));
		double meanError = Math.abs((expectedScore-result.mean));
		p("Mean: "+result.mean);
		p("Median: "+result.median);
		p("------------------");
		if(hasExpectedScore){
			p("Error:");
			p("Mean: "+meanError);
			p("Median: "+medianError);
		}
		p(div);
	}
	static private void p(String m){
		System.out.println(m);
	}

}
