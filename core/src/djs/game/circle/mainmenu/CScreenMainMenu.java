package djs.game.circle.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import djs.game.circle.CScreen;
import djs.game.circle.CSoundManager;
import djs.game.circle.CUiAbout;
import djs.game.circle.CUiDialogBox;
import djs.game.circle.CUiLabel;
import djs.game.circle.CUiSettings;
import djs.game.circle.CUiStats;
import djs.game.circle.IGameServices;
import djs.game.circle.playing.CScreenPlaying;

public class CScreenMainMenu extends CScreen {
    private enum EState{
        LOADING, TRANSITION, READY
    }

    // constants
    private static final float TRANSITION_IN_DURATION = 0.5f;
    private static final float TRANSITION_OUT_DURATION = 0.5f;
    private static final float BUTTON_WIDTH = 600.0f;
    private static final float BUTTON_HEIGHT = 128.0f;


    // variables
    private EState m_state;
    private CUiLabel m_label_title;
    private Group m_buttons_group;


    // functions
    public CScreenMainMenu(IGameServices game_services){
        super(game_services);

        // state
        this.m_state = EState.LOADING;

        // title
        this.m_label_title = new CUiLabel(
                this.m_game_services,
                "Leap"
        );
        this.m_label_title.setFontScale(6.0f);
        this.m_label_title.setWidth(720);
        this.m_label_title.setPosition(0, 1100);
        this.m_label_title.setAlignment(Align.center);
        this.m_label_title.setTouchable(Touchable.disabled);
        this.m_label_title.start_pulse();
        this.m_stage.addActor(this.m_label_title);

        // group for the buttons
        this.m_buttons_group = new Group();
        this.m_buttons_group.setSize(720, 1000);
        this.m_buttons_group.setPosition(0, 0);
        this.m_stage.addActor(this.m_buttons_group);

        // button style
        TextButton.TextButtonStyle tbs = this.m_game_services.get_asset_manager().create_text_button_style("fonts/droid_bold_064.fnt");
        tbs.fontColor = new Color(this.m_game_services.get_save_state().get_text_color());

        // play button
        TextButton tb = new TextButton("Play", tbs);
        tb.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        float button_base_y = (this.m_buttons_group.getHeight() - (6 * 8 + 7 * tb.getHeight())) / 2;
        tb.setPosition(
                (this.m_buttons_group.getWidth() - tb.getWidth()) / 2,
                button_base_y + 6 * (8 + tb.getHeight())
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CScreenMainMenu.this.on_button_play();
            }
        });
        this.m_buttons_group.addActor(tb);

        // stats button
        tb = new TextButton("Stats", tbs);
        tb.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        tb.setPosition(
                (this.m_buttons_group.getWidth() - tb.getWidth()) / 2,
                button_base_y + 5 * (8 + tb.getHeight())
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CScreenMainMenu.this.on_button_stats();
            }
        });
        this.m_buttons_group.addActor(tb);

        // leaderboard button
        tb = new TextButton("Leaderboards", tbs);
        tb.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        tb.setPosition(
                (this.m_buttons_group.getWidth() - tb.getWidth()) / 2,
                button_base_y + 4 * (8 + tb.getHeight())
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CScreenMainMenu.this.on_button_leaderboards();
            }
        });
//        tb.setDisabled(true);
        this.m_buttons_group.addActor(tb);

        // friends button
        tb = new TextButton("Friends", tbs);
        tb.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        tb.setPosition(
                (this.m_buttons_group.getWidth() - tb.getWidth()) / 2,
                button_base_y + 3 * (8 + tb.getHeight())
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CScreenMainMenu.this.on_button_friends();
            }
        });
