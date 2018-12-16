package djs.game.circle.playing;

import com.badlogic.gdx.graphics.Texture;

import djs.game.circle.CAssetManager;

public class CActorCircleWorld extends CActorCircle{
    // constants


    // variables
    private float m_gravity;

    // functions
    public CActorCircleWorld(CAssetManager am, float x, float y, int radius, float gravity) {
        // super me
        super(x, y, radius);

        // init
        this.m_gravity = gravity;

        // color
        this.setColor(57 / 255.0f, 255 / 255.0f, 20 / 255.0f, 1.0f);

        // override texture
//        this.m_texture = am.get("world.png", Texture.class);
    }

    public float get_gravity(){
        return this.m_gravity;
    }
}
