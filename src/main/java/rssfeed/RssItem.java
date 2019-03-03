package rssfeed;

import java.util.Date;
import java.util.Objects;

public class RssItem {
    public String title;
    public String link;
    public String description;
    public String imgUrl;
    public Date pubDate = new Date(0);

    public String guid;

    public RssItem() {}

    public RssItem(RssItem other) {
        title = other.title;
        link = other.link;
        description = other.description;
        imgUrl = other.imgUrl;
        pubDate.setTime(other.pubDate.getTime());

        guid = other.guid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RssItem)) return false;
        RssItem rssItem = (RssItem) o;
        return Objects.equals(title, rssItem.title) &&
                Objects.equals(link, rssItem.link) &&
                Objects.equals(description, rssItem.description) &&
                Objects.equals(imgUrl, rssItem.imgUrl) &&
                pubDate.equals(rssItem.pubDate) &&
                Objects.equals(guid, rssItem.guid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, description, imgUrl, pubDate, guid);
    }
}
