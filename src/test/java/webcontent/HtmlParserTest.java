import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HtmlParserTest {

	private HtmlParser parser = new HtmlParser();
	private ArrayList<WebContent> testList = new ArrayList<>();


	@Test
	void testGetContent() {

		parser.rssUrl = "";
		assertThrows(IllegalArgumentException.class, () -> {
			parser.getContent();
		});

		parser.rssUrl = "abdcds.html";
		assertThrows(IllegalArgumentException.class, () -> {
			parser.getContent();
		});

		parser.rssUrl = null;
		assertThrows(IllegalArgumentException.class, () -> {
			parser.getContent();
		});

		parser.rssUrl = "https://www.tvn24.pl/rss.html";
		assertEquals(29, parser.getContent().size());
		assertNotEquals(testList, parser.getContent());
		assertEquals(testList.getClass(), parser.getContent().getClass());
		assertNotEquals(null, parser.getContent());

	}
}