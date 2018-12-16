package djs.game.circle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class CUiSettings extends Group{
    public interface IListener{
        void on_ui_settings_close(CUiSettings ui);
    }

    // constants


    // variables
    private IGameServices m_game_services;
    private IListener m_listener;
    private Slider m_slider_music;
    private Slider m_slider_sound;
    private Image m_image_player;
    private Image m_image_world;
    private Image m_image_spike;


    // functions
    public CUiSettings(IGameServices game_services, IListener listener){
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
        background.setColor(1.0f, 1.0f, 1.0f, 0.95f);
        background.setTouchable(Touchable.disabled);
        this.addActor(background);

        // stats
        CUiLabel settings = new CUiLabel(
                this.m_game_services,
                "Settings"
        );
        settings.setFontScale(4.0f);
        settings.setWidth(this.getWidth());
        settings.setAlignment(Align.center);
        settings.setPosition(0,1000);
        settings.start_pulse();
        this.addActor(settings);

        // label style
        Label.LabelStyle ls = new Label.LabelStyle(
                this.m_game_services.get_asset_manager().get("fonts/droid_bold_064.fnt", BitmapFont.class),
                new Color(game_services.get_save_state().get_text_color())
        );

        // music volume
        Label label = new Label("Music", ls);
        label.setPosition(16, 800);
        this.addActor(label);
        Slider.SliderStyle ss = new Slider.SliderStyle(
                new NinePatchDrawable(
                        new NinePatch(
                                this.m_game_services.get_asset_manager().get("ui/slider_background.png", Texture.class),
                                6,6,6,6
                        )
                ),
                new TextureRegionDrawable(
                        new TextureRegion(
                                this.m_game_services.get_asset_manager().get("ui/slider_knob.png", Texture.class)
                        )
                )
        );
        ss.knobBefore = new NinePatchDrawable(
                new NinePatch(
                        this.m_game_services.get_asset_manager().get("ui/slider_before.png", Texture.class),
                        6,6,6,6
                )
        );
        this.m_slider_music = new Slider(
                0.0f,
                100.0f,
                1.0f,
                false,
                ss
        );
        this.m_slider_music.setWidth(454);
        this.m_slider_music.setPosition(250, 800);
        this.m_slider_music.setValue(this.m_game_services.get_save_state().get_music_volume() * 100.0f);
        this.m_slider_music.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        CUiSettings.this.on_music_slider_changed();
                    }
                }
        );
        this.addActor(this.m_slider_music);

        // sound effects volume
        label = new Label("Sound", ls);
        label.setPosition(16, 700);
        this.addActor(label);
        this.m_slider_sound = new Slider(
                0.0f,
                100.0f,
                1.0f,
                false,
                ss
        );
        this.m_slider_sound.setWidth(454);
        this.m_slider_sound.setPosition(250, 700);
        this.m_slider_sound.setValue(this.m_game_services.get_save_state().get_sound_volume() * 100.0f);
        this.m_slider_sound.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        CUiSettings.this.on_sound_slider_changed();
                    }
                }
        );
        this.addActor(this.m_slider_sound);


        // player color
        label = new Label("Player", ls);
        label.setPosition(16, 600);
        this.addActor(label);
        Pixmap pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        pm.fillRectangle(0, 0, 2, 2);
        this.m_image_player = new Image(new TextureRegion(new Texture(pm)));
        pm.dispose();
        this.m_image_player.setSize(454, label.getHeight());
        this.m_image_player.setPosition(250, 600);
        this.m_image_player.setColor(new Color(this.m_game_services.get_save_state().get_player_color()));
        this.m_image_player.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                CUiSettings.this.on_player_color_click();
            }
        });
        this.addActor(this.m_image_player);

        // world color
        label = new Label("World", ls);
        label.setPosition(16, 500);
        this.addActor(label);
        pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        pm.fillRectangle(0, 0, 2, 2);
        this.m_image_world = new Image(new TextureRegion(new Texture(pm)));
        pm.dispose();
        this.m_image_world.setSize(454, label.getHeight());
        this.m_image_world.setPosition(250, 500);
        this.m_image_world.setColor(new Color(this.m_game_services.get_save_state().get_world_color()));
        this.m_image_world.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                CUiSettings.this.on_world_color_click();
            }
        });
        this.addActor(this.m_image_world);

        // spike color
        label = new Label("Spike", ls);
        label.setPosition(16, 400);
        this.addActor(label);
        pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        pm.fillRectangle(0, 0, 2, 2);
        this.m_image_spike = new Image(new TextureRegion(new Texture(pm)));
        pm.dispose();
        this.m_image_spike.setSize(454, label.getHeight());
        this.m_image_spike.setPosition(250, 400);
        this.m_image_spike.setColor(new Color(this.m_game_services.get_save_state().get_spike_color()));
        this.m_image_spike.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                CUiSettings.this.on_spike_color_click();
            }
        });
        this.addActor(this.m_image_spike);

        // close button
        TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_064.fnt");
        tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());
        TextButton tb = new TextButton("Back", tbs);
        tb.setPosition((this.getWidth() - tb.getWidth()) / 2, 250);
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // sound
                CUiSettings.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                // save the state
                CUiSettings.this.m_game_services.save_save_state();

                // callback
                CUiSettings.this.m_listener.on_ui_settings_close(CUiSettings.this);
            }
        });
        this.addActor(tb);
    }

    private void on_world_color_click(){
        // disable settings touch
        this.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // open a color picker ui
        CUiColorPicker ui = new CUiColorPicker(
                this.m_game_services,
                this.m_game_services.get_save_state().get_world_color(),
                new CUiColorPicker.IListener() {
                    @Override
                    public void on_ui_color_picker_ok(CUiColorPicker ui, int color) {
                        // set the new color
                        CUiSettings.this.m_game_services.get_save_state().set_world_color(color);
                        CUiSettings.this.m_image_world.setColor(new Color(color));


                        // enable settings ui
                        CUiSettings.this.setTouchable(Touchable.enabled);

                        // close this ui
                        ui.remove();
                    }

                    @Override
                    public void on_ui_color_picker_cancel(CUiColorPicker ui) {
                        // enable settings ui
                        CUiSettings.this.setTouchable(Touchable.enabled);

                        // close this ui
                        ui.remove();
                    }
                }
        );
        ui.setPosition(
                (720 - ui.getWidth()) / 2,
                (1280 - ui.getHeight()) / 2
        );
        this.getStage().addActor(ui);
    }

    private void on_player_color_click(){
        // disable settings touch
        this.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // open a color picker ui
        CUiColorPicker ui = new CUiColorPicker(
                this.m_game_services,
                this.m_game_services.get_save_state().get_player_color(),
                new CUiColorPicker.IListener() {
                    @Override
                    public void on_ui_color_picker_ok(CUiColorPicker ui, int color) {
                        // set the new color
                        CUiSettings.this.m_game_services.get_save_state().set_player_color(color);
                        CUiSettings.this.m_image_player.setColor(new Color(color));

                        // enable settings ui
                        CUiSettings.this.setTouchable(Touchable.enabled);

                        // close this ui
                        ui.remove();
                    }

                    @Override
                    public void on_ui_color_picker_cancel(CUiColorPicker ui) {
                        // enable settings ui
                        CUiSettings.this.setTouchable(Touchable.enabled);

                        // close this ui
                        ui.remove();
                    }
                }
        );
        ui.setPosition(
                (720 - ui.getWidth()) / 2,
                (1280 - ui.getHeight()) / 2
        );
        this.getStage().addActor(ui);
    }

    private void on_spike_color_click(){
        // disable settings touch
        this.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // open a color picker ui
        CUiColorPicker ui = new CUiColorPicker(
                this.m_game_services,
                this.m_game_services.get_save_state().get_spike_color(),
                new CUiColorPicker.IListener() {
                    @Override
                    public void on_ui_color_picker_ok(CUiColorPicker ui, int color) {
                        // set the new color
                        CUiSettings.this.m_game_services.get_save_state().set_spike_color(color);
                        CUiSettings.this.m_image_spike.setColor(new Color(color));

                        // enable settings ui
                        CUiSettings.this.setTouchable(Touchable.enabled);

                        // close this ui
                        ui.remove();
                    }

                    @Override
                    public void on_ui_color_picker_cancel(CUiColorPicker ui) {
                        // enable settings ui
                        CUiSettings.this.setTouchable(Touchable.enabled);

                        // close this ui
                        ui.remove();
                    }
                }
        );
        ui.setPosition(
                (720 - ui.getWidth()) / 2,
                (1280 - ui.getHeight()) / 2
        );
        this.getStage().addActor(ui);
    }

    private void on_music_slider_changed(){
        this.m_game_services.get_save_state().set_music_volume(this.m_slider_music.getValue() / 100.0f);
    }

    private void on_sound_slider_changed(){
        this.m_game_services.get_save_state().set_sound_volume(this.m_slider_sound.getValue() / 100.0f);
    }
}
