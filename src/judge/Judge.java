package judge;

import judge.content.GlobalDictionary;
import judge.content.Item;

public class Judge {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Console Judge!");
		System.out.println("--------------");
		System.out.println("");
		
		GlobalDictionary dictionary = new GlobalDictionary();
		dictionary.addWord("bra", 1);
		dictionary.addWord("dålig", -1);
		dictionary.addWord("sämst", -2);
		dictionary.addWord("bäst", 2);
		
		Item i1 = new Item("jag tycker. att böcker är bra!");
		Item i2 = new Item("jag tycker att böcker är dålig!");
		Item i3 = new Item("jag tycker att bra böcker är dålig!");
		
		
		System.out.println(">> i1: " + dictionary.getWordWeightAsString( i1 )
				+ " i2: " + dictionary.getWordWeightAsString( i2 )
				+ " i3: " + dictionary.getWordWeightAsString( i3 ));
	}

}
