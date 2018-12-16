package djs.game.circle.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import djs.game.circle.CGame;
import djs.game.circle.IAdListener;
import djs.game.circle.IPlatformServices;

public class DesktopLauncher implements IPlatformServices {
	// constants


	// variables


	// functions
	public static void main (String[] arg) {
		new DesktopLauncher();
	}

	public DesktopLauncher() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.x = 2300;
		config.y = 20;
		config.forceExit = true;
		config.fullscreen = false;
		config.title = "Leap";
		config.resizable = false;
		config.vSyncEnabled = true;
		config.width = 720 / 2;
		config.height = 1280 / 2;

		CGame game = new CGame(this);

		new LwjglApplication(game, config);
	}

	// iplatformservices
	@Override
	public void open_rate(){
		// not implemented
		Gdx.app.log("DEBUG", "open_rate(): not implemented");
	}
	@Override
	public void show_ad(IAdListener listener){
		// not implemented
		Gdx.app.log("DEBUG", "show_ad(): not implemented");
		listener.on_ad_complete();
	}
	// iplatformservices end
}
