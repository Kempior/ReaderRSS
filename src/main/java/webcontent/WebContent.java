package webcontent;

import org.jsoup.nodes.Document;

public class WebContent {

    public String titleXml;
    public String xmlLink;
    public Document xmlContent;

    public WebContent(String title, String link, Document content){
        titleXml = title;
        xmlLink = link;
        xmlContent = content;
    }
}
