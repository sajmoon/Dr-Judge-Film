package judge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class ClassifierTest {
	Instances dataset;
	Instances structure;
	boolean DEBUGGING = true;
	
	public ClassifierTest(String dir){
		TextDirectoryLoader loader = new TextDirectoryLoader();
		try {
			loader.setDirectory(getFileDir(dir));
			dataset = loader.getDataSet();
			structure = loader.getStructure();
		} catch (IOException e) {
			p("Couldn't load the specified dir");
			e.printStackTrace();
		}
		
	}
	public void formatData(){
		StringToWordVector filter = new StringToWordVector();
		
		
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
