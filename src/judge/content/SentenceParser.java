package judge.content;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class SentenceParser {
	static String PATH = "datasets/sentences/";
	static String DEST = "datasets/sentences/CSV/";
	static String POS = "pos/";
	static String NEG = "neg/";
	static String FILENAME = "rt-polarity.";
	static String ARFF = ".arff";
	
	public static void main(String[] args){
		SentenceParser p = new SentenceParser();
		File pos = new File(PATH+FILENAME+"pos");
		File neg = new File(PATH+FILENAME+"neg");
		p.writeClassDir(pos, DEST+POS);
		p.writeClassDir(neg, DEST+NEG);
		
		
	}
	public SentenceParser(){
		File destDir = new File(DEST);
		if(!destDir.exists())
			destDir.mkdir();
		File posDir = new File(DEST+POS);
		if(!posDir.exists())
			posDir.mkdir();
		File negDir = new File(DEST+NEG);
		if(!negDir.exists())
			negDir.mkdir();
	}
	
	public void writeClassDir(File src, String dest){
		FastVector atts = new FastVector();
		atts.addElement(new Attribute("contents",(FastVector) null));
		Instances data = new Instances("Sentences",atts,0);
		try {
			BufferedReader in = new BufferedReader(new FileReader(src));
			char[] buf = new char[(int) (src.length()*2)];
			in.read(buf);
			String inFile = new String(buf);
			String[] sentences = inFile.split("\\.");
			for(String s : sentences){
				if(s.length()>5){
				s = s.trim().toLowerCase();
				double[] newInst = new double[1];
				newInst[0] = (double) data.attribute(0).addStringValue(s);
				data.add(new Instance(1.0,newInst));
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileWriter out = new FileWriter(new File(dest+"formatted"+ARFF));
			out.write(data.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
