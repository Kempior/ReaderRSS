package rssfeed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RssFeedTest {

	private RssItem itemNull;
	private RssItem itemDefault;
	private RssItem itemModified;

	@BeforeEach
	void setUp() {
		itemNull = null;
		itemDefault = new RssItem();
		itemModified = new RssItem();
		itemModified.title = "AAA";
		itemModified.pubDate.setTime(12345);
	}

	@Test
	void equalsTest() {

		RssFeed emptyFeed = new RssFeed();
		assertEquals(emptyFeed, emptyFeed);

		assertNotEquals(null, emptyFeed);

		RssFeed feedOne = new RssFeed();
		RssFeed feedTwo = new RssFeed();
		assertEquals(feedOne, feedTwo);

		feedOne.items.add(itemNull);
		assertEquals(feedOne, feedOne);
		assertNotEquals(feedOne, feedTwo);

		feedOne.items.add(itemDefault);
		feedOne.title = "AAA";
		feedTwo.items.add(itemNull);
		feedTwo.items.add(itemDefault);
		feedTwo.title = "AAA";
		assertEquals(feedOne, feedTwo);

		feedTwo.items = new ArrayList<RssItem>();
		assertNotEquals(feedOne, feedTwo);
		assertNotEquals(feedTwo, emptyFeed);

		feedTwo.title = null;
		assertEquals(feedTwo, emptyFeed);
	}

	@Test
	void hashCodeTest() {
		RssFeed feedOne = null;
		final RssFeed finalItemOne = feedOne;
		assertThrows(NullPointerException.class, () -> finalItemOne.hashCode());

		feedOne = new RssFeed();
		assertEquals(feedOne.hashCode(), feedOne.hashCode());

		RssFeed itemOneRef = feedOne;
		assertEquals(itemOneRef.hashCode(), feedOne.hashCode());

		RssFeed feedTwo = new RssFeed();
		assertEquals(feedOne.hashCode(), feedTwo.hashCode());

		feedTwo.title = "AAA";
		feedTwo.items.add(itemModified);
		assertNotEquals(feedOne.hashCode(), feedTwo.hashCode());

		feedOne.title = "AAA";
		feedOne.items.add(itemModified);
		assertEquals(feedOne.hashCode(), feedTwo.hashCode());

		feedTwo.items = new ArrayList<RssItem>(feedOne.items);
		assertEquals(feedOne.hashCode(), feedTwo.hashCode());
	}
}