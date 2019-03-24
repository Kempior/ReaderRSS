package readerrss;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gui.FeedSelection;
import gui.GUIHelper;
import gui.ItemSelection;
import rssfeed.RssFeed;

public class Application implements ApplicationListener {

	private Stage stage;

	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		GUIHelper.initialize();

		Table root = new Table(GUIHelper.getSkin());
		root.background("list");
		root.setFillParent(true);
		stage.addActor(root);

		ItemSelection itemSelection = new ItemSelection();
		FeedSelection feedSelection = new FeedSelection() {
			@Override
			public void onFeedSelected(RssFeed feed) {
				itemSelection.setData(feed);
			}
		};

		SplitPane splitPane = new SplitPane(feedSelection, itemSelection, false, GUIHelper.getSkin());
		splitPane.setSplitAmount(0.25f);
		root.add(splitPane).grow();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
