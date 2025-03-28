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

    public void addTerrain(String id, String terrainName, boolean excludeTilePicker, List<Texture> texture, float defense, float speed, boolean canBeDestroyed, List<Texture> damagedTextures, List<Texture> joinigTextures) {
        terrains.put(id, new Terrain(id, terrainName, excludeTilePicker, texture, defense, speed,canBeDestroyed, damagedTextures, joinigTextures));
    }

    public Terrain[] getTerrains() {
        return terrains.values().toArray(new Terrain[0]);
    }

    public Terrain getTerrain(String id) {
        return terrains.get(id);
    }
}
