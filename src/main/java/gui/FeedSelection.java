package gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import rssfeed.RssFeed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FeedSelection extends Table {

	private Map<String, RssFeed> titleToFeed = new HashMap<>();
	private com.badlogic.gdx.scenes.scene2d.ui.List<String> feedList;

	protected FeedSelection() {
		super();

		Skin skin = GUIHelper.getSkin();
		setSkin(skin);

		Label label = new Label("Feeds", skin);
		add(label).expandX().left().pad(5);
		row();

		feedList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);

		feedList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent changeEvent, Actor actor) {
				onFeedSelected(titleToFeed.get(feedList.getSelected()));
			}
		});

		add(feedList).expandY().fill();
	}

	public void setData(List<RssFeed> feeds){
		Array<String> labels = new Array<>();
		for(RssFeed feed : feeds){
			String title = feed.title;
			titleToFeed.put(title, feed);
			labels.add(title);
		}

		feedList.setItems(labels);
	}

	abstract public void onFeedSelected(RssFeed feed);
}
