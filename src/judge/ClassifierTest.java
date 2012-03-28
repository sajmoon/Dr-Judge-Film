package judge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import judge.content.RandomDatasetProvider;
import judge.content.SentenceParser;

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

public class ClassifierTest {
	Instances testDataset;
	Instances trainDataset;
	Instances testStructure;
	Instances trainStructure;
	Classifier classifier;
	String ALGO_USED;
	HashMap<String,Classifier> classifiers;
	Evaluation eval;
	boolean DEBUGGING = true;
	private static final String BASE_PATH = "datasets/classified/";
	private static final String TEST_PATH = "test/";
	private static final String TRAIN_PATH = "train/";
	private static final String CLASSIF_BAYES_MULTI ="Bayes Naive Multinomial";
	private static final String CLASSIF_BAYES_NAIVE ="Bayes Naive";
	private static final String CLASSIF_SMO ="Support Vector machine";

	private static final String div = "-----------------------------------";
	int NEG_CLASS = 0;
	int POS_CLASS = 1;

	public static void main(String[] args){
		ClassifierTest jodu = new ClassifierTest();
		try {
			jodu.setClassifier(CLASSIF_BAYES_MULTI);
			//jodu.evaluateModels();
			jodu.classify();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void evaluateModels() {
		try {
			evaluateModel("test",testDataset);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void classify(){
		RandomDatasetProvider p = new RandomDatasetProvider(testDataset);
		double high = 0;
		double low = 10;
		try {
			for(int i=0;i<100;i++){
				Instances data = p.getRandomData((double) i);
				double score = classifyData(data);
				if(score > high)
					high = score;
				if(score < low)
					low = score;
			}
			p("Highscore: "+high);
			p("Lowest: "+low);
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
		evaluateModel("testDataset",testDataset);
	}

	public ClassifierTest(){
		classifiers.put(CLASSIF_BAYES_MULTI, new NaiveBayesMultinomial());
		classifiers.put(CLASSIF_BAYES_NAIVE, new NaiveBayes());
		classifiers.put(CLASSIF_SMO, new SMO());

	}
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
		trainDataset = data;
		trainStructure = struct;
	}
	public void setTrainingData(Dataset set){
		trainDataset = set.data;
		trainStructure = set.structure;
	}
	public void setTestingData(Dataset set){
		testDataset = set.data;
		testStructure = set.structure;
	}
	public void setTestingData(Instances data, Instances struct){
		testDataset = data;
		testStructure = struct;
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
	public void formatData() throws Exception{
		StringToWordVector filter = new StringToWordVector();
		Stemmer stemmer = new LovinsStemmer();
		filter.setStemmer(stemmer);
		filter.setIDFTransform(true);
		filter.setInputFormat(trainStructure);
		trainDataset = Filter.useFilter(trainDataset, filter);
		testDataset = Filter.useFilter(testDataset, filter);
	}

	private void setClassifier(String classifierToUse){
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

	private Evaluation setTrainingData(Classifier c, Instances dataset) throws Exception{
		Evaluation e = new Evaluation(dataset);
		c.buildClassifier(dataset);
		return e;
	}

	private void evaluateModel(String testDatasetName, Instances data) throws Exception{
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
		p("Number of instances in data: "+numInstances);
		int pos = 0;
		for(int i=0;i < data.numInstances(); i++){
			int result = (int) classifier.classifyInstance(data.instance(i));
			boolean isPositive = result==POS_CLASS;
			if(isPositive)
				pos++;
		}
		double total = (double) numInstances;
		double meanValue = (pos/total);
		double score = meanValue*10;
		p("Score from "+numInstances+" instances: "+score);
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
	private void pp(String m){
		if(DEBUGGING)
			System.out.print(m);
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
