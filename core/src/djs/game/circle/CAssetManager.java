package djs.game.circle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class CAssetManager extends AssetManager{
    // constants
    private static final String TAG = CAssetManager.class.toString();

    // variables
    private ShaderProgram m_ui_label_shader;
    private Label.LabelStyle m_ui_label_style;

    // functions
    public CAssetManager(){
        // create the ui label shader
        this.m_ui_label_shader = new ShaderProgram(
                Gdx.files.internal("shaders/font-effects.vert"),
                Gdx.files.internal("shaders/font-effects.frag")
        );
        if (this.m_ui_label_shader.isCompiled() == false){
            Gdx.app.log(TAG, this.m_ui_label_shader.getLog());
            this.m_ui_label_shader = null;
        }

        // create the ui label style
        this.load("fonts/droid_bold_032_distance.fnt", BitmapFont.class);
        this.finishLoadingAsset("fonts/droid_bold_032_distance.fnt");
        this.m_ui_label_style = new Label.LabelStyle(
                this.get("fonts/droid_bold_032_distance.fnt", BitmapFont.class),
                Color.WHITE
        );
    }

    public TextButton.TextButtonStyle create_text_button_style(String font){
        NinePatchDrawable up = new NinePatchDrawable(
                new NinePatch(
                        this.get("ui/nine_patch_normal.png", Texture.class),
                        6,6,6,6
                )
        );
        NinePatchDrawable down = new NinePatchDrawable(
                new NinePatch(
                        this.get("ui/nine_patch_pressed.png", Texture.class),
                        6,6,6,6
                )
        );
        NinePatchDrawable disabled = new NinePatchDrawable(
                new NinePatch(
                        this.get("ui/nine_patch_disabled.png", Texture.class),
                        6,6,6,6
                )
        );
        BitmapFont bmf = this.get(font, BitmapFont.class);
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle(
                up,
                down,
                up,
                bmf
        );
        tbs.disabled = disabled;
        tbs.disabledFontColor = Color.DARK_GRAY;
        return tbs;
    }

    public ShaderProgram get_ui_label_shader(){
        return this.m_ui_label_shader;
    }

    public Label.LabelStyle get_ui_label_style(){
        return this.m_ui_label_style;
    }
}
