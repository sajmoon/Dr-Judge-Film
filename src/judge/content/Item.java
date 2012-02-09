package judge.content;

import java.util.ArrayList;

public class Item {
	String[] wordList;
		
	public Item(String input) {
		input = input.replaceAll("\\.", "");
		input = input.replaceAll("!", "");
		wordList = input.split(" ");
	}
	
	public String word(int i) {
		return wordList[i];
	}
	
	public int size() {
		return wordList.length;
	}

	public int wordCount() {
		// TODO Auto-generated method stub
		return wordList.length;
	}
}
