package gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import rssfeed.RssFeed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RssSelection extends Container<VerticalGroup> {

	private VerticalGroup verticalGroup = new VerticalGroup();
	private Map<String, RssFeed> titleToFeed = new HashMap<>();
	private SelectBox<String> dropdown;

	private static final int WIDTH = 500;
	private static final int HEIGHT = 900;
	private static final int DROPDOWN_HEIGHT = 30;
	private static final int ITEM_HEIGHT = 50;

	private static final Skin skin = new Skin(Gdx.files.internal("skin/cloud-form-ui.json"));

	public RssSelection() {
		super();
		setActor(verticalGroup);

		prefWidth(WIDTH);
		prefHeight(HEIGHT);

		dropdown = new SelectBox<>(skin);

		dropdown.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent changeEvent, Actor actor) {
				//TODO on feed selection populate item list
			}
		});

		Container<SelectBox> container = new Container<>(dropdown);
		container.prefWidth(WIDTH);
		container.prefHeight(DROPDOWN_HEIGHT);
		verticalGroup.addActor(container);
	}

	public void setData(List<RssFeed> feeds){
		Array<String> labels = new Array<>();
		for(RssFeed feed : feeds){
			String title = feed.title;
			titleToFeed.put(title, feed);
			labels.add(title);
		}

		dropdown.setItems(labels);
	}
}
