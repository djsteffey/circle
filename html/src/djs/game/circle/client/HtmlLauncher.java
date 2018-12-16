package djs.game.circle.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import djs.game.circle.CGame;
import djs.game.circle.IPlatformServices;

public class HtmlLauncher extends GwtApplication implements IPlatformServices {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(450, 800);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new CGame(this);
        }
}