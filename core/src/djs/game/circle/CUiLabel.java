package djs.game.circle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class CUiLabel extends Label{
    // constants


    // variables
    private Color m_glow_color;
    private float m_font_scale;
    private ShaderProgram m_shader;
    private Vector2 m_glow_range;
    private Action m_action_pulse;
    private float m_original_glow_alpha;

    // functions
    public CUiLabel(IGameServices gs, String text) {
        // super me
        super(text, gs.get_asset_manager().get_ui_label_style());

        // setup the shader
        this.m_shader = gs.get_asset_manager().get_ui_label_shader();

        // scale
        this.m_font_scale = 1.0f;

        // color default
        this.setColor(new Color(gs.get_save_state().get_text_color()));

        // glow
        this.m_glow_color = new Color(gs.get_save_state().get_text_glow_color());
        this.m_glow_range = new Vector2(0.0f, 0.55f);
        this.m_original_glow_alpha = this.m_glow_color.a;

        // pulse
        this.m_action_pulse = null;
    }

    @Override
    public void draw(Batch batch, float parent_alpha){
        // set our shader
        batch.setShader(this.m_shader);

        // size it
        this.setFontScale(this.m_font_scale);

        // set the uniforms for it
        this.m_shader.setUniformf("u_glow", this.m_glow_range);
        this.m_shader.setUniformf("u_glowColor", this.m_glow_color);
        this.m_shader.setUniformi("u_enableGlow", 1);

        // draw our text
        super.draw(batch, parent_alpha);

        // unset our shader
        batch.setShader(null);
    }

    public void set_glow_color(Color color) {
        this.m_glow_color = color;
    }

    @Override
    public void setFontScale(float scale){
        super.setFontScale(scale);

        this.m_font_scale = scale;
    }

    public void start_pulse(){
        if (this.m_action_pulse == null){
            this.m_action_pulse = Actions.forever(
                    Actions.sequence(
                            new Action() {
                                float m_total = 0.0f;
                                float m_duration = 0.75f;
                                @Override
                                public boolean act(float delta) {
                                    this.m_total += delta;
                                    CUiLabel.this.m_glow_color.a = Interpolation.linear.apply(0.5f, 1.0f, this.m_total / this.m_duration);
                                    if (this.m_total >= this.m_duration){
                                        this.m_total = 0.0f;
                                        return true;
                                    }
                                    return false;
                                }
                            },
                            new Action() {
                                float m_total = 0.0f;
                                float m_duration = 0.75f;
                                @Override
                                public boolean act(float delta) {
                                    this.m_total += delta;
                                    CUiLabel.this.m_glow_color.a = Interpolation.linear.apply(1.0f, 0.5f, this.m_total / this.m_duration);
                                    if (this.m_total >= this.m_duration){
                                        this.m_total = 0.0f;
                                        return true;
                                    }
                                    return false;
                                }
                            }
                    )
            );
        }
        this.addAction(this.m_action_pulse);
    }

    public void stop_pulse(){
        this.removeAction(this.m_action_pulse);
        this.m_glow_color.a = this.m_original_glow_alpha;
    }
}
