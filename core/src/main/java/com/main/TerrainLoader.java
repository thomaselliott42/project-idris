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
                List<Texture> textures = loadTextures(terrainData.texturePath);
                List<Texture> damagedTextures = loadDamagedTextures(terrainData);

                // Log loaded terrains for debugging
                Gdx.app.log("TerrainLoader", "Loading terrain: " + terrainData.id + " (" + terrainData.terrainName + ")");

                // Register terrain
                terrainManager.addTerrain(
                    terrainData.id,
                    terrainData.terrainName,
                    terrainData.excludeTilePicker,
                    textures,
                    terrainData.defense,
                    terrainData.speed,
                    terrainData.canBeDestroyed,
                    damagedTextures
                );
            }

            Gdx.app.log("TerrainLoader", "Terrains loaded successfully!");

        } catch (IOException e) {
            Gdx.app.error("TerrainLoader", "Failed to load terrains", e);
        }
    }

    private static List<Texture> loadTextures(Object textureData) {
        List<Texture> textures = new ArrayList<>();

        if (textureData instanceof String) {
            textures.add(new Texture((String) textureData));
        } else if (textureData instanceof List) {
            for (String path : (List<String>) textureData) {
                textures.add(new Texture(path));
            }
        }

        return textures;
    }

    private static List<Texture> loadDamagedTextures(TerrainData data) {
        List<Texture> dT = new ArrayList<>();
        if (data.canBeDestroyed) {
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
        public boolean excludeTilePicker;
        public Object texturePath; // Supports both String and List<String>
        public float defense;
        public float speed;
        public boolean canBeDestroyed;
        public List<String> damagedTextures;
    }
}
