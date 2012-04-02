package judge;

import java.io.File;
import java.util.Scanner;

import weka.classifiers.Classifier;

public class Judge {
	private static final String TEST_PATH = "data/datasets/classified/test/";
	private static final String CONF_PATH = "data/conf/conf";
	private static final String JUDGE_COMMAND = "judge";
	private static final String div = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

	private String judgePath;
	private String classFolderRegex = "((pos)*|(neg)*|(all)*)+";
	private static Config conf;
	private static ClassifierHandler handler;

	public Judge(){
		conf = new Config(CONF_PATH);
		handler = new ClassifierHandler();
		handler.setTrainingData(handler.loadDataFromDir(TEST_PATH));
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
			String optString = in.next();
			int option = -1;
			try{
				option = Integer.parseInt(optString);
			} catch(NumberFormatException e){
				if(optString.equals(JUDGE_COMMAND)){
					if(judgePath != null)
						judgeFilmFolder(judgePath);
					else
						p("Set a judge path first!");
				}
				else{
					p(error);
				}
			}
			if(option< -1 | option > 3)
				p(error);
			else{
				if(option == 1){
					p("Avaliable paths in root:");
					p(rootFileStructure());
					p("Which path should I judge?");
					pp(">");
					String path = in.next();
					if(new File(path).exists())
						judgePath = path;
					else
						p("Invalid judement path");
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
					String chosen = in.next();
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
					testAllClassifiers();
				}
			}
		}

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
		return recursiveFileStruct(new File("data/"),0);
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
				"2. Set a new classifier algorithm Or\n"+
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
		classify(-1,film.getAbsolutePath());
	}

	private void classify(double expectedScore, String path){
		boolean hasExpectedScore = expectedScore>0;
		String title = "";
		String filmName = path.substring(path.lastIndexOf(File.separatorChar)+1);
		if(hasExpectedScore){
			title = "Classifying opinions on "+filmName+" with expected score: "+expectedScore;
		}else{
			title = "Classifying opinions on "+filmName+":";
		}
		p(title);
		handler.setTestingData(handler.loadDataFromDir(path));
		ClassificationResult result = handler.classify();
		double medianError = Math.abs((expectedScore-result.median));
		double meanError = Math.abs((expectedScore-result.mean));
		p("Mean: "+result.mean);
		p("Median: "+result.median);
		if(hasExpectedScore){
			p("------------------");
			p("Error:");
			p("Mean: "+meanError);
			p("Median: "+medianError);
		}
		p(div);
	}
	/*
	private void testJudge(){
		String basePath = "datasets/classified/";
		p("Running tests:");
		for(int i=1;i<8;i++){
			classify(i,basePath+(i*10)+"/");
		}
	}*/
	static private void p(String m){
		System.out.println(m);
	}
	static private void pp(String m){
		System.out.print(m);
	}

}
