package com.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Main extends Game {
    @Override
    public void create() {
        // Start with the loading screen
        setScreen(new LoadingScreen(this));
    }
}
