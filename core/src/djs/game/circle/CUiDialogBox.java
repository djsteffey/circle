package djs.game.circle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class CUiDialogBox extends Group{
    public interface IListener{
        void on_ui_dialog_yes(CUiDialogBox ui);
        void on_ui_dialog_no(CUiDialogBox ui);
        void on_ui_dialog_ok(CUiDialogBox ui);
    }

    public enum EType{
        YES_NO,
        OK
    }

    // constants


    // variables
    private IGameServices m_game_services;
    private IListener m_listener;

    // functions
    public CUiDialogBox(IGameServices game_services, EType type, String text, float text_scale, IListener listener){
        // size
        this.setSize(720, 1280);

        // services
        this.m_game_services = game_services;

        // listener
        this.m_listener = listener;

        // background
        Image background = new Image(
                new NinePatch(
                        game_services.get_asset_manager().get("ui/nine_patch_normal.png", Texture.class),
                        6, 6, 6, 6
                )
        );
        background.setSize(this.getWidth(), this.getHeight());
        background.setColor(1.0f, 1.0f, 1.0f, 0.85f);
        this.addActor(background);

        // text
        CUiLabel label = new CUiLabel(this.m_game_services, text);
        label.setFontScale(text_scale);
        label.set_glow_color(new Color(this.m_game_services.get_save_state().get_text_glow_color()));
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setWidth(720);
        label.pack();
        label.setWidth(720);
        label.setPosition(
                (720 - label.getWidth()) / 2,
                (1280 - label.getHeight()) / 2
        );
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // buttons
        if (type == EType.YES_NO){
            // yes button
            TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_064.fnt");
            tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());
            TextButton tb = new TextButton("Yes", tbs);
            tb.setSize(200, 64);
            tb.setPosition(128, label.getY() - tb.getHeight() - 32);
            tb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // sound
                    CUiDialogBox.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                    // callback
                    CUiDialogBox.this.m_listener.on_ui_dialog_yes(CUiDialogBox.this);
                }
            });
            this.addActor(tb);

            // no
            tb = new TextButton("No", tbs);
            tb.setSize(200, 64);
            tb.setPosition(392, label.getY() - tb.getHeight() - 32);
            tb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // sound
                    CUiDialogBox.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                    // callback
                    CUiDialogBox.this.m_listener.on_ui_dialog_no(CUiDialogBox.this);
                }
            });
            this.addActor(tb);
        }
        else if (type == EType.OK){
            // yes button
            TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_064.fnt");
            tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());
            TextButton tb = new TextButton("OK", tbs);
            tb.setSize(200, 64);
            tb.setPosition(260, label.getY() - tb.getHeight() - 32);
            tb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // sound
                    CUiDialogBox.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                    // callback
                    CUiDialogBox.this.m_listener.on_ui_dialog_ok(CUiDialogBox.this);
                }
            });
            this.addActor(tb);
        }
    }

    /*
    @Override
    public void draw(Batch batch, float parent_alpha){
//        super.draw(batch, parent_alpha);
//        batch.end();

        batch.setShader(this.m_shader);
//        batch.begin();
        this.m_shader.setUniformf("u_glow", new Vector2(0.00f, 0.45f));
        this.m_shader.setUniformf("u_outline", new Vector2(0.45f, 0.55f));
        this.m_shader.setUniformf("u_glowColor", new Color(0x0000ff80));
        this.m_shader.setUniformf("u_outlineColor", new Color(0x00222b));

        this.m_shader.setUniformi("u_enableGlow", 1);
        this.m_shader.setUniformi("u_enableOutline", 0);
        super.draw(batch, parent_alpha);
//        batch.end();

        batch.setShader(null);
//        batch.begin();
    }
    */
}
