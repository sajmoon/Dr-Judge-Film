package judge.content;

import java.util.Random;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

public class RandomDatasetProvider {
	Instances allData;

	public RandomDatasetProvider(Instances dataset){
		allData = dataset;
	}

	public Instances getRandomDataAndRemovePercentage(double percent){
		RemovePercentage removalFilter = new RemovePercentage();
		Instances randomData = allData;
		try {
			Random r = new Random();
			randomData = randomData.resample(r);
			removalFilter.setInputFormat(randomData);
			removalFilter.setPercentage(percent);
			randomData = Filter.useFilter(randomData, removalFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return randomData;
	}
	public Instances getRandomData(){
		Instances randomData = allData;
		Random r = new Random();
		randomData = randomData.resample(r);
		//return randomData;
		return allData;
	}

}
