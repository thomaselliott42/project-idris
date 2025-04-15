package com.main;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {

        // load textures
        TerrainLoader.loadTerrains();
        BuildingLoader.loadBuildings();

        // load shaders
        ShaderManager.getInstance().loadShader("sea","shaders/sea/defaultSea.vert","shaders/sea/sea.frag");
        ShaderManager.getInstance().loadShader("buildingColourChange","shaders/buildings/defaultColourChange.vert","shaders/buildings/colourChange.frag");

        setScreen(new SplashScreen(this));
        //setScreen(new MainMenu(this));
        //setScreen(new LoadingScreen(this));
    }
}
