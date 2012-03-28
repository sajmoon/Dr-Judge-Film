package judge.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class SentenceParser {
	static String PATH = "datasets/sentences/";
	static String DEST = "datasets/sentences/ARFF/";
	static String FILENAME = "rt-polarity.";
	static String ARFF = ".arff";
	Instances data;
	
	public static void main(String[] args){
		SentenceParser p = new SentenceParser();
		p.createDataSet();
		p.writeToARFFFile(DEST+"sentenceDataset"+ARFF);
		
	}
	public Instances createDataSet(){
		FastVector classes = new FastVector();
		classes.addElement("neg");
		classes.addElement("pos");
		FastVector atts = new FastVector();
		atts.addElement(new Attribute("text",(FastVector) null));
		atts.addElement(new Attribute("@@class@@", classes));
		data = new Instances("sentences",atts,0);
		data.setClassIndex(data.numAttributes()-1);
		File neg = new File(PATH+FILENAME+"neg");
		File pos = new File(PATH+FILENAME+"pos");
		writeClassDir(neg,0);
		writeClassDir(pos,1);
		return data;
	}
	public void writeToARFFFile(String filePath){
		try {
			if(data == null)
				createDataSet();
			FileWriter out = new FileWriter(new File(filePath));
			out.write(data.toString());
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SentenceParser(){
		File destDir = new File(DEST);
		if(!destDir.exists())
			destDir.mkdir();
	}
	
	public void writeClassDir(File src,int classIndex){
		try {
			BufferedReader in = new BufferedReader(new FileReader(src));
			char[] buf = new char[(int) (src.length()*2)];
			in.read(buf);
			String inFile = new String(buf);
			String[] sentences = inFile.split("\\.");
			for(String s : sentences){
				if(s.length()>5){
				s = s.trim().toLowerCase();
				double[] newInst = new double[2];
				newInst[0] = (double) data.attribute(0).addStringValue(s);
				newInst[1] = classIndex;
				Instance sentenceInst = new Instance(1.0,newInst);
				data.add(sentenceInst);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
