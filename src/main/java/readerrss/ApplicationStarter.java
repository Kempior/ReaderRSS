package readerrss;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class ApplicationStarter {
	static public void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setTitle("RSS Reader");
		config.setWindowedMode(1600, 900);
		new Lwjgl3Application(new Application(), config);
	}
}
