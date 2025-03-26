package com.main;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerrainManager {
    private static TerrainManager instance;
    private Map<String, Terrain> terrains;

    private TerrainManager() {
        terrains = new HashMap<>();
    }

    public static TerrainManager getInstance() {
        if (instance == null) {
            instance = new TerrainManager();
        }
        return instance;
    }

    public void addTerrain(String id, String terrainName, Texture texture, float defense, float speed, boolean canBeDestroyed, List<Texture> damagedTextures) {
        terrains.put(id, new Terrain(id, terrainName, texture, defense, speed,canBeDestroyed, damagedTextures));
    }

    public Terrain[] getTerrains() {
        return terrains.values().toArray(new Terrain[0]);
    }

    public Terrain getTerrain(String id) {
        return terrains.get(id);
    }
}
