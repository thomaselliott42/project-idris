package com.main;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {

        TerrainLoader.loadTerrains();

        setScreen(new MapMaker());
    }
}
