package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TerrainLoader {

    private static final String TERRAIN_JSON_PATH = "assets/terrain.json";

    public static void loadTerrains() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            TerrainDataWrapper data = objectMapper.readValue(new File(TERRAIN_JSON_PATH), TerrainDataWrapper.class);

            TerrainManager terrainManager = TerrainManager.getInstance();

            for (TerrainData terrainData : data.terrains) {
                Texture texture = new Texture(terrainData.texturePath);
                List<Texture> damagedTextures = loadDamagedTextures(terrainData); // Load damaged textures
                terrainManager.addTerrain(terrainData.id, terrainData.terrainName, texture, terrainData.defense, terrainData.speed, terrainData.canBeDestroyed, damagedTextures);
            }

            Gdx.app.log("TerrainLoader", "Terrains loaded successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Texture> loadDamagedTextures(TerrainData data) {
        List<Texture> dT = new ArrayList<>();
        if (data.canBeDestroyed) {
            // Loop through the damaged textures paths and add to the list
            for (String damagedTexture : data.damagedTextures) {
                dT.add(new Texture(damagedTexture));
            }
        }
        return dT;
    }

    // Wrapper class for JSON data
    private static class TerrainDataWrapper {
        public List<TerrainData> terrains;
    }

    // Terrain data structure class
    private static class TerrainData {
        public String id;
        public String terrainName;
        public String texturePath;
        public float defense;
        public float speed;
        public boolean canBeDestroyed;
        public String[] damagedTextures; // Array of damaged textures
    }
}
