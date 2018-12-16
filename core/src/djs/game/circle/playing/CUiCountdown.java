package djs.game.circle.playing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import djs.game.circle.CAssetManager;
import djs.game.circle.CUiLabel;
import djs.game.circle.IGameServices;

public class CUiCountdown extends Group{
    public interface IListener{
        void on_ui_countdown_complete(CUiCountdown ui);
    }

    // constants


    // variables
    private int m_current_countdown;
    private float m_scale;
    private CUiLabel m_label;
    private IListener m_listener;


    // functions
    public CUiCountdown(IGameServices game_services, String font, float scale, int seconds, IListener listener){
        // seconds
        this.m_current_countdown = seconds;

        // scale
        this.m_scale = scale;

        // listener
        this.m_listener = listener;

        // size
        this.setSize(720, 1280);

        // origin in the center
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);

        // add the label for the counter
        this.m_label = new CUiLabel(
                game_services,
                ""
        );
        this.m_label.setFontScale(4.0f);
        this.m_label.setSize(720, 1280);
        this.m_label.setAlignment(Align.center);
        this.addActor(this.m_label);

        // set initial scale to 0
        this.setScale(0, 0);

        // add actions for each number in the countdown
        SequenceAction action = new SequenceAction();
        for (int i = seconds; i > 0; --i){
            // set the text
            action.addAction(
                    new Action() {
                        @Override
                        public boolean act(float delta) {
                            CUiCountdown.this.m_label.setText("" + CUiCountdown.this.m_current_countdown);
                            return true;
                        }
                    }
            );
            // scale in
            action.addAction(Actions.scaleTo(CUiCountdown.this.m_scale, CUiCountdown.this.m_scale, 0.25f, Interpolation.exp5In));
            // pause
            action.addAction(Actions.delay(0.5f));
            // scale out
            action.addAction(Actions.scaleTo(0, 0, 0.25f, Interpolation.exp5Out));
            // update countdown
            action.addAction(
                    new Action() {
                        @Override
                        public boolean act(float delta) {
                            CUiCountdown.this.m_current_countdown -= 1;
                            return true;
                        }
                    }
            );
        }

        // now do the final go
        action.addAction(
                new Action() {
                    @Override
                    public boolean act(float delta) {
                        CUiCountdown.this.m_label.setText("GO");
                        return true;
                    }
                }
        );
        // scale in
        action.addAction(Actions.scaleTo(CUiCountdown.this.m_scale, CUiCountdown.this.m_scale, 0.25f, Interpolation.exp5In));
        // pause
        action.addAction(Actions.delay(0.5f));
        // scale out
        action.addAction(Actions.scaleTo(0, 0, 0.25f, Interpolation.exp5Out));
        // complete
        action.addAction(
                new Action() {
                    @Override
                    public boolean act(float delta) {
                        CUiCountdown.this.m_listener.on_ui_countdown_complete(CUiCountdown.this);
                        return true;
                    }
                }
        );

        // set the action
        this.addAction(action);
    }
}
