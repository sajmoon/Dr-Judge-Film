package judge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Judge {
	private static final String TEST_PATH = "datasets/classified/test/";
	private static final String TRAIN_PATH = "datasets/classified/train/";
	private static final String FULL_TRAIN_PATH = "datasets/combinedTrainingData/";
	private static final String ARFF_PATH = "datasets/sentences/ARFF/sentenceDataset.arff";
	private static final String CONF_PATH = "datasets/conf/";
	private static final String REV_PATH = "DataDownloader/data/";
	private static final String JUDGE_COMMAND = "judge";
	private static final String div = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

	private String judgePath;
	private String classFolderRegex = "((pos)*|(neg)*|(all)*|(imdb)*|(twitter)*)+";
	private static Config conf;
	private static ClassifierHandler handler;

	public Judge(){
		conf = new Config(CONF_PATH);
		handler = new ClassifierHandler();
		handler.setTrainingData(handler.loadDataFromDir(TRAIN_PATH));
		String classif = conf.readClassifierToUse();
		setClassifier(classif);
	}

	public static void main(String[] args){
		new Judge().showMenu();
	}

	private void showMenu() {
		Scanner in = new Scanner(System.in);
		String error = "That's not a judgemental command. Be more judgemental!";
		boolean running = true;
		while(running){
			p(mainMenu());
			pp(">");
			String optString = in.nextLine();
			int option = -1;
			try{
				option = Integer.parseInt(optString);
			} catch(NumberFormatException e){
				if(optString.startsWith(JUDGE_COMMAND)){
					if(judgePath != null){
						judgeFilmFolder (judgePath);
					}
					else
						p("Set a judge path first!");
				}
				else{
					p(error);
				}
			}
			if(option< -1 | option > 4)
				p(error);
			else{
				if(option == 1){
					p("Avaliable paths in root:");
					p(rootFileStructure());
					p("Which path should I judge?");
					pp(">");
					String path = in.nextLine();
					path = REV_PATH+path+"/";
					if(new File(path).exists())
						judgePath = path;
					else
						p("Invalid judement path: "+path);
				}
				if(option == 2){
					Classifier[] classifiers = ClassifierHandler.USABLE_ALGOS;
					String[] extra = ClassifierUtils.getExtraInfo();
					for(int i=1;i<=classifiers.length;i++){
						String className = classifiers[i-1].getClass().getSimpleName();
						p(""+i+". "+className+" "+extra[i-1]);
					}

					p("Choose one. Choose wisely:");
					pp(">");
					String chosen = in.nextLine();
					int choice = 0;
					try{
						choice = Integer.parseInt(chosen)-1;
					} catch(NumberFormatException e){
						//Do nothing. let choice stay 0
					}
					String chosenClassName = classifiers[choice].getClass().getSimpleName();
					setClassifier(chosenClassName);
				}
				if(option== 3){
					evaluateCurrentClassifier();
				}
				if(option==4){
					p("Which one?");
					p("1. Reviews");
					p("2. Sentences");
					pp(">");
					String chosen = in.nextLine();
					int choice = 0;
					try{
						choice = Integer.parseInt(chosen);
					} catch(NumberFormatException e){
						//Do nothing. let choice stay 0
					}
					if(choice==1){
						useReviewTraining();
						p("Using imdb reviews as training data");
					}
					if(choice==2){
						useSentencesTraining();
						p("Using sentences as training data");
					}

				}
			}
		}

	}
	private void useSentencesTraining(){
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(ARFF_PATH));
			Instances data = new Instances(reader);
			reader.close();
			// set class attribute, must be done :((
			data.setClassIndex(data.numAttributes() - 1);

			handler.setTrainingData(data, data);
			handler.setClassifier(handler.ALGO_USED);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void useReviewTraining(){
		handler.setTrainingData(handler.loadDataFromDir(TRAIN_PATH));
		handler.setClassifier(handler.ALGO_USED);
	}
	private void testAllClassifiers() {
		if(judgePath != null){
			for(Classifier c : handler.USABLE_ALGOS){
				String name = c.getClass().getSimpleName();
				p("Using classifier: "+name);
				setClassifier(name);
				judgeFilmFolder(judgePath);
			}
		}else{
			p("No judge path");
		}


	}

	private String rootFileStructure(){
		return recursiveFileStruct(new File(REV_PATH),0);
	}

	private String recursiveFileStruct(File dir, int level){
		File[] list = dir.listFiles();
		StringBuilder sb = new StringBuilder();
		String tabs = "";
		for(int i=0;i<level;i++)
			tabs += "\t";
		sb.append(tabs+dir.getName()+":\n");
		if(list != null && list.length > 0){
			for(File f : list){
				boolean isClassFolder = f.getName().matches(classFolderRegex);
				if(!isClassFolder){
					if(f.isDirectory()){
						//Append files from lower down
						sb.append(recursiveFileStruct(f,level+1));
					}else{
						sb.append(tabs+"\t"+f.getName()+"\n");
					}
				}
			}
		}
		return sb.toString();
	}

	private String mainMenu(){
		return "Court now in session with "+handler.ALGO_USED+"\n"+
				"All rise for the honorable judge Dr Film.\n" +
				"Select a judgemental action:\n" +
				"1. Set judgement path\n" +
				"2. Set a new classifier algorithm\n"+
				"3. Evaluate the current algorithm\n"+
				"4. Change training data\n"+
				"type "+JUDGE_COMMAND+" to judge the shit out of the path";
	}

	private static void setClassifier(String classifier){
		handler.setClassifier(classifier);
		conf.writeCurrentConf(handler.ALGO_USED);
	}

	public void judgeFilmFolder(String path){
		File dir = new File(path);
		if(!dir.isDirectory()){
			p(path+" is not a directory");
			return;
		}
		File[] films = dir.listFiles();
		boolean isFilmDirectory = false;
		for(File f : films){
			//Is this directory a directory of films, or
			// does it contain one film?
			isFilmDirectory = f.getName().matches(classFolderRegex);
		}
		if(isFilmDirectory){
			judgeFilm(dir);
		}
		else{
			for(File film : films){
				judgeFilm(film);
			}
		}
	}

	public void judgeFilm(File film){
		p("Judging film: "+film.getName());
		int numReviews = numReviews(film);
		try {
			classify(-1,film.getAbsolutePath(), numReviews);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void classify(double expectedScore, String path, int numReviews) throws Exception{
		boolean hasExpectedScore = expectedScore>0;
		String title = "";
		String filmName = path.substring(path.lastIndexOf(File.separatorChar)+1);
		if(hasExpectedScore){
			title = "Classifying "+numReviews+" opinions on "+filmName+" with expected score: "+expectedScore;
		}else{
			title = "Classifying "+numReviews+" opinions on "+filmName+":";
		}
		p(title);
		Instances dataset = handler.loadDataFromDir(path).data;
		handler.setTestingData(dataset);
		ClassificationResult result = handler.classify();
		double medianError = Math.abs((expectedScore-result.median));
		double meanError = Math.abs((expectedScore-result.mean));
		p("Mean: "+doubleToOneDecimal(result.mean));
		p("Median: "+doubleToOneDecimal(result.median));
		if(hasExpectedScore){
			p("------------------");
			p("Error:");
			p("Mean: "+meanError);
			p("Median: "+medianError);
		}
		p(div);
	}
	private String doubleToOneDecimal(double d){
		if(Double.toString(d).length() < 4){
			return Double.toString(d);
		}
		String twoDecimals = Double.toString(d).substring(0,4);
		String integer = twoDecimals.substring(0,1);
		String firstDecimal = Double.toString(d).substring(2, 3);
		String lastDecimal = twoDecimals.substring(3);
		int lastDec = Integer.parseInt(lastDecimal);
		if(lastDec < 5)
			return integer+"."+firstDecimal;
		else{
			int newFirstDecimal = (Integer.parseInt(firstDecimal))+1;
			if(newFirstDecimal >9 ){
				newFirstDecimal = 0;
				integer = Integer.toString((Integer.parseInt(integer)+1));
			}
			return integer+"."+newFirstDecimal;
		}
	}

	private void evaluateCurrentClassifier(){
		try {
			if(!handler.isTestdataSet()){
				handler.setTestingData(handler.loadDataFromDir(TEST_PATH));
			}
			Evaluation e = handler.evaluateModel(handler.testDataset);
			p("");
			p("Class details:\n"+e.toClassDetailsString());
			p(div);
			p(e.toSummaryString("Test data:", true));
			p(div);
			p("confusion matrix:");
			p(e.toMatrixString());
			p(div);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private int numReviews(File film){
		String[] revs = film.listFiles()[0].list();
		return revs.length;
	}
	static private void p(String m){
		System.out.println(m);
	}
	static private void pp(String m){
		System.out.print(m);
	}

}
