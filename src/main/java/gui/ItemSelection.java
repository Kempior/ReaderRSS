package gui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import rssfeed.RssFeed;
import rssfeed.RssItem;

public class ItemSelection extends Table {

	private VerticalGroup itemList;

	public ItemSelection() {
		super();

		Skin skin = GUIHelper.getSkin();

		Label title = new Label("Items", skin);
		add(title).expandX().left().pad(5);
		row();

		itemList = new VerticalGroup();
		itemList.grow();

		ScrollPane scrollPane = new ScrollPane(itemList, skin);
		scrollPane.getStyle().background = skin.getDrawable("scrollbar-vertical");
		add(scrollPane).expand().fill().pad(2);
	}

	public void setData(RssFeed feed){
		itemList.clear();
		for(RssItem item : feed.items){
			itemList.addActor(new ItemPane(item));
		}
	}
}
