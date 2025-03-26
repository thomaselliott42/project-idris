package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static SoundManager instance;

    private final Map<String, Sound> sounds = new HashMap<>();
    private final Map<String, Music> musicTracks = new HashMap<>();
    private final Map<String, Boolean> musicTrackStatus = new HashMap<>(); // Track status per track
    private final Map<String, Boolean> soundTrackStatus = new HashMap<>(); // Track status per track

    private float soundVolume = 1.0f;
    private float musicVolume = 1.0f;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            synchronized (SoundManager.class) {
                if (instance == null) {
                    instance = new SoundManager();
                }
            }
        }
        return instance;
    }

    // Load and store a sound effect
    public void loadSound(String key, String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        if (!sounds.containsKey(key)) {
            sounds.put(key, Gdx.audio.newSound(file));
            musicTrackStatus.put(key, false); // Initialize the track as not playing

        }
    }

    // Play a sound effect
    public void playSound(String key) {
        Sound sound = sounds.get(key);
        if (sound != null) {
            sound.play(soundVolume);
            musicTrackStatus.put(key, true); // Initialize the track as not playing

        }
    }

    public boolean isSoundPlaying(String key) {
        return soundTrackStatus.getOrDefault(key, false); // Return false if the track doesn't exist
    }

    public void playSound(String key, float volume) {
        Sound sound = sounds.get(key);
        if (sound != null) {
            sound.play(volume);
            musicTrackStatus.put(key, true); // Initialize the track as not playing

        }
    }

    // Stop a sound effect
    public void stopSound(String key) {
        Sound sound = sounds.get(key);
        if (sound != null) {
            sound.stop();
            musicTrackStatus.put(key, false); // Initialize the track as not playing
        }
    }

    // Load and store a music track
    public void loadMusic(String key, String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        if (!musicTracks.containsKey(key)) {
            Music music = Gdx.audio.newMusic(file);
            music.setVolume(musicVolume);
            musicTracks.put(key, music);
            musicTrackStatus.put(key, false); // Initialize the track as not playing

        }
    }

    // Play a music track with an option to loop
    public void playMusic(String key, boolean loop) {
        Music music = musicTracks.get(key);
        if (music != null) {
            music.setLooping(loop);
            music.play();
            musicTrackStatus.put(key, true); // Mark the track as stopped

        }
    }

    // Pause a specific music track
    public void pauseMusic(String key) {
        Music music = musicTracks.get(key);
        if (music != null) {
            music.pause();
            musicTrackStatus.put(key, false); // Mark the track as stopped
        }
    }

    // Resume a specific music track
    public void resumeMusic(String key) {
        Music music = musicTracks.get(key);
        if (music != null) {
            music.play();
        }
    }

    // Stop a specific music track
    public void stopMusic(String key) {
        Music music = musicTracks.get(key);
        if (music != null) {
            music.stop();
            musicTrackStatus.put(key, false); // Mark the track as stopped
        }
    }

    // Set volume for sound effects
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0, Math.min(volume, 1)); // Clamp between 0-1
    }

    // Set volume for all music tracks
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(volume, 1));
        for (Music music : musicTracks.values()) {
            music.setVolume(musicVolume);
        }
    }

    public boolean isMusicPlaying(String key) {
        return musicTrackStatus.getOrDefault(key, false); // Return false if the track doesn't exist
    }

    // Dispose of all sounds and music
    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();

        for (Music music : musicTracks.values()) {
            music.dispose();
        }
        musicTracks.clear();
    }
}
