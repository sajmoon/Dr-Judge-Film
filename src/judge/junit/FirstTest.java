package judge.junit;

import static org.junit.Assert.*;
import judge.content.Item;

import org.junit.Test;

public class FirstTest {

	@Test
	public void testCreateItem() {
		Item i = new Item("string");
		assertEquals("Size", 1, i.wordCount());
		assertFalse("Size", 2 == i.wordCount());
		
	}

}
