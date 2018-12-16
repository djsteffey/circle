package djs.game.circle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class CUiAbout extends Group{
    public interface IListener{
        void on_ui_about_close(CUiAbout ui);
    }

    // constants


    // variables
    private IGameServices m_game_services;
    private IListener m_listener;

    // functions
    public CUiAbout(IGameServices game_services, IListener listener){
        // size
        this.setSize(720, 1280);

        // services
        this.m_game_services = game_services;

        // listener
        this.m_listener = listener;

        // background
        Image background = new Image(
                new NinePatch(
                        this.m_game_services.get_asset_manager().get("ui/nine_patch_normal.png", Texture.class),
                        6, 6, 6, 6
                )
        );
        background.setSize(this.getWidth(), this.getHeight());
        background.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        background.setTouchable(Touchable.disabled);
        this.addActor(background);

        // design and programming
        Label label = new Label(
                "Design and Programming\nDaniel Steffey",
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_048.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        label.setWidth(720);
        label.setAlignment(Align.center);
        label.setPosition(
                0,
                1000
        );
        label.setTouchable(Touchable.disabled);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // libgdx
        label = new Label(
                "Game Library - libGdx",
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_024.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        label.setWidth(720);
        label.setAlignment(Align.center);
        label.setPosition(
                0,
                900
        );
        label.setTouchable(Touchable.disabled);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // graphics
        label = new Label(
                "Graphics\nDaniel Steffey",
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_024.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        label.setWidth(720);
        label.setAlignment(Align.center);
        label.setPosition(
                0,
                750
        );
        label.setTouchable(Touchable.disabled);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // music and sound
        label = new Label(
                "Music and Sound\nopengameart.org\njobromedia, Machine, Matthew Pablo, mrpoly\nneocrey, Nicole Marie T, Snabisch",
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_024.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        label.setWidth(720);
        label.setAlignment(Align.center);
        label.setPosition(
                0,
                550
        );
        label.setTouchable(Touchable.disabled);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // special thanks
        label = new Label(
                "Special Thanks\nSofia\nVicki",
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_024.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        label.setWidth(720);
        label.setAlignment(Align.center);
        label.setPosition(
                0,
                400
        );
        label.setTouchable(Touchable.disabled);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // close button
        TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_064.fnt");
        tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());
        TextButton tb = new TextButton("Back", tbs);
        tb.setPosition((this.getWidth() - tb.getWidth()) / 2, 150);
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // sound
                CUiAbout.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                // callback
                CUiAbout.this.m_listener.on_ui_about_close(CUiAbout.this);
            }
        });
        this.addActor(tb);
    }
}
