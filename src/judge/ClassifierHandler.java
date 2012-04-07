package judge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import judge.content.Dataset;
import judge.content.RandomDatasetProvider;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.AODE;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.ComplementNaiveBayes;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.classifiers.functions.LeastMedSq;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.PaceRegression;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LBR;
import weka.classifiers.misc.HyperPipes;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.NNge;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.Prism;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.Id3;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.lmt.LogisticBase;
import weka.classifiers.trees.m5.M5Base;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.stemmers.LovinsStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class ClassifierHandler {
	Instances testDataset;
	Instances trainDataset;
	Instances trainStructure;
	Classifier classifier;
	String ALGO_USED;
	public HashMap<String,Classifier> classifiers;
	StringToWordVector filter;
	boolean DEBUGGING = true;
	public static final Classifier[] USABLE_ALGOS = ClassifierUtils.getClassifiers();

	private static final String div = "-----------------------------------";
	private static int NEG_CLASS = 0;
	private static int POS_CLASS = 1;
	
	public ClassifierHandler(){
		classifiers = new HashMap<String,Classifier>();
		for(Classifier c : USABLE_ALGOS){
			classifiers.put(c.getClass().getSimpleName(), c);
		}
		
		filter = new StringToWordVector();
		Stemmer stemmer = new LovinsStemmer();
		filter.setStemmer(stemmer);
		filter.setIDFTransform(true);

	}
	/*
	private void evaluateModels() {
		try {
			evaluateModel("test",testDataset);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	*/
	public ClassificationResult classify(){
		RandomDatasetProvider p = new RandomDatasetProvider(testDataset);
		double high = 0;
		double low = 10;
		double meanScore = 0.0;
		double medianScore = 0.0;
		
		try {
			double totalScore = 0;
			double numDatasets= 50;
			double[] scores = new double[(int) numDatasets];
			
			for(int i=0;i<numDatasets;i++){
				Instances data = p.getRandomDataAndRemovePercentage((double) i);
				double score = classifyData(data);
				totalScore += score;
				scores[i] = score;
				if(score > high)
					high = score;
				if(score < low)
					low = score;
			}
			meanScore = totalScore/numDatasets;
			medianScore = scores[(int) (numDatasets/2)];
		} catch (Exception e) {
			e.printStackTrace();
		}
		ClassificationResult result = new ClassificationResult();
		result.high = high;
		result.low = low;
		result.mean = meanScore;
		result.median = medianScore;
		return result;
	}
	/*
	private void runTests() throws Exception {
		evaluateModel(new NaiveBayes(), "Naive bayes");
		evaluateModel(new DecisionStump(), "Decision Stump");
		evaluateModel(new VFI(), "VFI");
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		mlp.setHiddenLayers("5");
		evaluateModel(mlp, "MLP");
		evaluateModel(new SMO(), "Support Vector lol");
		evaluateModel("testDataset",testDataset);
	}*/
	Dataset loadDataFromDir(String dir){
		TextDirectoryLoader loader = new TextDirectoryLoader();
		Dataset set = null;
		try {
			loader.setDirectory(getFileDir(dir));
			Instances data = loader.getDataSet();
			Instances struct = loader.getStructure();
			set = new Dataset(data,struct);
		} catch (IOException e) {
			p("Couldn't load the specified dir");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;

	}
	public void setTrainingData(Dataset set){
		setTrainingData(set.data,set.structure);
	}
	public void setTrainingData(Instances data, Instances struct){
		trainStructure = struct;
		try {
			filter.setInputFormat(trainStructure);
			trainDataset = formatData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTestingData(Dataset set){
		setTestingData(set.data);
	}
	public void setTestingData(Instances data){
		try {
			testDataset = formatData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean isTestdataSet(){
		if(testDataset == null)
			return false;
		return true;
	}
	public boolean isTraindataSet(){
		if(trainDataset == null)
			return false;
		return true;
	}
	/**
	 * Stem and format the training and testing datasets for use with a classifier
	 * must be called before classifyData()
	 * @throws Exception
	 */
	private Instances formatData(Instances data) throws Exception{
		data = Filter.useFilter(data, filter);
		return data;
	}
	/**
	 * Set the classifier to use, and build its model 
	 * with the current training set
	 * @param classifierToUse
	 */
	public void setClassifier(String classifierToUse){
		Classifier classif = classifiers.get(classifierToUse);
		ALGO_USED = classifierToUse;
		try {
			//Build the classifier
			classif.buildClassifier(trainDataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		classifier = classif;
	}

	public Evaluation evaluateModel(Instances data) throws Exception{
		p("Evulation of algo "+ALGO_USED+":");
		Evaluation e = new Evaluation(trainDataset);
		e.evaluateModel(classifier, data);
		return e;
		
	}

	private double classifyData(Instances data) throws Exception{
		int numInstances = data.numInstances();
		int pos = 0;
		int neg = 0;
		for(int i=0;i < data.numInstances(); i++){
			int result = (int) classifier.classifyInstance(data.instance(i));
			boolean isPositive = result==POS_CLASS;
			if(isPositive)
				pos++;
			boolean isNegative = result==NEG_CLASS;
			if(isNegative)
				neg++;
		}
		//p("amount of positive votes: "+pos+"among "+numInstances+" reviews");
		//p("amount of negative votes: "+neg+"among "+numInstances+" reviews");
		//p(div);
		double total = (double) numInstances;
		double meanValue = (pos/total);
		double score = meanValue*10;
		return score;
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
	
	

}
