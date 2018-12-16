package djs.game.circle.playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import djs.game.circle.CScreen;
import djs.game.circle.CSoundManager;
import djs.game.circle.CUiDialogBox;
import djs.game.circle.CUiSettings;
import djs.game.circle.IAdListener;
import djs.game.circle.IGameServices;
import djs.game.circle.mainmenu.CScreenMainMenu;

public class CScreenPlaying extends CScreen{
    private enum EState{
        LOADING, TRANSITION, START_UP, PLAYING, GAME_OVER
    }

    // constants
    private static final float TRANSITION_OUT_DURATION = 0.5f;
    private static final float TRANSITION_IN_DURATION = 0.5f;
    private static final int WORLD_RADIUS = 256;
    private static final int PLAYER_RADIUS = 24;
    private static final float PLAYER_SPEED = -1.0f * 60;
    private static final float WORLD_GRAVITY = 0.8f;
    private static final float[] JUMP_FORCES = {12, 9, 6};
    private static final float PARTICLE_EXPLOSION_DURATION = 1.25f;
    private static final float PARTICLE_EXPLOSION_FORCE = 96.0f;
    private static final float MIN_SPIKE_DEGREE_SEPARATION = 3.0f;
    private static final float GAME_SPEED_START = 0.5f;
    private static final float GAME_SPEED_INCREMENT = 0.05f;
    private static final int COUNTDOWN_SECONDS = 3;
    private static final int GAMES_PER_AD = 4;


    // variables
    private EState m_state;
    private Random m_random;
    private Group m_input_group;
    private CActorCircleWorld m_world;
    private CActorCirclePlayer m_player;
    private List<CActorSpike> m_spikes;
    private List<CActorSpike> m_spikes_working_list;
    private CActorWorldMask m_world_mask;
    private Stage m_actor_stage;
    private CUiScore m_ui_score;
    private int m_number_revolutions;
    private int m_number_jumped_spikes;
    private int m_score;
    private float m_game_speed;
    private boolean m_dialog_open;
    private boolean m_need_to_reset_flag;

