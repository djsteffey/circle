package djs.game.circle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class CUiColorPicker extends Group{
    public interface IListener{
        void on_ui_color_picker_ok(CUiColorPicker ui, int color);
        void on_ui_color_picker_cancel(CUiColorPicker ui);
    }

    // constants
    private static final float WHEEL_SIZE = 128.0f;
    private static final float IMAGE_SIZE = 512.0f;
    private static final float LIGHTNESS = 1.0f;

    // variables
    private IGameServices m_game_services;
    private IListener m_listener;
    private Image m_image_selected_color;
    private Pixmap m_pixmap;

    // functions
    public CUiColorPicker(IGameServices game_services, int initial_color, IListener listener){
        // size
        this.setSize(576, 768);

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
        background.setTouchable(Touchable.disabled);
        this.addActor(background);

        // create the color wheel image
        this.create_color_wheel_pixmap((int)WHEEL_SIZE, LIGHTNESS);
        Image image = new Image(new TextureRegion(new Texture(this.m_pixmap)));
        image.setSize(IMAGE_SIZE, IMAGE_SIZE);
        image.setPosition(
                (this.getWidth() - image.getWidth()) / 2,
                130
        );
        image.addListener(new DragListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                CUiColorPicker.this.on_color_wheel_touch(x, y);
                return true;
            }
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                CUiColorPicker.this.on_color_wheel_touch(x, y);
           }
        });
        this.addActor(image);

        // button style
        TextButton.TextButtonStyle tbs = game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_064.fnt");
        tbs.fontColor = new Color(game_services.get_save_state().get_text_color());

        // ok button
        TextButton tb = new TextButton("OK", tbs);
        tb.setSize(256, 90);
        tb.setPosition(8, 8);
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // sound
                CUiColorPicker.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                // callback
                CUiColorPicker.this.m_listener.on_ui_color_picker_ok(CUiColorPicker.this, Color.rgba8888(CUiColorPicker.this.m_image_selected_color.getColor()));
            }
        });
        this.addActor(tb);

        // cancel button
        tb = new TextButton("Cancel", tbs);
        tb.setSize(256, 90);
        tb.setPosition(this.getWidth() - 8 - tb.getWidth(), 8);
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // sound
                CUiColorPicker.this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

                // callback
                CUiColorPicker.this.m_listener.on_ui_color_picker_cancel(CUiColorPicker.this);
            }
        });
        this.addActor(tb);

        // selected color image
        Pixmap pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        pm.drawRectangle(0, 0, 2, 2);
        this.m_image_selected_color = new Image(new TextureRegion(new Texture(pm)));
        pm.dispose();
        this.m_image_selected_color.setColor(new Color(initial_color));
        this.m_image_selected_color.setSize(256, 86);
        this.m_image_selected_color.setPosition(
                (this.getWidth() - this.m_image_selected_color.getWidth()) / 2,
                674
        );
        this.addActor(this.m_image_selected_color);
    }

    private void create_color_wheel_pixmap(int size, float lightness){
        if (this.m_pixmap == null) {
            this.m_pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        }

        // Center Point (MIDDLE, MIDDLE)
        int centerX = this.m_pixmap.getWidth() / 2;
        int centerY = this.m_pixmap.getHeight() / 2;
        int radius2 = ((this.m_pixmap.getWidth() - 1) / 2) * ((this.m_pixmap.getWidth() - 1) / 2);

        for (int i = 0; i < this.m_pixmap.getHeight(); ++i){
            for (int j = 0; j < this.m_pixmap.getWidth(); ++j){
                int x = j - centerX;
                int y = i - centerY;
                float distance2 = (float)x * x + y * y;
                if (distance2 <= radius2) {
                    float angle = (float) Math.toDegrees(Math.atan2(y, x));
                    float saturation = distance2 / radius2;
                    Color c = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                    c.fromHsv(angle, saturation, lightness);
                    this.m_pixmap.setColor(c);
                    this.m_pixmap.drawPixel(i, j);
                }
                else{
                    this.m_pixmap.setColor(0x000000ff);
                    this.m_pixmap.drawPixel(i, j);
                }
            }
        }
    }

    private void on_color_wheel_touch(float x, float y){
        float scale = WHEEL_SIZE / IMAGE_SIZE;

        int pixel_x = (int)(x * scale);
        int pixel_y = (this.m_pixmap.getHeight() - (int)(y * scale) - 1);

        if ((pixel_x < 0) || (pixel_x > this.m_pixmap.getWidth() - 1)){
            return;
        }
        if ((pixel_y < 0) || (pixel_y > this.m_pixmap.getHeight() - 1)){
            return;
        }
        int color = this.m_pixmap.getPixel(pixel_x, pixel_y);
        if (color != 0x000000ff) {
            this.m_image_selected_color.setColor(new Color(color));
        }
    }
}