//        tb.setDisabled(true);
        this.m_buttons_group.addActor(tb);

        // settings button
        tb = new TextButton("Settings", tbs);
        tb.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        tb.setPosition(
                (this.m_buttons_group.getWidth() - tb.getWidth()) / 2,
                button_base_y + 2 * (8 + tb.getHeight())
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CScreenMainMenu.this.on_button_settings();
            }
        });
        this.m_buttons_group.addActor(tb);

        // about button
        tb = new TextButton("About", tbs);
        tb.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        tb.setPosition(
                (this.m_buttons_group.getWidth() - tb.getWidth()) / 2,
                button_base_y + 1 * (8 + tb.getHeight())
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CScreenMainMenu.this.on_button_about();
            }
        });
        this.m_buttons_group.addActor(tb);

        // quit button
        tb = new TextButton("Quit", tbs);
        tb.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        tb.setPosition(
                (this.m_buttons_group.getWidth() - tb.getWidth()) / 2,
                button_base_y + 0 * (8 + tb.getHeight())
        );
        tb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CScreenMainMenu.this.on_button_quit();
            }
        });
        this.m_buttons_group.addActor(tb);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void transition_in(){
        // state
        this.m_state = EState.TRANSITION;

        // title
        this.m_label_title.setPosition(
                -this.m_label_title.getWidth(),
                this.m_label_title.getY()
        );
        this.m_label_title.addAction(
                Actions.sequence(
                        Actions.moveTo(
                                0,
                                this.m_label_title.getY(),
                                TRANSITION_IN_DURATION,
                                Interpolation.exp5Out
                        )
                )
        );

        // buttons group
        this.m_buttons_group.setPosition(
                -this.m_buttons_group.getWidth(),
                0
        );
        this.m_buttons_group.addAction(
                Actions.sequence(
                        Actions.moveTo(
                                0,
                                0,
                                TRANSITION_IN_DURATION,
                                Interpolation.exp5Out
                        )
                )
        );

        // stage
        this.m_stage.addAction(
                Actions.sequence(
                        Actions.delay(TRANSITION_IN_DURATION),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                CScreenMainMenu.this.transition_in_complete();
                                return true;
                            }
                        }
                )
        );
    }

    @Override
    public void transition_out(){
        // state
        this.m_state = EState.TRANSITION;

        // no more input
        this.m_input_multiplexer.removeProcessor(this.m_stage);

        // title
        this.m_label_title.addAction(
                Actions.sequence(
                        Actions.moveTo(
                                -this.m_label_title.getWidth(),
                                this.m_label_title.getY(),
                                TRANSITION_OUT_DURATION,
                                Interpolation.exp5In
                        )
                )
        );

        // buttons
        this.m_buttons_group.addAction(
                Actions.sequence(
                        Actions.moveTo(
                                -this.m_buttons_group.getWidth(),
                                0,
                                TRANSITION_OUT_DURATION,
                                Interpolation.exp5In
                        )
                )
        );

        // stage
        this.m_stage.addAction(
                Actions.sequence(
                        Actions.delay(TRANSITION_OUT_DURATION),
                        new Action() {
                            @Override
                            public boolean act(float delta) {
                                CScreenMainMenu.this.transition_out_complete();
                                return true;
                            }
                        }
                )
        );
    }

    @Override
    public void transition_in_complete(){
        this.m_state = EState.READY;
        super.transition_in_complete();

        // see if we can ask about rating the game
        // more than 0 games played, but multiple of 5, and if not already rated
        if ((this.m_game_services.get_save_state().get_total_games_played() > 0) &&
            (this.m_game_services.get_save_state().get_total_games_played() % 5 == 0) &&
            (this.m_game_services.get_save_state().get_has_rated() == false)){
            // open a ui to ask if they want to rate
            this.on_show_rate_ui();
        }
    }

    private void on_button_play(){
        // disable the group
        this.m_buttons_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // change to playing
        this.m_game_services.set_next_screen(new CScreenPlaying(this.m_game_services));
    }

    private void on_button_stats(){
        // disable the group
        this.m_buttons_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // create settings ui
        CUiStats ui = new CUiStats(
                this.m_game_services,
                new CUiStats.IListener() {
                    @Override
                    public void on_ui_stats_close(CUiStats ui) {
                        // animate stage out, close it, enable buttons
                        ui.addAction(
                                Actions.sequence(
                                        Actions.moveTo(720, ui.getY(), TRANSITION_OUT_DURATION, Interpolation.exp5In),
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                CScreenMainMenu.this.m_buttons_group.setTouchable(Touchable.enabled);
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

    private void on_button_leaderboards(){
        this.m_game_services.show_leaderboards();
    }

    private void on_button_friends(){
        this.on_show_feature_not_implemented();
    }

    private void on_button_settings(){
        // disable the group
        this.m_buttons_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // create settings ui
        CUiSettings ui = new CUiSettings(
                this.m_game_services,
                new CUiSettings.IListener() {
                    @Override
                    public void on_ui_settings_close(CUiSettings ui) {
                        // animate stage out, close it, enable buttons
                        ui.addAction(
                                Actions.sequence(
                                        Actions.moveTo(720, ui.getY(), TRANSITION_OUT_DURATION, Interpolation.exp5In),
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                CScreenMainMenu.this.m_buttons_group.setTouchable(Touchable.enabled);
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

    private void on_button_about(){
        // disable the group
        this.m_buttons_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // create settings ui
        CUiAbout ui = new CUiAbout(
                this.m_game_services,
                new CUiAbout.IListener() {
                    @Override
                    public void on_ui_about_close(CUiAbout ui) {
                        // animate stage out, close it, enable buttons
                        ui.addAction(
                                Actions.sequence(
                                        Actions.moveTo(720, ui.getY(), TRANSITION_OUT_DURATION, Interpolation.exp5In),
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                CScreenMainMenu.this.m_buttons_group.setTouchable(Touchable.enabled);
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

    private void on_button_quit(){
        // disable the group
        this.m_buttons_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        Gdx.app.exit();
    }

    private void on_show_rate_ui(){
        // disable the group
        this.m_buttons_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // create settings ui
        CUiDialogBox ui = new CUiDialogBox(
                this.m_game_services,
                CUiDialogBox.EType.YES_NO,
                "Enjoying the game?\nWould you like to rate it?",
                3.0f,
                new CUiDialogBox.IListener() {
                    @Override
                    public void on_ui_dialog_yes(CUiDialogBox ui) {
                        // animate stage out, close it, rate, enable buttons
                        ui.addAction(
                                Actions.sequence(
                                        Actions.moveTo(720, ui.getY(), TRANSITION_OUT_DURATION, Interpolation.exp5In),
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                CScreenMainMenu.this.m_buttons_group.setTouchable(Touchable.enabled);
                                                return true;
                                            }
                                        },
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                // save that they rated....and hope they actually did
                                                CScreenMainMenu.this.m_game_services.get_save_state().set_has_rated(true);
                                                CScreenMainMenu.this.m_game_services.save_save_state();

                                                // open the actual rating ui
                                                CScreenMainMenu.this.m_game_services.open_rate();
                                                return true;
                                            }
                                        },
                                        Actions.removeActor()
                                )
                        );
                    }
                    @Override
                    public void on_ui_dialog_no(CUiDialogBox ui) {
                        // animate stage out, close it, enable buttons
                        ui.addAction(
                                Actions.sequence(
                                        Actions.moveTo(720, ui.getY(), TRANSITION_OUT_DURATION, Interpolation.exp5In),
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                CScreenMainMenu.this.m_buttons_group.setTouchable(Touchable.enabled);
                                                return true;
                                            }
                                        },
                                        Actions.removeActor()
                                )
                        );
                    }
                    @Override
                    public void on_ui_dialog_ok(CUiDialogBox ui) {
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

    private void on_show_feature_not_implemented(){
        // disable the group
        this.m_buttons_group.setTouchable(Touchable.disabled);

        // sound
        this.m_game_services.play_sound(CSoundManager.ESound.BUTTON);

        // create settings ui
        CUiDialogBox ui = new CUiDialogBox(
                this.m_game_services,
                CUiDialogBox.EType.OK,
                "This feature is not yet implemented",
                3.0f,
                new CUiDialogBox.IListener() {
                    @Override
                    public void on_ui_dialog_yes(CUiDialogBox ui) {

                    }
                    @Override
                    public void on_ui_dialog_no(CUiDialogBox ui) {

                    }
                    @Override
                    public void on_ui_dialog_ok(CUiDialogBox ui) {
                        // animate stage out, close it, enable buttons
                        ui.addAction(
                                Actions.sequence(
                                        Actions.moveTo(720, ui.getY(), TRANSITION_OUT_DURATION, Interpolation.exp5In),
                                        new Action() {
                                            @Override
                                            public boolean act(float delta) {
                                                CScreenMainMenu.this.m_buttons_group.setTouchable(Touchable.enabled);
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

}
