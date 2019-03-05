package dbaccess;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import rssfeed.RssFeed;
import rssfeed.RssItem;

import java.security.InvalidParameterException;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class DbAccessor {

	final String defaultName = "rssFeed";
	private MongoClient client;
	private MongoDatabase db;

	/**
	 * Constructor, uses given hostname and port 27017
	 *
	 * @param hostname The hostname to connect to. For local access, use "localhost"
	 */
	public DbAccessor(String hostname) {
		this(hostname, 27017);
	}

	/**
	 * Constructor, uses given hostname and a given port
	 *
	 * @param hostname The hostname to connect to. For local access, use "localhost"
	 * @param port     The port to use
	 */
	public DbAccessor(String hostname, int port) {
		ConnectToDb(hostname, port);
	}

	/**
	 * Switches accessor to another database under same host.
	 *
	 * @param newDb The name of a database to switch to
	 * @throws IllegalArgumentException when name contains an invalid character TODO check for invalid characters
	 */
	public void switchToDb(String newDb) throws IllegalArgumentException {

		if (newDb.contains(" "))
			throw new IllegalArgumentException("Database name cannot contain spaces ' '");

		db = client.getDatabase(newDb);
	}

	/**
	 * Returns the current database name
	 *
	 * @return Current database name
	 */
	public String getCurrentDb() {
		return db.getName();
	}

	/**
	 * Adds RssItem to a given feed
	 *
	 * @param feedName The feed name TODO add collection name checking
	 * @param newItem  The item to insert into collection
	 * @throws IllegalArgumentException when newItem is null or is already in collection
	 */
	public void add(String feedName, RssItem newItem) throws IllegalArgumentException {

		if (newItem == null)
			throw new IllegalArgumentException("newItem cannot be null");

		if (newItem.guid == null)
			throw new IllegalArgumentException("newItem.guid cannot be null");

		if (contains(feedName, newItem))
			throw new IllegalArgumentException("newItem already in feed");

		MongoCollection collection = db.getCollection(feedName);
		Document newDoc = rssItem2DocumentMapper(newItem);


//        collection.updateOne(eq("title", feedName), );
		// TODO FIX
	}

	/**
	 * Returns true if collection already contains the rssItem
	 *
	 * @param collection Collection name
	 * @param newItem    Item to check if it's in the collection
	 * @return True if collection already contains the rssItem, false otherwise
	 */
	private boolean contains(String collection, RssItem newItem) {
		if (isEmpty(collection))
			return false;

		return db.getCollection(collection)
				.find(eq("_id", newItem.guid))
				.first() != null;
	}

	/**
	 * Returns true if collection is empty
	 *
	 * @param collection The collection name to check
	 * @return True if collection is empty, false otherwise
	 */
	private boolean isEmpty(String collection) {
		return db.getCollection(collection).find().first() == null;
	}

	/**
	 * Adds the feed to the database as a collection with its name being rssFeed's title
	 *
	 * @param feed The feed to add to the database
	 */
	public void add(RssFeed feed) {

		if (feed.title == null)
			throw new InvalidParameterException("Feed title cannot be null");

		MongoCollection collection = db.getCollection(feed.title);
		collection.insertOne(new Document("title", feed.title));
		collection.insertOne(new Document("description", feed.description));
		collection.insertOne(new Document("link", feed.link));
		collection.insertOne(new Document("language", feed.language));
		collection.insertOne(new Document("copyright", feed.copyright));

		for (RssItem item : feed.items) {
			add(feed.title, item);
		}

		for (RssItem item : feed.items)
			add(feed.title, item);
	}

	/**
	 * Returns the collection with a given name as a RssFeed
	 *
	 * @param collectionName The name of collection to retrieve
	 * @return The collection matching the parameters
	 */
	public RssFeed find(String collectionName) {
		RssFeed returnFeed = new RssFeed();

		MongoCollection collection = db.getCollection(collectionName);

		collection.find().projection(Projections.include("items"));

		MongoCursor cursor = collection.find().iterator();

		try {
			cursor.forEachRemaining(i ->
					returnFeed.items.add(documentToRssItemMapper((Document) i)));
		} finally {
			cursor.close();
		}

		return returnFeed;
	}

	/**
	 * Drops the current database
	 */
	public void dropDb() {
		db.drop();
	}

	/**
	 * Drops the collection
	 *
	 * @param name The name of the collection
	 */
	public void dropCollection(String name) {
		db.getCollection(name).drop();
	}

	/**
	 * ONLY FOR TESTING, TIMEOUT TAKES 30s
	 *
	 * @return True if connected
	 */
	protected boolean isConnected() {
		try {
			db.listCollections().first();
			return true;
		} catch (MongoTimeoutException e) {
			return false;
		}
	}

	/**
	 * Connects to a given database
	 *
	 * @param hostname The hostname of the database
	 * @param port     The port to use
	 */
	private void ConnectToDb(String hostname, int port) {
		client = MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings((builder) ->
						builder.hosts(Arrays.asList(new ServerAddress(hostname, port))))
				.build());

		db = client.getDatabase(defaultName);
	}

	private Document rssItem2DocumentMapper(RssItem newItem) {
		return new Document("_id", newItem.guid)
				.append("title", newItem.title)
				.append("description", newItem.description)
				.append("link", newItem.link)
				.append("imgUrl", newItem.imgUrl)
				.append("pubDate", newItem.pubDate.getTime());
	}

	private RssItem documentToRssItemMapper(Document doc) {
		RssItem returnItem = new RssItem();

		returnItem.guid = doc.get("_id").toString();

		returnItem.title = toStringIfNotNull(doc.get("title"));
		returnItem.description = toStringIfNotNull(doc.get("description"));
		returnItem.link = toStringIfNotNull(doc.get("link"));
		returnItem.imgUrl = toStringIfNotNull(doc.get("imgUrl"));

		if (doc.get("pubDate") != null)
			returnItem.pubDate.setTime(Long.parseLong(doc.get("pubDate").toString()));

		return returnItem;
	}

	private String toStringIfNotNull(Object o) {
		if (o == null)
			return null;
		else
			return o.toString();
	}
}
