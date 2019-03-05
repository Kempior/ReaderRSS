package rssfeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RssFeed {
	public String title;
	public String description;
	public String link;
	public String language;
	public String copyright;

	public List<RssItem> items = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RssFeed)) return false;
		RssFeed rssFeed = (RssFeed) o;
		return Objects.equals(title, rssFeed.title) &&
				Objects.equals(description, rssFeed.description) &&
				Objects.equals(link, rssFeed.link) &&
				Objects.equals(language, rssFeed.language) &&
				Objects.equals(copyright, rssFeed.copyright) &&
				Objects.equals(items, rssFeed.items);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, description, link, language, copyright, items);
	}
}
