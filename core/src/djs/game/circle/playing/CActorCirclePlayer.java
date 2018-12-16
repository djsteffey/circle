package djs.game.circle.playing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import djs.game.circle.CAssetManager;

public class CActorCirclePlayer extends CActorCircle{
    public interface IListener{
        void on_player_cross_quadrant(int quadrant);
        void on_player_revolution_complete();
    }

    // constants

    // variables
    private float m_current_angle;
    private int m_first_quadrant;
    private int m_current_quadrant;
    private float m_jump_offset;
    private int m_jumps;
    private float m_current_jump_force;
    private float[] m_jump_forces;
    private float m_player_speed;
    private CActorCircleWorld m_world;
    private IListener m_listener;

    // functions
    public CActorCirclePlayer(CAssetManager am, float x, float y, int radius, float[] jump_forces, CActorCircleWorld world, IListener listener) {
        // super me
        super(x, y, radius);

        // init
        this.m_current_angle = 89.0f;
        this.m_first_quadrant = 1;
        this.m_current_quadrant = 1;
        this.m_jump_offset = 0.0f;
        this.m_jumps = 0;
        this.m_current_jump_force = 0.0f;
        this.m_jump_forces = jump_forces;
        this.m_player_speed = 0.0f;
        this.m_world = world;
        this.m_listener = listener;

        // color
        this.setColor(255 / 255.0f, 20 / 255.0f, 175 / 255.0f, 1.0f);

//        this.setColor(Color.BLUE);
    }

    @Override
    public void act(float delta_time){
        // super me
        super.act(delta_time);

        // handle jumping
        if (this.m_jumps > 0){
            this.m_jump_offset += this.m_current_jump_force * delta_time * 60;
            this.m_current_jump_force -= this.m_world.get_gravity() * delta_time * 60;
            if (this.m_jump_offset < 0.0f){
                this.m_jump_offset = 0.0f;
                this.m_jumps = 0;
                this.m_current_jump_force = 0.0f;
            }
        }

        // handle rotation around world
        this.m_current_angle += this.m_player_speed * delta_time;
        while (this.m_current_angle < 0.0f){
            this.m_current_angle += 360.0f;
        }
        while (this.m_current_angle >= 360.0f){
            this.m_current_angle -= 360.0f;
        }

        // update quadrant
        int quadrant = (int)(this.m_current_angle / 90.0f) + 1;
        if (quadrant != this.m_current_quadrant){
            this.m_current_quadrant = quadrant;
            this.m_listener.on_player_cross_quadrant(this.m_current_quadrant);
            if (this.m_current_quadrant == this.m_first_quadrant){
                this.m_listener.on_player_revolution_complete();
            }
        }
        else{
            this.m_current_quadrant = quadrant;
        }

        // update the position based on the rotation
        float radians = this.m_current_angle * MathUtils.degreesToRadians;
        float distance_from_center = this.m_world.get_radius() + this.get_radius() + this.m_jump_offset;
        this.setPosition(
                (float)(this.m_world.getX() + distance_from_center * Math.cos(radians)),
                (float)(this.m_world.getY() + distance_from_center * Math.sin(radians))
        );

        // rotate the player itself
        this.setRotation(
                this.m_current_angle * ((this.m_world.get_radius() * 2) / (this.get_radius() * 2) + 1)
        );
    }

    public void jump(){
        if (this.m_jumps < this.m_jump_forces.length){
            this.m_jumps += 1;
            this.m_current_jump_force = this.m_jump_forces[this.m_jumps - 1];
        }
    }

    public float get_current_angle(){
        return this.m_current_angle;
    }

    public void restart(){
        this.m_current_angle = 89.0f;
        this.m_current_quadrant = 1;
        this.m_jump_offset = 0.0f;
        this.m_jumps = 0;
        this.m_current_jump_force = 0.0f;
        this.m_player_speed = 0.0f;
    }

    public void set_speed(float speed){
        this.m_player_speed = speed;
    }
}
