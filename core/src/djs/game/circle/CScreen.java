package djs.game.circle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class CScreen implements Screen {
    // constants


    // variables
    protected IGameServices m_game_services;
    private OrthographicCamera m_camera;
    protected Viewport m_viewport;
    protected Stage m_stage;
    protected InputMultiplexer m_input_multiplexer;


    // fucntions
    public CScreen(IGameServices game_services){
        this.m_game_services = game_services;
        this.m_camera = new OrthographicCamera(720, 1280);
        this.m_viewport = new FitViewport(720, 1280, this.m_camera);
        this.m_stage = new Stage(this.m_viewport);
        this.m_input_multiplexer = new InputMultiplexer();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.m_input_multiplexer);
    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(this.m_input_multiplexer);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        this.m_viewport.update(width, height);
    }

    @Override
    public void render(float delta) {
        this.m_stage.act(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.m_stage.draw();
    }

    public void transition_in(){
        this.transition_in_complete();
    }

    public void transition_in_complete(){
        this.m_input_multiplexer.addProcessor(this.m_stage);
    }

    public void transition_out(){
        this.m_input_multiplexer.removeProcessor(this.m_stage);
        this.transition_out_complete();
    }

    public void transition_out_complete(){
        this.m_game_services.signal_transition_out_complete();
    }
}
