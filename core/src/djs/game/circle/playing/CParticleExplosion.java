package djs.game.circle.playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CParticleExplosion extends Group{
    private class CParticle{
        // constants


        // variables
        public float m_position_x;
        public float m_position_y;
        private float m_velocity_x;
        private float m_velocity_y;
        public float m_life;
        private float m_life_max;
        public Color m_color;

        // functions
        public CParticle(float x, float y, float velocity_x, float velocity_y, float life, Color color){
            this.m_position_x = x;
            this.m_position_y = y;
            this.m_velocity_x = velocity_x;
            this.m_velocity_y = velocity_y;
            this.m_life = life;
            this.m_life_max = life;
            this.m_color = color;
        }

        public boolean act(float delta_time){
            // update position
            this.m_position_x += this.m_velocity_x * delta_time;
            this.m_position_y += this.m_velocity_y * delta_time;

            // update life
            this.m_life -= delta_time;
            float percent = this.m_life / this.m_life_max;
            this.m_color.a = percent;
            if (this.m_life <= 0.0f){
                return false;
            }
            return true;
        }
    }

    // constants


    // variables
    private ShapeRenderer m_shape_renderer;
    private List<CParticle> m_particles;

    // functions
    public CParticleExplosion(float x, float y, float duration, float force, Random random, int color){
        this.m_shape_renderer = new ShapeRenderer();
        this.m_particles = new ArrayList<CParticle>();

        Color particle_color = new Color(color);
        for (int i = 0; i < 32; ++i){
            float radians = random.nextFloat() * (3.1415927f * 2);
            float vel = (random.nextFloat() * force) + 16.0f;
            float vel_x = MathUtils.sin(radians) * vel;
            float vel_y = MathUtils.cos(radians) * vel;
            this.m_particles.add(
                    new CParticle(
                            x,
                            y,
                            vel_x,
                            vel_y,
                            duration,
                            particle_color
                    )
            );
        }
    }

    @Override
    public void act(float delta_time){
        // super me
        super.act(delta_time);

        // update each particle and record how many still alive
        int num_alive = 0;
        for (CParticle particle : this.m_particles){
           if (particle.act(delta_time) == true){
               // still alive
               num_alive += 1;
           }
        }

        // end the explosion if none left alive
        if (num_alive == 0){
            this.remove();
        }
    }

    @Override
    public void draw(Batch batch, float parent_alpha){
        super.draw(batch, parent_alpha);

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.m_shape_renderer.setProjectionMatrix(batch.getProjectionMatrix());
        this.m_shape_renderer.begin(ShapeRenderer.ShapeType.Filled);

        for (CParticle particle : this.m_particles) {
            this.m_shape_renderer.setColor(particle.m_color);
            this.m_shape_renderer.circle(
                    particle.m_position_x,
                    particle.m_position_y,
                    4.0f,
                    32
            );
        }

        this.m_shape_renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

//        batch.setColor(Color.WHITE);
        batch.begin();
    }
}
