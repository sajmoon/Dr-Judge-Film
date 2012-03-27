package judge.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;



public class ContentFilterer {
	String CONTENT_DIR_PATH;
	String WORDS_FILE;
	HashMap<String,Boolean> stopWords;
	boolean DEBUGGING = true;
	private static final String CONTENT_PATH = "datasets/classified/";
	private static final String BASE_PATH = "datasets/";
	private static final String TEST_PATH = "test/";
	private static final String TRAIN_PATH = "train/";
	private static final String STOP_FILE = "stopwords";
	private static final String FILTERED_PATH = "filtered/";
	
	public static void main(String[] args){
		ContentFilterer filt = new ContentFilterer(CONTENT_PATH+TEST_PATH,BASE_PATH+STOP_FILE);
		File filterPath = new File(BASE_PATH+FILTERED_PATH);
		if(!filterPath.exists())
			filterPath.mkdir();
		filt.filterAll(BASE_PATH+FILTERED_PATH);
		
	}
	
	public ContentFilterer(String contentDir, String wordsFile){
		CONTENT_DIR_PATH = contentDir;
		WORDS_FILE = wordsFile;
		
	}
	public void filterAll(String newDir){
		File wordFile = new File(WORDS_FILE);
		if(wordFile.exists())
			stopWords = parseWordFile(wordFile);
		else
			p("Stopwordsfilen finns inte");
		
		File contentDir = new File(CONTENT_DIR_PATH);
		
		if(!contentDir.exists())
			return;
		if(!contentDir.isDirectory()){
			p("content dir is not a dir :(");
			return;
		}
		File[] classDirs  = contentDir.listFiles();
		for(File f : classDirs){
			if(f.isDirectory()){
				String className = f.getName();
				File dest = new File(newDir+className);
				if(!dest.exists())
					dest.mkdir();
				parseClassDir(f, dest);
			}
		}
		
	}
	private void parseClassDir(File classDir, File dest){
		if(!classDir.isDirectory())
			return;
		File[] classFiles = classDir.listFiles();
		for(File f : classFiles){
			filterAndCopyClassFile(f,dest);
		}
	}
	private void filterAndCopyClassFile(File src, File destDir){
		File destFile = new File(destDir.getAbsolutePath()+"\\"+src.getName());
		try {
			boolean createdFile = destFile.createNewFile();
			if(!createdFile){
				p("Could not create a new file copy");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String toFilter = parseClassFile(src);
		String filtered = filterText(toFilter);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(destFile));
			out.write(filtered);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String filterText(String toFilter) {
		StringBuilder filtered = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(toFilter);
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if(stopWords.containsKey(token)){
				filtered.append(token);
				filtered.append("\n");
			}
		}
		return filtered.toString();
		
	}
	private String parseClassFile(File f){
		String theFile = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			char[] buf = new char[(int) f.length()*2];
			in.read(buf);
			theFile = new String(buf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return theFile;
	}
	
	private HashMap<String,Boolean> parseWordFile(File f){
		HashMap<String,Boolean> stops = new HashMap<String,Boolean>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String word;
			while((word = in.readLine()) != null){
				if(!(word.startsWith("#")))
					stops.put(word, true);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stops;
		
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
