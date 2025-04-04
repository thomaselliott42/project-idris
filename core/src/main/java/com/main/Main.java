package com.main;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        // Start with the loading screen
        AtlasManager.getInstance();
        //setScreen(new shaderExample(this));
        setScreen(new LoadingScreen(this));
    }
}
