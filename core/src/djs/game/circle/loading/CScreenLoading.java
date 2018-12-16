package djs.game.circle.loading;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import djs.game.circle.CScreen;
import djs.game.circle.IGameServices;
import djs.game.circle.mainmenu.CScreenMainMenu;

public class CScreenLoading extends CScreen {
    // constants


    // variables
    private Label m_label_percent;

    // functions
    public CScreenLoading(IGameServices game_services) {
        super(game_services);

        // queue all needed assets
        this.queue_assets();

        // create the loading label
        this.m_game_services.get_asset_manager().finishLoadingAsset("fonts/droid_bold_064.fnt");
        this.m_label_percent = new Label(
                "Loading\n0%",
                new Label.LabelStyle(
                        this.m_game_services.get_asset_manager().get("fonts/droid_bold_064.fnt", BitmapFont.class),
                        Color.WHITE
                )
        );
        this.m_label_percent.setWidth(720);
        this.m_label_percent.setPosition(
                0,
                (1280 - this.m_label_percent.getHeight()) / 2
        );
        this.m_label_percent.setAlignment(Align.center);
        this.m_label_percent.setColor(new Color(this.m_game_services.get_save_state().get_text_color()));
        this.m_stage.addActor(this.m_label_percent);

    }

    @Override
    public void render(float delta) {
        // update asset manager
        this.m_game_services.get_asset_manager().update();

        // update the label
        this.m_label_percent.setText(
                "Loading\n" + (int)(this.m_game_services.get_asset_manager().getProgress() * 100) + "%"
        );

        // done loading?
        if (this.m_game_services.get_asset_manager().getProgress() == 1.0f) {
            this.m_game_services.set_next_screen(new CScreenMainMenu(this.m_game_services));
        }

        super.render(delta);
    }

    private void queue_assets() {
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_008.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_012.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_016.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_024.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_032.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_048.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_064.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_096.fnt", BitmapFont.class);
        this.m_game_services.get_asset_manager().load("fonts/droid_bold_128.fnt", BitmapFont.class);

        this.m_game_services.get_asset_manager().load("ui/nine_patch_normal.png", Texture.class);
        this.m_game_services.get_asset_manager().load("ui/nine_patch_pressed.png", Texture.class);
        this.m_game_services.get_asset_manager().load("ui/nine_patch_disabled.png", Texture.class);
        this.m_game_services.get_asset_manager().load("ui/nine_patch_ui_background.png", Texture.class);
        this.m_game_services.get_asset_manager().load("ui/progress_bar.png", Texture.class);
        this.m_game_services.get_asset_manager().load("ui/slider_knob.png", Texture.class);
        this.m_game_services.get_asset_manager().load("ui/slider_background.png", Texture.class);
        this.m_game_services.get_asset_manager().load("ui/slider_before.png", Texture.class);


        this.m_game_services.get_asset_manager().load("fonts/droid_bold_032_distance.fnt", BitmapFont.class);
    }
}
