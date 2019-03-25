package dbaccess;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import rssfeed.RssFeed;
import rssfeed.RssItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

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
	 * @throws IllegalArgumentException when name contains an invalid character
	 */
	public void switchToDb(String newDb) throws IllegalArgumentException {

		nameCheck(newDb);

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
	 * @param feedName The feed name
	 * @param newItem  The item to insert into collection
	 * @throws IllegalArgumentException when newItem is null or is already in collection
	 */
	public void add(String feedName, RssItem newItem) throws RuntimeException {

		nameCheck(feedName);

		if (newItem == null)
			throw new IllegalArgumentException("newItem cannot be null");

		if (newItem.guid == null)
			throw new IllegalArgumentException("newItem.guid cannot be null");

		if (contains(feedName, newItem))
			throw new IllegalArgumentException("newItem already in feed");

		MongoCollection collection = db.getCollection(feedName, RssFeed.class);
		RssFeed feed = find(feedName);

		if (!exists(feedName)) {
			feed = new RssFeed();
			feed.title = feedName;
		}

		feed.items.add(newItem);

		dropCollection(feedName);
		add(feed);
	}

	/**
	 * Adds the feed to the database as a collection with its name being rssFeed's title
	 *
	 * @param feed The feed to add to the database
	 */
	public void add(RssFeed feed) throws RuntimeException {

		nameCheck(feed.title);

		MongoCollection collection = db.getCollection(feed.title, RssFeed.class);
		collection.insertOne(feed);
	}

	void nameCheck(String name) throws RuntimeException {

		String[] invalidChars = "/\\. \"$*<>:|?$\n".split("");

		if (name.isBlank() ||
				Arrays.stream(invalidChars).parallel().anyMatch(name::contains))
			throw new IllegalArgumentException("The name of an object contains an invalid character");

	}

	private boolean exists(String feedName) {
		FindIterable<Document> iterable = db.getCollection("activity").find(new Document("_id", feedName));
		return iterable.first() != null;
	}

	/**
	 * Returns true if feed already contains the rssItem
	 *
	 * @param feed    Feed name
	 * @param newItem Item to check if it's in the feed
	 * @return True if feed already contains the rssItem, false otherwise
	 */
	private boolean contains(String feed, RssItem newItem) {
		if (isEmpty(feed))
			return false;

		return db.getCollection(feed)
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
	 * Returns the collection with a given name as a RssFeed
	 *
	 * @param collectionName The name of collection to retrieve
	 * @return The collection matching the parameters
	 */
	public RssFeed find(String collectionName) {
		final RssFeed[] returnFeed = new RssFeed[1];
		MongoCollection collection = db.getCollection(collectionName, RssFeed.class);

		Consumer<RssFeed> printAThing = rssFeed -> returnFeed[0] = rssFeed;

		collection.find(new Document("_id", collectionName)).forEach(printAThing);

		return returnFeed[0];
	}

	public void dropDb() {
		db.drop();
	}

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
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		client = MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings((builder) ->
						builder.hosts(Arrays.asList(new ServerAddress(hostname, port))))
				.codecRegistry(pojoCodecRegistry)
				.build());

		db = client.getDatabase(defaultName);
	}

	public Set<RssFeed> findAll() {
		var names =  db.listCollectionNames();

		Set<RssFeed> returnVal = new HashSet<>();

		for (String name : names) {
			returnVal.add(
					find(name)
			);
		}

		return  returnVal;
	}
}
