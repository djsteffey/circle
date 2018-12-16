package djs.game.circle.playing;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CActorWorldMask extends Actor{
    // constants


    // variables
    private Texture m_texture;
    private CActorCirclePlayer m_player;

    // functions
    public CActorWorldMask(CActorCirclePlayer player) {
        // create 2x2 pixmap
        Pixmap pm = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pm.drawPixel(0, 0, 0x000000f0);
        pm.drawPixel(1, 0, 0x00000000);
        pm.drawPixel(0, 1, 0x000000f0);
        pm.drawPixel(1, 1, 0x00000000);

        // make a texture from it
        this.m_texture = new Texture(pm);

        // clean up
        pm.dispose();

        // player
        this.m_player = player;
    }

    @Override
    public void act(float delta_time){
        // super me
        super.act(delta_time);

        // rotate to keep opposite of player
        this.setRotation(this.m_player.get_current_angle());
    }
    @Override
    public void draw(Batch batch, float parent_alpha){
        batch.setColor(this.getColor());

        batch.draw(
                this.m_texture,
                -390,-110,
                1500 / 2, 1500 / 2,
                1500, 1500,
                1, 1,
                this.getRotation(),
                0, 0,
                this.m_texture.getWidth(), this.m_texture.getHeight(),
                false, false
        );
    }
}
