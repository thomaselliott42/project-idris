package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {

        TerrainLoader.loadTerrains();

        setScreen(new MapMaker());
    }
}
