package judge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import judge.content.RandomDatasetProvider;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.SMO;
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
	HashMap<String,Classifier> classifiers;
	StringToWordVector filter;
	Evaluation eval;
	boolean DEBUGGING = true;
	public static final String CLASSIF_BAYES_MULTI ="Bayes Naive Multinomial";
	public static final String CLASSIF_BAYES_NAIVE ="Bayes Naive";
	public static final String CLASSIF_SMO ="Support Vector machine";
	public static final String[] USABLE_ALGOS = {CLASSIF_BAYES_MULTI,CLASSIF_BAYES_NAIVE,CLASSIF_SMO};

	private static final String div = "-----------------------------------";
	private static int NEG_CLASS = 0;
	private static int POS_CLASS = 1;
	
	public ClassifierHandler(){
		classifiers = new HashMap<String,Classifier>();
		classifiers.put(CLASSIF_BAYES_MULTI, new NaiveBayesMultinomial());
		classifiers.put(CLASSIF_BAYES_NAIVE, new NaiveBayes());
		classifiers.put(CLASSIF_SMO, new SMO());
		
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
	public Dataset loadDataFromDir(String dir){
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
	public void setTrainingData(Instances data, Instances struct){
		trainStructure = struct;
		try {
			filter.setInputFormat(trainStructure);
			trainDataset = formatData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setTrainingData(Dataset set){
		trainStructure = set.structure;
		try {
			filter.setInputFormat(trainStructure);
			trainDataset = formatData(set.data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void setTestingData(Dataset set){
		try {
			testDataset = formatData(set.data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setTestingData(Instances data, Instances struct){
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

	public void setClassifier(String classifierToUse){
		Classifier classif = classifiers.get(classifierToUse);
		ALGO_USED = classifierToUse;
		try {
			eval = setTrainingData(classif,trainDataset);
			p("Built classifier");

		} catch (Exception e) {
			e.printStackTrace();
		}
		classifier = classif;
	}

	public Evaluation setTrainingData(Classifier c, Instances dataset) throws Exception{
		Evaluation e = new Evaluation(dataset);
		c.buildClassifier(dataset);
		return e;
	}

	public void evaluateModel(String testDatasetName, Instances data) throws Exception{
		p("Classification of "+testDatasetName+" with algo: "+ALGO_USED+":");
		eval.evaluateModel(classifier, data);
		p("");
		p("Class details:\n"+eval.toClassDetailsString());
		p(div);
		p("Test data:\n"+eval.toSummaryString("jodu", true));
		p(div);
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
	private class Dataset{
		public Instances data;
		public Instances structure;
		public Dataset(Instances dat, Instances struct){
			data = dat;
			structure = struct;
		}
	}
	

}
