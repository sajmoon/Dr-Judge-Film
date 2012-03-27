package judge;

import java.io.File;
import java.io.IOException;


import weka.classifiers.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.misc.VFI;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.DecisionStump;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class ClassifierTest {
	Instances testDataset;
	Instances trainDataset;
	Instances testStructure;
	Instances trainStructure;
	boolean DEBUGGING = true;
	private static final String BASE_PATH = "datasets/classified/";
	private static final String TEST_PATH = "test/";
	private static final String TRAIN_PATH = "train/";
	private String div = "-----------------------------------";
	
	public static void main(String[] args){
		ClassifierTest jodu = new ClassifierTest(BASE_PATH+TEST_PATH, BASE_PATH+TRAIN_PATH);
		try {
			jodu.formatData();
			jodu.runTests();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void runTests() throws Exception {
		/*evaluateModel(new NaiveBayes(), "Naive bayes");
		evaluateModel(new DecisionStump(), "Decision Stump");
		evaluateModel(new VFI(), "VFI");
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		mlp.setHiddenLayers("5");
		evaluateModel(mlp, "MLP");
		evaluateModel(new SMO(), "Support Vector lol");*/
		evaluateModel(new NaiveBayesMultinomial(), "Naive bayes multinomial");
	}

	public ClassifierTest(String testDir, String trainDir){
		TextDirectoryLoader testLoader = new TextDirectoryLoader();
		TextDirectoryLoader trainLoader = new TextDirectoryLoader();
		try {
			testLoader.setDirectory(getFileDir(testDir));
			testDataset = testLoader.getDataSet();
			testStructure = testLoader.getStructure();
			
			trainLoader.setDirectory(getFileDir(trainDir));
			trainDataset = trainLoader.getDataSet();
			trainStructure = trainLoader.getStructure();
		} catch (IOException e) {
			p("Couldn't load the specified dir");
			e.printStackTrace();
		}
		
	}
	public void formatData() throws Exception{
		StringToWordVector filter = new StringToWordVector();
		Stemmer stemmer = new LovinsStemmer();
		filter.setStemmer(stemmer);
		filter.setIDFTransform(true);
		filter.setInputFormat(trainStructure);
		trainDataset = Filter.useFilter(trainDataset, filter);
		filter.setInputFormat(testStructure);
		testDataset = Filter.useFilter(testDataset, filter);
		
	}
	
	private void evaluateModel(Classifier classifier, String className) throws Exception{
		p("Classification with algo: "+className+":");
		classifier.buildClassifier(trainDataset);
		p("Built classifier");
		Evaluation eval = new Evaluation(trainDataset);
		eval.evaluateModel(classifier, testDataset);
		p("");
		p("Class details:\n"+eval.toClassDetailsString());
		p(div);
		p("Test data:\n"+eval.toSummaryString("jodu", true));
		p(div);
	}
	

	private File getFileDir(String dir) {
		File file = new File(dir);
		if (file.exists() && file.isDirectory())
			return file;
		
		return null;
	}
	
	private void p(String m){
		if(DEBUGGING)
			System.out.println(m);
	}
	private void pp(String m){
		if(DEBUGGING)
			System.out.print(m);
	}

}