    // functions
    public CScreenPlaying(IGameServices game_services) {
        // super me
        super(game_services);

        // state
        this.m_state = EState.LOADING;

        // random
        this.m_random = new Random();

        // actor stage
        this.m_actor_stage = new Stage(this.m_viewport){
                @Override
                public void addActor(Actor actor){
                    super.addActor(actor);
                    if (CScreenPlaying.this.m_world_mask != null){
                        CScreenPlaying.this.m_world_mask.toFront();
                    }
                }
        };

        // full screen group to detect press anywhere for the jump
        this.m_input_group = new Group();
        this.m_input_group.setSize(720, 1280);
        this.m_input_group.addListener(
                new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                        CScreenPlaying.this.on_touch();
                        return true;
                    }
                }
        );
        this.m_stage.addActor(this.m_input_group);

        // score
        this.m_number_revolutions = 0;
        this.m_number_jumped_spikes = 0;
        this.m_score = 0;
        this.m_game_speed = GAME_SPEED_START;
        this.m_ui_score = new CUiScore(this.m_game_services);
        this.m_ui_score.setScale(1.5f);
        this.m_ui_score.setPosition(
                0,
                1150
        );
        this.m_stage.addActor(this.m_ui_score);

        // world
        this.m_world = new CActorCircleWorld(this.m_game_services.get_asset_manager(), 720 / 2,  1280 / 2, WORLD_RADIUS, WORLD_GRAVITY);
        this.m_world.setColor(new Color(this.m_game_services.get_save_state().get_world_color()));
        this.m_world.setTouchable(Touchable.disabled);
        this.m_actor_stage.addActor(this.m_world);

        // player
        this.m_player = new CActorCirclePlayer(
                this.m_game_services.get_asset_manager(),
                720 / 2,
                1280 / 2 + WORLD_RADIUS + PLAYER_RADIUS,
                PLAYER_RADIUS,
                JUMP_FORCES,
                this.m_world,
                new CActorCirclePlayer.IListener() {
                    @Override
                    public void on_player_cross_quadrant(int quadrant) {
                        CScreenPlaying.this.on_player_cross_quadrant(quadrant);
                    }
                    @Override
                    public void on_player_revolution_complete() {
                        CScreenPlaying.this.on_player_revolution_complete();
                    }
                }
        );
        this.m_player.setColor(new Color(this.m_game_services.get_save_state().get_player_color()));
        this.m_player.setTouchable(Touchable.disabled);
        this.m_actor_stage.addActor(this.m_player);

        // spikes
        this.m_spikes = new ArrayList<CActorSpike>();
        this.m_spikes_working_list = new ArrayList<CActorSpike>();

        // world mask
        this.m_world_mask = new CActorWorldMask(this.m_player);
        this.m_world_mask.setTouchable(Touchable.disabled);
        this.m_actor_stage.addActor(this.m_world_mask);

        // settings button
        TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_096.fnt");
        tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());
        TextButton tb = new TextButton("S", tbs);
        tb.setSize(96, 96);
        tb.setPosition(720 - 8 - tb.getWidth(), 8);
        tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (CScreenPlaying.this.m_state == EState.PLAYING) {
                    CScreenPlaying.this.on_button_settings();
                }
            }
        });
        this.m_stage.addActor(tb);

        // state todo transition instead

        // other
        this.m_dialog_open = false;

        // startup
        if (this.m_game_services.get_save_state().get_total_games_played() == 0){
            this.on_show_how_to_play();
        }
        else {
            this.restart();
        }

        // reset flag
        this.m_need_to_reset_flag = false;
    }

    @Override
    public void render(float delta_time){
        // see if need to reset
        if (this.m_need_to_reset_flag){
            this.m_need_to_reset_flag = false;
            this.restart();
        }

        // stage act
        if (this.m_dialog_open == false) {
            this.m_actor_stage.act(delta_time * this.m_game_speed);
        }
        this.m_stage.act(delta_time);

        // check for player and spike collision..if playing
        if (this.m_state == EState.PLAYING) {
            for (CActorSpike spike : this.m_spikes) {
                if (Intersector.intersectSegmentCircle(
                        this.m_world.get_center_position(),
                        spike.get_end_point(),
                        this.m_player.get_center_position(),
                        this.m_player.get_radius() * this.m_player.get_radius()
                )) {
                    // collision
                    // add particle explosion
                    CParticleExplosion pe = new CParticleExplosion(
                            this.m_player.getX(),
                            this.m_player.getY(),
                            PARTICLE_EXPLOSION_DURATION,
                            PARTICLE_EXPLOSION_FORCE,
                            this.m_random,
                            this.m_game_services.get_save_state().get_player_color()
                    );
                    this.m_actor_stage.addActor(pe);

                    // hide the player
                    this.m_player.setVisible(false);

                    // game over
                    this.set_game_over();

                    // break the for loop
                    break;
                } else {
                    // no collision...get degree of difference
                    float degree_difference = this.degree_difference(this.m_player.get_current_angle(), spike.get_current_angle());

                    // see if we have just now jumped over it
                    if ((degree_difference < -3.0f) && (spike.get_jumped() == false)){
                        // mark it as jumped
                        spike.set_jumped(true);

                        // more score...each jump gets num (revolutions + 1) points
                        this.m_number_jumped_spikes += 1;
                        this.m_score += (this.m_number_revolutions + 1);
                    }

                    // check if hidden by the mask now and can remove
                    if ((degree_difference < -90.0f) && (degree_difference > -180.0f)) {
                        this.m_spikes_working_list.add(spike);
                        spike.remove();
                    }
                    // check if just now coming into view and make it visible
                    else if (degree_difference <= 90.0f) {
                        // turn it on
                        spike.setVisible(true);
                    }
                }
            }
            this.m_spikes.removeAll(this.m_spikes_working_list);
            this.m_spikes_working_list.clear();
        }

        // update scoring label
        this.m_ui_score.set_score(this.m_score);

        // draw
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.m_actor_stage.draw();
        this.m_stage.draw();
    }

    private void on_touch(){
        if (this.m_state == EState.PLAYING) {
            this.m_player.jump();
        }
    }

    private float degree_difference(float degree_0, float degree_1){
        float rad = (degree_0 - degree_1) * MathUtils.degreesToRadians;
        return (float)(MathUtils.radiansToDegrees * Math.atan2(Math.sin(rad), Math.cos(rad)));
    }

    private void on_player_cross_quadrant(int quadrant){
        // calculate the quadrant two places ahead
        quadrant -= 1;
        if (quadrant < 1){
            quadrant += 4;
        }

        this.generate_spikes(quadrant);
    }

    private void on_player_revolution_complete(){
        // increment num revolutions
        this.m_number_revolutions += 1;

        // increase game speed
        this.m_game_speed += GAME_SPEED_INCREMENT;
    }

    private void generate_spikes(int quadrant){
        // generate spikes there
        for (int j = 0; j < 3; ++j) {
            // try 100 times to make one that is more than 5 degrees apart from any other spike
            for (int i = 0; i < 100; ++i) {
                float angle = (this.m_random.nextFloat() * 90.0f) + ((quadrant - 1) * 90.0f);
                for (CActorSpike other : this.m_spikes){
                    if (Math.abs(this.degree_difference(other.get_current_angle(), angle)) < MIN_SPIKE_DEGREE_SEPARATION){
                        // conflict...continue loop
                        continue;
                    }
                }

                // enough separation...so add it
                float spike_min_size = 10.0f;
                float spike_range = 25.0f;
                CActorSpike spike = new CActorSpike(
                        angle,
                        (int)(this.m_random.nextFloat() * spike_range + spike_min_size),
                        this.m_game_services.get_save_state().get_spike_color(),
                        this.m_world
                );
                spike.setTouchable(Touchable.disabled);
                spike.setVisible(false);
                this.m_spikes.add(spike);
                this.m_actor_stage.addActor(spike);

                // since we added we can break the inner loop
                break;
            }
        }
    }

    private void set_game_over(){
        // change state
        this.m_state = EState.GAME_OVER;

        // stop player
        this.m_player.set_speed(0.0f);

        // wait for particle explosion duration then show ui
        this.m_stage.addAction(
                Actions.sequence(
                        Actions.delay(PARTICLE_EXPLOSION_DURATION / this.m_game_speed),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                // show game over
                                CUiGameOver ui = new CUiGameOver(
                                        CScreenPlaying.this.m_game_services,
                                        CScreenPlaying.this.m_number_revolutions,
                                        CScreenPlaying.this.m_number_jumped_spikes,
                                        CScreenPlaying.this.m_score,
                                        new CUiGameOver.IListener() {
                                            @Override
                                            public void on_ui_game_over_button_again(CUiGameOver ui) {
                                                CScreenPlaying.this.on_ui_game_over_button_again(ui);
                                            }
                                            @Override
                                            public void on_ui_game_over_button_quit(CUiGameOver ui) {
                                                CScreenPlaying.this.on_ui_game_over_button_quit(ui);
                                            }
                                        }
                                );
                                ui.setPosition(
                                        (720 - ui.getWidth()) / 2,
                                        (1280 - ui.getHeight()) / 2
                                );
                                CScreenPlaying.this.m_stage.addActor(ui);
                                return true;
                            }
                        }
                )
        );
    }

    private void on_ui_game_over_button_again(CUiGameOver ui){
        // close ui
        ui.remove();

        // restart
        this.restart();
    }

    private void on_ui_game_over_button_quit(CUiGameOver ui){
        // close ui
        ui.remove();

        // go back to main menu
        this.m_game_services.set_next_screen(new CScreenMainMenu(this.m_game_services));
    }

    private void restart(){
        // first see if we need to show an ad
        if (this.m_game_services.get_save_state().get_games_since_ad() >= GAMES_PER_AD){
            // we do so ask the game services to show one
            this.m_game_services.show_ad(
                    new IAdListener() {
                        @Override
                        public void on_ad_complete() {
                            // the ad is complete, record it in the state and restart the game
                            CScreenPlaying.this.m_game_services.get_save_state().set_games_since_ad(0);
                            CScreenPlaying.this.m_need_to_reset_flag = true;
                        }
                    }
            );

            // go no further
            return;
        }

        // state
        this.m_state = EState.START_UP;

        // increment number games since ad
        this.m_game_services.get_save_state().set_games_since_ad(this.m_game_services.get_save_state().get_games_since_ad() + 1);

        // restart the player
        this.m_player.restart();
        this.m_player.setVisible(true);

        // clear all spikes
        for (CActorSpike spike : this.m_spikes){
            spike.remove();
        }
        this.m_spikes.clear();

        // add the first spikes to quadrant 4
        this.generate_spikes(4);

        // change score
        this.m_number_revolutions = 0;
        this.m_number_jumped_spikes = 0;
        this.m_score = 0;
        this.m_game_speed = GAME_SPEED_START;
        this.m_ui_score.set_score(0);

        // start a countdown
        CUiCountdown ui = new CUiCountdown(
                this.m_game_services,
                "fonts/droid_bold_128.fnt",
                4.0f,
                COUNTDOWN_SECONDS,
                new CUiCountdown.IListener() {
                    @Override
                    public void on_ui_countdown_complete(CUiCountdown ui) {
                        // change stage
                        CScreenPlaying.this.m_state = EState.PLAYING;

                        // move player
                        CScreenPlaying.this.m_player.set_speed(PLAYER_SPEED);

                        // remove this ui
                        ui.remove();
                    }
                }
        );
        this.m_stage.addActor(ui);
    }

    private void on_button_settings(){
        // do nothing if a dialog is already open
        if (this.m_dialog_open == true){
            return;
        }

        // flag settings open
        this.m_dialog_open = true;

        // disable the group
        this.m_input_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // create settings ui
        CUiSettings ui = new CUiSettings(
                this.m_game_services,
                new CUiSettings.IListener() {
                    @Override
                    public void on_ui_settings_close(CUiSettings ui) {
                        // todo save settings

                        // animate stage out, close it, enable buttons
                        ui.addAction(
                                Actions.sequence(
                                        Actions.moveTo(720, ui.getY(), TRANSITION_OUT_DURATION, Interpolation.exp5In),
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                // set the colors in case they changed
                                                CScreenPlaying.this.m_player.setColor(new Color(CScreenPlaying.this.m_game_services.get_save_state().get_player_color()));
                                                CScreenPlaying.this.m_world.setColor(new Color(CScreenPlaying.this.m_game_services.get_save_state().get_world_color()));
                                                for (CActorSpike spike : CScreenPlaying.this.m_spikes){
                                                    spike.setColor(new Color(CScreenPlaying.this.m_game_services.get_save_state().get_spike_color()));
                                                }

                                                // enable input again
                                                CScreenPlaying.this.m_input_group.setTouchable(Touchable.enabled);

                                                // settings not open
                                                CScreenPlaying.this.m_dialog_open = false;

                                                // done
                                                return true;
                                            }
                                        },
                                        Actions.removeActor()
                                )
                        );
                    }
                }
        );
        ui.setPosition(720, (1280 - ui.getHeight()) / 2);
        ui.addAction(
                Actions.sequence(
                        Actions.moveTo((720 - ui.getWidth()) / 2, ui.getY(), TRANSITION_IN_DURATION, Interpolation.exp5Out)
                )
        );
        this.m_stage.addActor(ui);
    }

    private void on_show_how_to_play(){
        this.m_dialog_open = true;

        CUiDialogBox ui = new CUiDialogBox(
                this.m_game_services,
                CUiDialogBox.EType.OK,
                "The smaller player circle rotates around the larger world circle. Tap on the screen to jump the player circle over the spikes. Touching a spike will result in Game Over. You can perform a double or triple jump by tapping on the screen while already jumping. Gain points for each spike jumped with increasing points per complete revolution.",
                1.0f,
                new CUiDialogBox.IListener() {
                    @Override
                    public void on_ui_dialog_yes(CUiDialogBox ui) {

                    }
                    @Override
                    public void on_ui_dialog_no(CUiDialogBox ui) {

                    }
                    @Override
                    public void on_ui_dialog_ok(CUiDialogBox ui) {
                        CScreenPlaying.this.m_dialog_open = false;
                        ui.remove();
                        CScreenPlaying.this.restart();
                    }
                }
        );
        ui.setPosition(0, 0);
        this.m_stage.addActor(ui);
    }

}
