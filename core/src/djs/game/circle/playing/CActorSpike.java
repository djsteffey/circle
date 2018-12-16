package djs.game.circle.playing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CActorSpike extends Actor{
    // constants
    private static final int BASE_SIZE = 16;


    // variables
    private float m_current_angle;
    private int m_size;
    private Vector2 m_end_point;
    private CActorCircleWorld m_world;
    private boolean m_jumped;
    private TextureRegion m_texture_region;

    // functions
    public CActorSpike(float angle, int size, int color, CActorCircleWorld world){
        // setup state
        this.m_current_angle = angle;
        this.m_size = size;
        this.m_world = world;
        this.m_end_point = new Vector2(
                (float)(this.m_world.getX() + (this.m_world.get_radius() + this.m_size) * Math.cos(this.m_current_angle * MathUtils.degreesToRadians)),
                (float)(this.m_world.getY() + (this.m_world.get_radius() + this.m_size) * Math.sin(this.m_current_angle * MathUtils.degreesToRadians))
        );
        this.m_jumped = false;

        // create the texture
        int glow_range = 24;
        Pixmap pm = new Pixmap((this.m_size * 2) + (glow_range * 2) + 1, BASE_SIZE + (glow_range * 2) + 1, Pixmap.Format.RGBA8888);
        pm.setBlending(Pixmap.Blending.None);

        // outer glow range
        for (int i = 0; i < glow_range; ++i){
            float alpha = (i + 1) / ((float)glow_range + 1) / 4;
            pm.setColor(1.0f, 1.0f, 1.0f, alpha);
            pm.fillTriangle(
                    glow_range, pm.getHeight() - i,
                    pm.getWidth() - i, pm.getHeight() / 2,
                    glow_range, i
            );
        }

        // main sections
        pm.setColor(1, 1, 1, 1);
        pm.fillTriangle(
                glow_range + 0, glow_range + BASE_SIZE,
                glow_range + this.m_size, glow_range + BASE_SIZE / 2,
                glow_range + 0, glow_range + 0
        );

        // make the texture region
        this.m_texture_region = new TextureRegion(new Texture(pm));
        this.m_texture_region.flip(false, false);
        pm.dispose();

        this.setColor(new Color(color));
    }

    @Override
    public void draw(Batch batch, float parent_alpha){
        // super me
        super.draw(batch, parent_alpha);

        // draw it
        batch.setColor(this.getColor());
        batch.draw(
                this.m_texture_region,
                this.m_end_point.x - this.m_texture_region.getRegionWidth() / 2, this.m_end_point.y - this.m_texture_region.getRegionHeight() / 2,
                this.m_texture_region.getRegionWidth() / 2, this.m_texture_region.getRegionHeight() / 2,
                this.m_texture_region.getRegionWidth(), this.m_texture_region.getRegionHeight(),
                1, 1,
                this.m_current_angle
        );

        batch.setColor(Color.WHITE);
    }

    public Vector2 get_end_point(){
        return this.m_end_point;
    }

    public float get_current_angle(){
        return this.m_current_angle;
    }

    public boolean get_jumped(){
        return this.m_jumped;
    }

    public void set_jumped(boolean value){
        this.m_jumped= value;
    }
}
