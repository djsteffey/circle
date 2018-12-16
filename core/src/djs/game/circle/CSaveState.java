package djs.game.circle;

import com.badlogic.gdx.graphics.Color;

public class CSaveState{
    // constants


    // variables
    private int m_player_color;
    private int m_world_color;
    private int m_spike_color;
    private int m_text_color;
    private int m_text_glow_color;
    private float m_music_volume;
    private float m_sound_volume;

    private long m_total_games_played;
    private long m_total_revolutions;
    private long m_total_spikes_jumped;
    private long m_total_scores;
    private long m_max_revolutions;
    private long m_max_spikes_jumped;
    private long m_max_score;

    private boolean m_has_rated;
    private boolean m_ad_free;
    private int m_games_since_ad;

    private long m_outbox_total_games_played;
    private long m_outbox_total_revolutions;
    private long m_outbox_total_spikes_jumped;
    private long m_outbox_total_scores;
    private long m_outbox_max_revolutions;
    private long m_outbox_max_spikes_jumped;
    private long m_outbox_max_score;

    // functions
    public CSaveState(){
        this.m_player_color = Color.rgba8888(255 / 255.0f, 20 / 255.0f, 175 / 255.0f, 1.0f);
        this.m_world_color = Color.rgba8888(57 / 255.0f, 255 / 255.0f, 20 / 255.0f, 1.0f);
        this.m_spike_color = Color.rgba8888(Color.RED);
        this.m_text_color = 0xf0fff0ff;
        this.m_text_glow_color = 0xff000080;
        this.m_music_volume = 0.50f;
        this.m_sound_volume = 1.00f;

        this.m_total_games_played = 0;
        this.m_total_revolutions = 0;
        this.m_total_spikes_jumped = 0;
        this.m_total_scores = 0;
        this.m_max_revolutions = 0;
        this.m_max_spikes_jumped = 0;
        this.m_max_score = 0;

        this.m_has_rated = false;
        this.m_ad_free = false;
        this.m_games_since_ad = 0;

        this.m_outbox_total_games_played = 0;
        this.m_outbox_total_revolutions = 0;
        this.m_outbox_total_spikes_jumped = 0;
        this.m_outbox_total_scores = 0;
        this.m_outbox_max_revolutions = 0;
        this.m_outbox_max_spikes_jumped = 0;
        this.m_outbox_max_score = 0;
    }

    public int get_player_color(){
        return this.m_player_color;
    }

    public void set_player_color(int color){
        this.m_player_color = color;
    }

    public int get_world_color(){
        return this.m_world_color;
    }

    public void set_world_color(int color){
        this.m_world_color = color;
    }

    public int get_spike_color(){
        return this.m_spike_color;
    }

    public void set_spike_color(int color){
        this.m_spike_color = color;
    }

    public int get_text_color(){
        return this.m_text_color;
    }

    public int get_text_glow_color(){
        return this.m_text_glow_color;
    }

    public float get_music_volume(){
        return this.m_music_volume;
    }

    public void set_music_volume(float volume){
        this.m_music_volume = volume;
    }

    public float get_sound_volume(){
        return this.m_sound_volume;
    }

    public void set_sound_volume(float volume){
        this.m_sound_volume = volume;
    }

    public long get_total_games_played(){
        return this.m_total_games_played;
    }

    public void set_total_games_played(long games){
        this.m_total_games_played = games;
        this.m_outbox_total_games_played = games;
    }

    public long get_total_revolutions(){
        return this.m_total_revolutions;
    }

    public void set_total_revolutions(long revolutions){
        this.m_total_revolutions = revolutions;
        this.m_outbox_total_revolutions = revolutions;
    }

    public long get_total_spikes_jumped(){
        return this.m_total_spikes_jumped;
    }

    public void set_total_spikes_jumped(long spikes){
        this.m_total_spikes_jumped = spikes;
        this.m_outbox_total_spikes_jumped = spikes;
    }

    public long get_total_scores(){
        return this.m_total_scores;
    }

    public void set_total_scores(long scores){
        this.m_total_scores = scores;
        this.m_outbox_total_scores = scores;
    }

    public long get_max_revolutions(){
        return this.m_max_revolutions;
    }

    public void set_max_revolutions(long revolutions){
        this.m_max_revolutions = revolutions;
        this.m_outbox_max_revolutions = revolutions;
    }

    public long get_max_spikes_jumped(){
        return this.m_max_spikes_jumped;
    }

    public void set_max_spikes_jumped(long spikes){
        this.m_max_spikes_jumped = spikes;
        this.m_outbox_max_spikes_jumped = spikes;
    }

    public long get_max_score(){
        return this.m_max_score;
    }

    public void set_max_score(long score){
        this.m_max_score = score;
        this.m_outbox_max_score = score;
    }

    public boolean get_has_rated(){
        return this.m_has_rated;
    }

    public void set_has_rated(boolean rated){
        this.m_has_rated = rated;
    }

    public boolean get_ad_free(){
        return this.m_ad_free;
    }

    public void set_ad_free(boolean ad_free){
        this.m_ad_free = ad_free;
    }

    public int get_games_since_ad(){
        return this.m_games_since_ad;
    }

    public void set_games_since_ad(int value){
        this.m_games_since_ad = value;
    }

    public long get_outbox_total_games_played(){
        return this.m_outbox_total_games_played;
    }

    public long get_outbox_total_revolutions(){
        return this.m_outbox_total_revolutions;
    }

    public long get_outbox_total_spikes_jumped(){
        return this.m_outbox_total_spikes_jumped;
    }

    public long get_outbox_total_scores(){
        return this.m_outbox_total_scores;
    }

    public long get_outbox_max_revolutions(){
        return this.m_outbox_max_revolutions;
    }

    public long get_outbox_max_spikes_jumped(){
        return this.m_outbox_max_spikes_jumped;
    }

    public long get_outbox_max_score(){
        return this.m_outbox_max_score;
    }

    public void clear_outbox(){
        this.m_outbox_total_games_played = 0;
        this.m_outbox_total_revolutions = 0;
        this.m_outbox_total_spikes_jumped = 0;
        this.m_outbox_total_scores = 0;
        this.m_outbox_max_revolutions = 0;
        this.m_outbox_max_spikes_jumped = 0;
        this.m_outbox_max_score = 0;
    }
}
