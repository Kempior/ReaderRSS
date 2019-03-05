package dbaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rssfeed.RssFeed;
import rssfeed.RssItem;

import static org.junit.jupiter.api.Assertions.*;

class DbAccessorTest {

	private DbAccessor accessor;
	private RssItem itemNull;
	private RssItem itemDefault;
	private RssItem itemModified;
	private RssItem itemDummy;

	@BeforeEach
	void setUp() {

		accessor = new DbAccessor("localhost");
		accessor.switchToDb("test");

		itemNull = null;

		itemDefault = new RssItem();
		itemDefault.guid = "itemDefault";

		itemModified = new RssItem();
		itemModified.guid = "itemModified";
		itemModified.title = "AAA";
		itemModified.pubDate.setTime(12345);

		itemDummy = new RssItem();
		itemDummy.guid = "This is the guid";
		itemDummy.title = "This is the title";
		itemDummy.description = "This is the description";
		itemDummy.link = "This is the link";
		itemDummy.imgUrl = "This is the imgUrl";
		itemDefault.pubDate.setTime(12345);
	}

	@AfterEach
	void tearDown() {

		accessor.switchToDb("test");
		accessor.dropDb();
	}

	@Test
	void constructorTest() {

		DbAccessor testedAccessor = new DbAccessor("localhost");
		DbAccessor finalTestedAccessor1 = testedAccessor;
		assertDoesNotThrow(() -> finalTestedAccessor1.switchToDb("test"));
		assertTrue(testedAccessor.isConnected());

		testedAccessor = new DbAccessor("This will fail.");
		testedAccessor.switchToDb("test");
		assertFalse(testedAccessor.isConnected());

		DbAccessor finalTestedAccessor = testedAccessor;
		assertThrows(IllegalArgumentException.class, () -> finalTestedAccessor.switchToDb("/\\. \"$*<>:|?"));
	}

	@Test
	void databaseSwitchTest() {

		assertEquals(accessor.getCurrentDb(), "test");
		assertDoesNotThrow(() -> accessor.switchToDb("testDb"));

		assertEquals(accessor.getCurrentDb(), "testDb");
	}

	@Test
	void inputSimpleTest() {

		assertThrows(IllegalArgumentException.class, () -> accessor.add("testFeed", itemNull));
		assertDoesNotThrow(() -> accessor.add("testFeed", itemModified));

		assertEquals(itemModified, accessor.find("testFeed").items.get(0));
		assertEquals(1, accessor.find("testFeed").items.size());
		assertEquals(0, accessor.find("testFeed").items.size());
	}

	@Test
	void inputUnorderedTest() {

		RssFeed feed = new RssFeed();
		feed.title = "testFeed";
		feed.items.add(itemDefault);
		feed.items.add(itemModified);
		accessor.add(feed);

		RssFeed dbFeed = accessor.find("testFeed");
		for (RssItem item : dbFeed.items) {
			assertTrue(feed.items.contains(item));
		}
		for (RssItem item : feed.items) {
			assertTrue(dbFeed.items.contains(item));
		}

		feed = new RssFeed();
		dbFeed = accessor.find("testFeed");

		boolean areAllItemsIdentical = true;
		for (RssItem item : dbFeed.items)
			if (!feed.items.contains(item))
				areAllItemsIdentical = false;
		assertFalse(areAllItemsIdentical);

		areAllItemsIdentical = true;
		for (RssItem item : dbFeed.items)
			if (!feed.items.contains(item))
				areAllItemsIdentical = false;
		assertFalse(areAllItemsIdentical);
	}

	@Test
	void feedAccessTest() {
		RssFeed feed = new RssFeed();
		feed.title = "Feed title";
		feed.items.add(itemNull);
		feed.items.add(itemDefault);
		feed.items.add(itemModified);
		feed.items.add(itemDummy);

		assertNotEquals(feed, accessor.find("Feed title"));

		accessor.add(feed);

		assertEquals(feed, accessor.find("Feed title"));
	}
}
