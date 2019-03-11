package dbaccess;

import com.mongodb.Block;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import rssfeed.RssFeed;
import rssfeed.RssItem;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
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
	public void add(String feedName, RssItem newItem) throws RuntimeException {

		if (newItem == null)
			throw new IllegalArgumentException("newItem cannot be null");

		if (newItem.guid == null)
			throw new IllegalArgumentException("newItem.guid cannot be null");

		if (contains(feedName, newItem))
			throw new IllegalArgumentException("newItem already in feed");

		MongoCollection collection = db.getCollection(feedName, RssItem.class);
		collection.insertOne(newItem);
//		Document newDoc = rssItem2DocumentMapper(newItem);
//
//		collection.insertOne(newDoc);

//		List<Document> docs = new ArrayList<Document>();
//		docs.add(newDoc);
//		docs.add(newDoc);
//		newDoc.toBsonDocument();

//		collection.insertOne(new Document("_id", feedName)
//			.append("blood", docs));

//		docs.add(newDoc);

//		var updateQuery = new BasicDBObject("_id", "blood").put("itemList.itemID", "1");

//		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("itemList.$.resources", newDoc));
//		collection.findOneAndUpdate(updateQuery, updateCommand);
		// TODO jesus what a fucking mess
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

		MongoCollection collection = db.getCollection(feed.title, RssFeed.class);
		collection.insertOne(feed);
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
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		client = MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings((builder) ->
						builder.hosts(Arrays.asList(new ServerAddress(hostname, port))))
				.codecRegistry(pojoCodecRegistry)
				.build());

		db = client.getDatabase(defaultName);
	}
}
