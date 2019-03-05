package rssfeed;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RssItemTest {

	@Test
	void equalsTest() {
		RssItem itemOne = null;
		RssItem finalItemOne = itemOne;
		assertThrows(NullPointerException.class, () -> finalItemOne.equals(null));

		itemOne = new RssItem();
		assertEquals(itemOne, itemOne);
		assertNull(itemOne.description);
		assertNotNull(itemOne.pubDate);

		RssItem itemTwo = new RssItem();
		assertEquals(itemOne, itemTwo);

		itemTwo.title = "AAA";
		itemTwo.pubDate.setTime(12345);
		assertNotEquals(itemOne, itemTwo);

		itemOne.title = "AAA";
		itemOne.pubDate.setTime(12345);
		assertEquals(itemOne, itemTwo);

		itemTwo.pubDate = new Date(12345);
		assertEquals(itemOne, itemTwo);

		RssItem itemOneClone = new RssItem(itemOne);
		assertEquals(itemOneClone, itemTwo);
	}

	@Test
	void hashCodeTest() {
		RssItem itemOne = null;
		final RssItem finalItemOne = itemOne;
		assertThrows(NullPointerException.class, () -> finalItemOne.hashCode());

		itemOne = new RssItem();
		assertEquals(itemOne.hashCode(), itemOne.hashCode());

		RssItem itemOneRef = itemOne;
		assertEquals(itemOneRef.hashCode(), itemOne.hashCode());

		RssItem itemTwo = new RssItem();
		assertEquals(itemOne.hashCode(), itemTwo.hashCode());

		itemTwo.title = "AAA";
		itemTwo.pubDate.setTime(12345);
		assertNotEquals(itemOne.hashCode(), itemTwo.hashCode());

		itemOne.title = "AAA";
		itemOne.pubDate.setTime(12345);
		assertEquals(itemOne.hashCode(), itemTwo.hashCode());

		itemTwo.pubDate = new Date(12345);
		assertEquals(itemOne.hashCode(), itemTwo.hashCode());

		RssItem itemOneClone = new RssItem(itemOne);
		assertEquals(itemOneClone.hashCode(), itemTwo.hashCode());
	}
}