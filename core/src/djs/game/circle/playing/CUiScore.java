package djs.game.circle.playing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import djs.game.circle.CUiLabel;
import djs.game.circle.IGameServices;

public class CUiScore extends Group{
    // constants


    // variables
    private CUiLabel m_label_score;

    // functions
    public CUiScore(IGameServices gs){
        this.m_label_score = new CUiLabel(
                gs,
                "0"
        );
        this.m_label_score.setFontScale(5.0f);
        this.m_label_score.setWidth(720);
        this.m_label_score.setAlignment(Align.center);
        this.m_label_score.start_pulse();
        this.addActor(this.m_label_score);

        // size
        this.setSize(this.m_label_score.getWidth(), this.m_label_score.getHeight());

        // origin in the center
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
    }

    public void set_score(int score){
        this.m_label_score.setText("" + score);
    }
}
