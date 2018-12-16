package djs.game.circle.playing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import djs.game.circle.CSoundManager;
import djs.game.circle.CUiLabel;
import djs.game.circle.IGameServices;

public class CUiGameOver extends Group{
    public interface IListener{
        void on_ui_game_over_button_again(CUiGameOver ui);
        void on_ui_game_over_button_quit(CUiGameOver ui);
    }

    // constants


    // variables
    private IGameServices m_game_services;
    private IListener m_listener;

    // functions
    public CUiGameOver(IGameServices game_services, int revolutions, int jumped_spikes, int score, IListener listener){
        // size
        this.setSize(704, 640);

        // services
        this.m_game_services = game_services;

        // update stats
        this.m_game_services.get_save_state().set_total_games_played(this.m_game_services.get_save_state().get_total_games_played() + 1);
        this.m_game_services.get_save_state().set_total_revolutions(this.m_game_services.get_save_state().get_total_revolutions() + revolutions);
        this.m_game_services.get_save_state().set_total_spikes_jumped(this.m_game_services.get_save_state().get_total_spikes_jumped() + jumped_spikes);
        this.m_game_services.get_save_state().set_total_scores(this.m_game_services.get_save_state().get_total_scores() + score);
        if (this.m_game_services.get_save_state().get_max_revolutions() < revolutions){
            this.m_game_services.get_save_state().set_max_revolutions(revolutions);
        }
        if (this.m_game_services.get_save_state().get_max_spikes_jumped() < jumped_spikes){
            this.m_game_services.get_save_state().set_max_spikes_jumped(jumped_spikes);
        }
        if (this.m_game_services.get_save_state().get_max_score() < score){
            this.m_game_services.get_save_state().set_max_score(score);
        }
        this.m_game_services.save_save_state();
        this.m_game_services.update_leaderboards();

        // background
        Image background = new Image(
                new NinePatch(
                        this.m_game_services.get_asset_manager().get("ui/nine_patch_normal.png", Texture.class),
                        6, 6, 6, 6
                )
        );
        background.setSize(this.getWidth(), this.getHeight());
        background.setColor(1.0f, 1.0f, 1.0f, 0.95f);
        this.addActor(background);

        // button style
        TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_096.fnt");
        tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());

        // again button
        TextButton tb = new TextButton("Again", tbs);
        tb.setWidth(320);
        tb.setPosition(
                (this.getWidth() - 2 * tb.getWidth()) / 3,
                8
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // sound
                CUiGameOver.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                // callback
                CUiGameOver.this.m_listener.on_ui_game_over_button_again(CUiGameOver.this);
            }
        });
        this.addActor(tb);

        // quit button
        tb = new TextButton("Quit", tbs);
        tb.setWidth(320);
        tb.setPosition(
                ((this.getWidth() - 2 * tb.getWidth()) / 3) + tb.getWidth() + ((this.getWidth() - 2 * tb.getWidth()) / 3),
                8
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // sound
                CUiGameOver.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                // callback
                CUiGameOver.this.m_listener.on_ui_game_over_button_quit(CUiGameOver.this);
            }
        });
        this.addActor(tb);

        // game over
        CUiLabel go = new CUiLabel(
                this.m_game_services,
                "Game Over"
        );
        go.setFontScale(3.5f);
        go.setWidth(this.getWidth() - 8 - 8);
        go.setAlignment(Align.center);
        go.setPosition(
                8,
                this.getHeight() - 8 - go.getHeight() - 50
        );
        go.start_pulse();
        this.addActor(go);

        // score/rev/spikes
        Label label = new Label(
                "Score\nRevolutions\nSpikes",
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_064.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        label.setAlignment(Align.left);
        label.setPosition(
                32,
                8 + tb.getHeight() + 64
        );
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // numbers
        label = new Label(
                "" + score + "\n" + revolutions + "\n" + jumped_spikes,
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_064.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        label.setAlignment(Align.right);
        label.setPosition(
                this.getWidth() - 32 - label.getWidth(),
                8 + tb.getHeight() + 64
        );
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // origin in the center
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);

        // listener
        this.m_listener = listener;
    }
}
