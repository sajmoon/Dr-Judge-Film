package judge;

import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.ComplementNaiveBayes;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.VotedPerceptron;
import weka.classifiers.misc.HyperPipes;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;

public class ClassifierUtils {
	static int length = 14;

	public static Classifier[] getClassifiers(){
		Classifier[] c = {
				new NaiveBayesMultinomial(),
				new BayesNet(), 
				new ComplementNaiveBayes(),
				new ConjunctiveRule(),
				new DecisionStump(),
				new HyperPipes(),
				new NaiveBayes(),
				new OneR(),
				new RandomForest(),
				new RandomTree(),
				new RBFNetwork(),
				new SMO(),
				new VotedPerceptron(),
		};
		return c;
	}
	public static String[] getExtraInfo(){
		String[] info = new String[length];
		Arrays.fill(info, "");
		info[1] = "(A little weird)";
		info[3] = "(bad?)";
		info[4] = "(bad?)";
		info[6] = "(slow)";
		return info;
		
	}
}
