package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AtlasManager {
    private static AtlasManager instance;

    // singelton that contains cases to return textureregion
    private TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas/terrain.atlas"));

    public static AtlasManager getInstance() {
        if (instance == null) {
            instance = new AtlasManager();
        }
        return instance;
    }

    public TextureRegion getTexture(String terrainType) {
        switch (terrainType) {
            case "P":
                return atlas.findRegion("plain");
            case "D":
                return atlas.findRegion("desert");
            case "W":
                return atlas.findRegion("winter");
            case "FP":
                return atlas.findRegion("plainForest");
            case "FD":
                return atlas.findRegion("desertForest");
            case "MP":
                return atlas.findRegion("plainMountain");
            case "MD":
                return atlas.findRegion("desertMountain");
            default:
                return atlas.findRegion(terrainType);
        }
    }


}
