package djs.game.circle;

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

import java.text.NumberFormat;

public class CUiStats extends Group{
    public interface IListener{
        void on_ui_stats_close(CUiStats ui);
    }

    // constants


    // variables
    private IGameServices m_game_services;
    private IListener m_listener;

    // functions
    public CUiStats(IGameServices game_services, IListener listener){
        // size
        this.setSize(720, 1280);

        // services
        this.m_game_services = game_services;

        // origin in the center
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);

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
        background.setColor(1.0f, 1.0f, 1.0f, 0.95f);
        this.addActor(background);

        // stats
        CUiLabel stats = new CUiLabel(
                this.m_game_services,
                "Stats"
        );
        stats.setFontScale(6.0f);
        stats.setSize(500, 100);
        stats.setAlignment(Align.center);
        stats.setPosition(110,900);
        stats.start_pulse();
        this.addActor(stats);

        // label style
        Label.LabelStyle ls = new Label.LabelStyle(
                this.m_game_services.get_asset_manager().get("fonts/droid_bold_048.fnt", BitmapFont.class),
                Color.WHITE
        );

        // games
        Label label = new Label("Games Played: " + this.m_game_services.get_save_state().get_total_games_played(), ls);
        label.setSize(500, 64);
        label.setAlignment(Align.center);
        label.setPosition(110, 775);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);


        // columns
        label = new Label("\nRev.\nSpikes\nScore", ls);
        label.setSize(170, 300);
        label.setAlignment(Align.center);
        label.setPosition(20, 500);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        label = new Label(
                "Total" +
                        "\n" + this.m_game_services.get_save_state().get_total_revolutions() +
                        "\n" + this.m_game_services.get_save_state().get_total_spikes_jumped() +
                        "\n" + this.m_game_services.get_save_state().get_total_scores(),
                ls
        );
        label.setSize(170, 300);
        label.setAlignment(Align.center);
        label.setPosition(190, 500);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        NumberFormat.getNumberInstance().setMinimumFractionDigits(2);
        NumberFormat.getNumberInstance().setMaximumFractionDigits(2);
        label = new Label(
                "Avg." +
                        "\n" + NumberFormat.getNumberInstance().format((float)this.m_game_services.get_save_state().get_total_revolutions() / this.m_game_services.get_save_state().get_total_games_played()) +
                        "\n" + NumberFormat.getNumberInstance().format((float)this.m_game_services.get_save_state().get_total_spikes_jumped() / this.m_game_services.get_save_state().get_total_games_played()) +
                        "\n" + NumberFormat.getNumberInstance().format((float)this.m_game_services.get_save_state().get_total_scores() / this.m_game_services.get_save_state().get_total_games_played()),
                ls
        );
        label.setSize(170, 300);
        label.setAlignment(Align.center);
        label.setPosition(360, 500);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        label = new Label(
                "High" +
                        "\n" + this.m_game_services.get_save_state().get_max_revolutions() +
                        "\n" + this.m_game_services.get_save_state().get_max_spikes_jumped() +
                        "\n" + this.m_game_services.get_save_state().get_max_score(),
                ls
        );
        label.setSize(170, 300);
        label.setAlignment(Align.center);
        label.setPosition(530, 500);
        label.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.addActor(label);

        // back button
        TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_064.fnt");
        tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());
        TextButton tb = new TextButton("Back", tbs);
        tb.setPosition((this.getWidth() - tb.getWidth()) / 2, 350);
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // sound
                CUiStats.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                // callback
                CUiStats.this.m_listener.on_ui_stats_close(CUiStats.this);
            }
        });
        this.addActor(tb);
    }
}
