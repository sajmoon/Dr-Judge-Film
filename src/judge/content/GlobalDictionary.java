package judge.content;

import java.util.HashMap;

public class GlobalDictionary {
	HashMap<String, Integer> dictionary;
	public GlobalDictionary() {
		dictionary = new HashMap<String, Integer>();
		setStandardDictionary();
	}
	
	public void setStandardDictionary() {
		addWord("bra", 1);
		addWord("dålig", -1);
		addWord("sämst", -2);
		addWord("bäst", 2);
	}
	
	public void addWord(String newWord, int weight) {
		dictionary.put(newWord, weight);
	}
	
	public int getWordWeight(String newWord) {
		int weight = 0;
		if (dictionary.containsKey(newWord)) {
			weight = dictionary.get(newWord);
		}
		return weight;
	}
	
	public int weightOfItem(Item inputItem) {
		int sum = 0;
		for ( int i = 0; i < inputItem.size(); i++) {
			sum += getWordWeight( inputItem.word(i) );
		}
		return sum;
	}
	
	public String getWordWeightAsString( Item inputItem) {
		return Integer.toString( weightOfItem(inputItem) );
	}
}
