package readerrss;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class Application implements ApplicationListener {

	private Stage stage;

	@Override
	public void create() {
		stage = new Stage(new ScalingViewport(Scaling.fill, 1920, 1080));
	}

	@Override
	public void resize(int i, int i1) {

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1,1 ,1 ,1);
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

	}
}
