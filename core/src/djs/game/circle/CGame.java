package djs.game.circle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import djs.game.circle.loading.CScreenLoading;

public class CGame extends Game implements IGameServices{
	// constants
	private static final String SAVE_STATE_FILENAME = "save_state";

	// variables
	private IPlatformServices m_platform_services;
	private CAssetManager m_asset_manager;
	private CSoundManager m_sound_manager;
	private CSaveState m_save_state;
	private CScreen m_current_screen;
	private CScreen m_next_screen;

	// functions
	public CGame(IPlatformServices platform_services) {
		this.m_platform_services = platform_services;
		this.m_asset_manager = null;
		this.m_sound_manager = null;
		this.m_current_screen = null;
		this.m_next_screen = null;
	}

	@Override
	public void create() {
		// load the save state
		this.load_save_state();
		this.m_save_state.set_games_since_ad(0);

		// asset manager
		this.m_asset_manager = new CAssetManager();

		// sound manager
		this.m_sound_manager = new CSoundManager(this);
		this.m_sound_manager.start_music();

		// set the next screen as the loading screen
		this.set_next_screen(new CScreenLoading(this));
	}

	@Override
	public void render(){
		// go to next screen if needed
		if (this.m_current_screen == null) {
			this.m_current_screen = this.m_next_screen;
			this.m_next_screen = null;
			if (this.m_current_screen != null){
				this.setScreen(this.m_current_screen);
				this.m_current_screen.transition_in();
			}
		}

		// music
		this.m_sound_manager.handle_music();

		// super me
		super.render();
	}

	// igameservices
	public CAssetManager get_asset_manager(){
		return this.m_asset_manager;
	}
	public void signal_transition_out_complete(){
		// current screen done
		this.setScreen(null);
		this.m_current_screen.dispose();
		this.m_current_screen = null;

		// render() will pick up next screen
	}
	public void set_next_screen(CScreen screen){
		// set next screen
		this.m_next_screen = screen;

		// transition out current
		if (this.m_current_screen != null) {
			this.m_current_screen.transition_out();
		}
	}
	public CSaveState get_save_state(){
		return this.m_save_state;
	}
	public void save_save_state(){
		Gdx.app.log("SAVE", Gdx.app.getType().name());
		if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
			// turn the save state into json
			Json json = new Json();
			String text = json.toJson(this.m_save_state);

			// write it to the save file
			FileHandle fh = Gdx.files.local(SAVE_STATE_FILENAME);
			fh.writeString(text, false);
		}
	}
	public void play_sound(CSoundManager.ESound sound){
		this.m_sound_manager.play(sound);
	}
	public void open_rate(){
		this.m_platform_services.open_rate();
	}
	public void show_ad(IAdListener listener){
		this.m_platform_services.show_ad(listener);
	}
	public void show_leaderboards(){
		this.m_platform_services.show_leaderboards();
	}
	public void update_leaderboards(){
		this.m_platform_services.update_leaderboards();
	}
	// igameservices end

	private void load_save_state(){
		FileHandle fh = Gdx.files.local(SAVE_STATE_FILENAME);

		// check if it exists
		if (fh.exists()) {
			// it does exist so get the save state from it in json
			String text = fh.readString();
			Json json = new Json();
			this.m_save_state = json.fromJson(CSaveState.class, text);
		}
		else{
			// does not exist so create new save state
			this.m_save_state = new CSaveState();
		}
	}
}
