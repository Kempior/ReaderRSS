package webcontent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class HtmlParser {

    public String rssUrl = "https://www.tvn24.pl/rss.html";
    private ArrayList<WebContent> list;

    public ArrayList<WebContent> getList() {
        return list;
    }

    public void setList(ArrayList<WebContent> list){
        this.list = list;
    }

    public ArrayList<WebContent> getContent() {

        ArrayList<WebContent> list = new ArrayList<>();

        try {
            Document document = Jsoup.connect(rssUrl).get();
            Elements link = document.select(".rssLink a");
            Elements title = document.select(".name");

            for(int i=0; i<title.size();i++){
                String urlTitle = title.get(i).text();
                String urlLink = link.get(i).attr("abs:href");
                if(urlLink.equals("https://ekstraklasa.tv/ekstraklasa-tv,83,m.xml")) {
                    continue;
                }
                    try {
                        Document xml = Jsoup.connect(urlLink).get();
                        list.add(new WebContent(urlTitle, urlLink, xml));
                    } catch (Exception error) {
                        throw error;
                    }
                }
        }
        catch(Exception error) {
            throw new IllegalArgumentException(error);
        }
        return list;
    }
}

