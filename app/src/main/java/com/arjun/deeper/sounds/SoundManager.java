package com.arjun.deeper.sounds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;

import com.arjun.deeper.DeeperApplication;
import com.arjun.deeper.R;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    public enum Sound {
        APPLAUSE,
        WHOO,
        CRICKETS,
        LAUGHING,
        SARCASTIC_YAY,
        RIGHT_CLICK,
        WRONG_CLICK
    }

    private static SoundManager instance;

    private SoundPool soundPool;
    private AsyncTask asyncSoundLoader;

    private Map<Sound, Integer> sounds;

    public static void initialize() {
        if (instance == null) {
            instance = new SoundManager();
            instance.loadSounds();
        }
    }

    public static void release() {
        if (instance != null) {
            instance.releaseSounds();
            instance = null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadSounds() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        asyncSoundLoader = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Context context = DeeperApplication.getContext();
                sounds = new HashMap<>();
                sounds.put(Sound.APPLAUSE, soundPool.load(context, R.raw.applause, 1));
                sounds.put(Sound.WHOO, soundPool.load(context, R.raw.whooo, 1));
                sounds.put(Sound.CRICKETS, soundPool.load(context, R.raw.crickets, 1));
                sounds.put(Sound.LAUGHING, soundPool.load(context, R.raw.laughing, 1));
                sounds.put(Sound.SARCASTIC_YAY, soundPool.load(context, R.raw.sarcastic_yay, 1));
                sounds.put(Sound.RIGHT_CLICK, soundPool.load(context, R.raw.menu_click, 1));
                sounds.put(Sound.WRONG_CLICK, soundPool.load(context, R.raw.wrong_click, 1));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                asyncSoundLoader = null;
            }
        }.execute();
    }

    private void releaseSounds() {
        if (asyncSoundLoader != null) {
            asyncSoundLoader.cancel(true);
            asyncSoundLoader = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void playSound(int soundId) {
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }

    public static void playSound(Sound sound) {
        if (instance != null)
            instance.playSound(instance.sounds.get(sound));
    }
}
