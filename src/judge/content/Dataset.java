package judge.content;

import weka.core.Instances;

public class Dataset{
	public Instances data;
	public Instances structure;
	public Dataset(Instances dat, Instances struct){
		data = dat;
		structure = struct;
	}
}