package gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GUIHelper {
	private static Skin skin;

	public static void initialize(){
		skin = new Skin(Gdx.files.internal("skin/cloud-form-ui.json"));
	}

	public static Skin getSkin(){
		return skin;
	}

}
