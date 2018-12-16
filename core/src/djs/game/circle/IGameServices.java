package djs.game.circle;

public interface IGameServices {
    CAssetManager get_asset_manager();
    void signal_transition_out_complete();
    void set_next_screen(CScreen screen);
    CSaveState get_save_state();
    void save_save_state();
    void play_sound(CSoundManager.ESound sound);
    void open_rate();
    void show_ad(IAdListener listener);
    void show_leaderboards();
    void update_leaderboards();
}
