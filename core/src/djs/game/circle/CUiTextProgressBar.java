package djs.game.circle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class CUiTextProgressBar extends Group{
    // constants


    // variables
    private int m_max_value;
    private int m_current_value;
    private Label m_text;
    private Image m_background;
    private Image m_foreground;

    // functions
    public CUiTextProgressBar(AssetManager am, int width, int height, String font){
        this.setSize(width, height);

        // initial values
        this.m_max_value = 100;
        this.m_current_value = 0;

        // nine patch
        NinePatch np = new NinePatch(am.get("ui/progress_bar.png", Texture.class), 4, 4, 4, 4);

        // background
        this.m_background = new Image(np);
        this.m_background.setSize(this.getWidth(), this.getHeight());
        this.m_background.setPosition(0, 0);
        this.m_background.setColor(Color.RED);
        this.addActor(this.m_background);

        // foreground
        this.m_foreground = new Image(np);
        this.m_foreground.setSize(this.getWidth(), this.getHeight());
        this.m_foreground.setPosition(0, 0);
        this.m_foreground.setColor(Color.GREEN);
        this.addActor(this.m_foreground);

        // create the text label
        this.m_text = new Label(
                "",
                new Label.LabelStyle(
                        am.get(font, BitmapFont.class),
                        Color.WHITE
                )
        );
        this.m_text.setSize(this.getWidth(), this.getHeight());
        this.m_text.setAlignment(Align.center);
        this.addActor(this.m_text);

        // set the text and bar sizes
        this.update_text();
    }

    @Override
    public void draw(Batch batch, float parent_alpha){
        super.draw(batch, parent_alpha);
        batch.setColor(Color.WHITE);
    }

    public void set_max_value(int value){
        this.m_max_value = value;
        this.update_text();
    }

    public void set_current_value(int value){
        this.m_current_value = value;
        if (this.m_current_value > this.m_max_value){
            this.m_current_value = this.m_max_value;
        }
        this.update_text();
    }

    public void set_values(int current_value, int max_value){
        this.m_current_value = current_value;
        this.m_max_value = max_value;
        if (this.m_current_value > this.m_max_value){
            this.m_current_value = this.m_max_value;
        }
        this.update_text();
    }

    private void update_text(){
        this.m_text.setText(
                this.m_current_value + " / " + this.m_max_value
        );
        this.m_foreground.setSize(
                this.getWidth() * (this.m_current_value / (float)this.m_max_value),
                this.getHeight()
        );
    }

    public void set_background_color(Color color){
        this.m_background.setColor(color);
    }

    public void set_foreground_color(Color color){
        this.m_foreground.setColor(color);
    }
}
