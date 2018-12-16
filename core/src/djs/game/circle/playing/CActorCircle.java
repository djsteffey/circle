package djs.game.circle.playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CActorCircle extends Actor{
    // constants
    private static final String TAG = CActorCircle.class.toString();

    // variables
    private int m_radius;
    protected Texture m_texture;
    private ShaderProgram m_shader;


    // functions
    public CActorCircle(float x, float y, int radius){
        // center point location
        this.setPosition(x, y);

        // radius
        this.m_radius = radius;

        // glowing setup
        int glow_range = 24;
        int main_band_thickness = 8;

        // create the pixmap
        Pixmap pm = new Pixmap((this.m_radius + glow_range) * 2 + 1, (this.m_radius + glow_range) * 2 + 1, Pixmap.Format.RGBA8888);
        pm.setBlending(Pixmap.Blending.None);

        // outer glow range
        for (int i = 0; i < glow_range; ++i){
            int now_radius = this.m_radius + glow_range - i;
            if (now_radius > 0) {
                float alpha = (i + 1) / ((float) glow_range + 1) / 4;
                pm.setColor(1.0f, 1.0f, 1.0f, alpha);
                pm.fillCircle(pm.getWidth() / 2, pm.getHeight() / 2, now_radius);
            }
        }

        // main band
        if (this.m_radius - main_band_thickness > 0) {
            pm.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            pm.fillCircle(pm.getWidth() / 2, pm.getHeight() / 2, this.m_radius);
            pm.setColor(1.0f, 1.0f, 1.0f, 0.0f);
            pm.fillCircle(pm.getWidth() / 2, pm.getHeight() / 2, this.m_radius - main_band_thickness);
        }

        // inner glow range
        for (int i = 0; i < glow_range; ++i){
            int now_radius = this.m_radius - main_band_thickness - 1 - i;
            if (now_radius > 0) {
                float alpha = (glow_range - i) / ((float) glow_range + 1) / 4;
                pm.setColor(1.0f, 1.0f, 1.0f, alpha);
                pm.fillCircle(pm.getWidth() / 2, pm.getHeight() / 2, now_radius);
            }
        }

        // save to a texture
        this.m_texture = new Texture(pm);

        // cleanup pm
        pm.dispose();

        // shader
        this.m_shader = new ShaderProgram(
                Gdx.files.internal("shaders/circle.vert"),
                Gdx.files.internal("shaders/circle.frag")
        );
        if (this.m_shader.isCompiled() == false){
            Gdx.app.log(TAG, this.m_shader.getLog());
            this.m_shader = null;
        }
    }

    @Override
    public void draw(Batch batch, float parent_alpha){
        // additive blending
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        // set the color
        Color color = this.getColor();
        color.a = 1.0f;
        batch.setColor(color);

        // draw it
        batch.draw(
                this.m_texture,
                this.getX() - this.m_texture.getWidth() / 2,
                this.getY() - this.m_texture.getHeight() / 2
        );

        // back to normal blending
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public float get_radius(){
        return this.m_radius;
    }

    public Vector2 get_center_position(){
        return new Vector2(this.getX(), this.getY());
    }
}
