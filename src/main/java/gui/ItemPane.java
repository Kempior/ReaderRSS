package gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.StreamUtils;
import rssfeed.RssItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ItemPane extends Table {

	private Image image;

	public ItemPane(RssItem item) {
		super();

		createImg(item.imgUrl);

		Skin skin = GUIHelper.getSkin();
		setSkin(skin);
		background("list");

		TextButton openBtn = new TextButton("Open", skin);
		openBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent changeEvent, Actor actor) {
				Gdx.net.openURI(item.link);
			}
		});
		add(openBtn);

		Label title = new Label(item.title, skin);
		add(title);

		row();

		image = new Image();
		add(image);

		Label desc = new Label(item.description, skin);
		desc.setWrap(true);
		add(desc).expandX().fill();
	}

	private void createImg(String link){
		new Thread(new Runnable() {
			private int download (byte[] out, String url) {
				InputStream in = null;
				try {
					HttpURLConnection conn;
					conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setDoInput(true);
					conn.setDoOutput(false);
					conn.setUseCaches(true);
					conn.connect();
					in = conn.getInputStream();
					int readBytes = 0;
					while (true) {
						int length = in.read(out, readBytes, out.length - readBytes);
						if (length == -1) break;
						readBytes += length;
					}
					return readBytes;
				} catch (Exception ex) {
					return 0;
				} finally {
					StreamUtils.closeQuietly(in);
				}
			}

			@Override
			public void run () {
				byte[] bytes = new byte[1000 * 1024];
				int numBytes = download(bytes, link);
				if (numBytes != 0) {
					Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
					Gdx.app.postRunnable(() -> {
						image.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
					});
				}
			}
		}).start();
	}
}
