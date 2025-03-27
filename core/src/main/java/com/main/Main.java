package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {

        SoundManager.getInstance().loadSound("fs","fireStart.mp3");
        SoundManager.getInstance().loadSound("fg","fireGoing.mp3");

        SoundManager.getInstance().loadSound("tf","taskFinished.mp3");
        SoundManager.getInstance().playSound("tf");
        TerrainLoader.loadTerrains();

        setScreen(new MapMaker());
    }
}
