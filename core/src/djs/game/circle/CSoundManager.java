package djs.game.circle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class CSoundManager implements Music.OnCompletionListener{
    private enum EMusic {
        MUSIC_00{
            @Override
            public float length(){
                return 270.0f;
            }
            @Override
            public String filename(){
                return "music/jobromedia.mp3";
            }
        },
        MUSIC_01{
            @Override
            public float length(){
                return 30.0f;
            }
            @Override
            public String filename(){
                return "music/machine.mp3";
            }
        },
        MUSIC_02{
            @Override
            public float length(){
                return 106.0f;
            }
            @Override
            public String filename(){
                return "music/matthew_pablo.mp3";
            }
        },
        MUSIC_03{
            @Override
            public float length(){
                return 84.0f;
            }
            @Override
            public String filename(){
                return "music/mrpoly.mp3";
            }
        },
        MUSIC_04{
            @Override
            public float length(){
                return 120.0f;
            }
            @Override
            public String filename(){
                return "music/neocrey.mp3";
            }
        },
        MUSIC_05{
            @Override
            public float length(){
                return 54.0f;
            }
            @Override
            public String filename(){
                return "music/nicole_marie_t.mp3";
            }
        },
        MUSIC_06{
            @Override
            public float length(){
                return 98.0f;
            }
            @Override
            public String filename(){
                return "music/snabisch - 00.mp3";
            }
        },
        MUSIC_07{
            @Override
            public EMusic next(){
                // roll back over to the first one
                return values()[0];
            }
            @Override
            public float length(){
                return 88.0f;
            }
            @Override
            public String filename(){
                return "music/snabisch - 01.mp3";
            }
        },
        MUSIC_08{
            @Override
            public EMusic next(){
                // roll back over to the first one
                return values()[0];
            }
            @Override
            public float length(){
                return 110.0f;
            }
            @Override
            public String filename(){
                return "music/snabisch - 02.mp3";
            }
        };

        public EMusic next(){
            return values()[ordinal() + 1];
        }
        public static EMusic random(Random rand) { return values()[rand.nextInt(EMusic.values().length)]; }
        public abstract float length();
        public abstract String filename();
    }
    public enum ESound{
        BUTTON
    }

    // constants
    private static final float MUSIC_FADE_TIME = 4.0f;

    // variables
    private IGameServices m_game_services;
    private Map<ESound, Sound> m_sounds;
    private Map<ESound, Long> m_sound_last_id;
    private Music m_background_music;
    private EMusic m_background_music_enum;

    // functions
    public CSoundManager(IGameServices game_services){
        this.m_game_services = game_services;

        // load sounds
        this.m_sounds = new EnumMap<ESound, Sound>(ESound.class);
        this.m_sounds.put(ESound.BUTTON, Gdx.audio.newSound(Gdx.files.internal("sound/lfa.wav")));

        // looping
        this.m_sound_last_id = new EnumMap<ESound, Long>(ESound.class);
        this.m_sound_last_id.put(ESound.BUTTON, (long)0);
    }

    public void play(ESound sound){
        this.play(sound, 1.0f);
    }

    public void play(ESound sound, float volume){
        this.play(sound, volume, false);
    }

    public void play(ESound sound, float volume, boolean loop){
        if (this.m_game_services.get_save_state().get_sound_volume() == 0.0f){
            return;
        }
        if (this.m_sounds.get(sound) != null) {
            long id = this.m_sounds.get(sound).play(this.m_game_services.get_save_state().get_sound_volume() * volume);
            this.m_sounds.get(sound).setLooping(id, loop);
            this.m_sound_last_id.put(sound, id);
        }
    }

    public void stop(ESound sound){
        this.stop(sound, false);
    }

    public void stop(ESound sound, boolean immediate) {
        if (immediate){
            this.m_sounds.get(sound).stop();
        }
        else {
            this.m_sounds.get(sound).setLooping(
                    this.m_sound_last_id.get(sound),
                    false
            );
        }
    }

    public void start_music(){
        // play the music
        if (this.m_background_music != null){
            // already have it loaded so just play it
            this.m_background_music.play();
        }
        else {
            // load up a random one and play it
            this.m_background_music_enum = EMusic.random(new Random());
            this.m_background_music = Gdx.audio.newMusic(Gdx.files.internal(this.m_background_music_enum.filename()));
            this.m_background_music.setLooping(false);
            this.m_background_music.setVolume(0.0f);
            this.m_background_music.setOnCompletionListener(this);
            this.m_background_music.play();
        }
    }

    public void handle_music(){
        // get the current time of the music
        float position = this.m_background_music.getPosition();

        // see if we should be increasing sound
        if (position < MUSIC_FADE_TIME){
            // increasing sound
            this.m_background_music.setVolume((position / MUSIC_FADE_TIME) * this.m_game_services.get_save_state().get_music_volume());
            return;
        }

        // see if we should be decreasing sound
        float diff = this.m_background_music_enum.length() - position;
        if (diff < MUSIC_FADE_TIME){
            // decreasing sound
            this.m_background_music.setVolume((diff / MUSIC_FADE_TIME) * this.m_game_services.get_save_state().get_music_volume());
            return;
        }

        // somewhere in the middle for full sound
        this.m_background_music.setVolume(1.0f * this.m_game_services.get_save_state().get_music_volume());
    }

    @Override
    public void onCompletion(Music music) {
        try {
            // music finished playing
            if (this.m_background_music != null) {
                this.m_background_music.dispose();
                this.m_background_music = null;
            }

            // load and play the next
            this.m_background_music_enum = this.m_background_music_enum.next();
            this.m_background_music = Gdx.audio.newMusic(Gdx.files.internal(this.m_background_music_enum.filename()));
            this.m_background_music.setLooping(false);
            this.m_background_music.setVolume(0.0f);
            this.m_background_music.setOnCompletionListener(this);
            this.m_background_music.play();
        }
        catch (Exception e){
            e.printStackTrace();
            // try the next song
            this.onCompletion(null);
        }
    }
}