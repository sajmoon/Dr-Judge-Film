package judge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
	private String CONF_PATH;
	
	public Config(String path){
		CONF_PATH = path;
	}
	
	private String[] defaultConfValues(){
		String classif = ClassifierHandler.USABLE_ALGOS[0].getClass().getSimpleName();
		String[] returnArray = {"#classifier:"+classif};
		return returnArray;
	}
	public void writeDefaultConfFile(File f) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		for(String s : defaultConfValues()){
			out.write(s+"\n");
		}
		out.close();
	}
	public void writeCurrentConf(String currentAlgoUsed){
		File f = new File(CONF_PATH);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write("#classifier:"+currentAlgoUsed);
			out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String readClassifierToUse() {
		String filePath = CONF_PATH;
		File confFile = new File(filePath);
		String classifier = "";
		try {
			if(!confFile.exists()){
				confFile.createNewFile();
				writeDefaultConfFile(confFile);
			}
			BufferedReader in = new BufferedReader(new FileReader(confFile));
			String line = in.readLine();
			while(line != null){
				if(line.contains("#classifier")){
					classifier = line.substring(line.indexOf(":")+1);
				}
				line = in.readLine();
			}
			in.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return classifier;
	}

}
