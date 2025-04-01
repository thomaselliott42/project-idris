package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TerrainLoader {

    private static final String TERRAIN_JSON_PATH = "assets/data/terrain.json";

    public static void loadTerrains() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            TerrainDataWrapper data = objectMapper.readValue(new File(TERRAIN_JSON_PATH), TerrainDataWrapper.class);
            TerrainManager terrainManager = TerrainManager.getInstance();

            for (TerrainData terrainData : data.terrains) {

                List<Texture> damagedTextures = loadDamagedTextures(terrainData);
                List<Texture> joinigTextures = loadJoinigTextures(terrainData);

                // Log loaded terrains for debugging
                Gdx.app.log("TerrainLoader", "Loading terrain: " + terrainData.id + " (" + terrainData.terrainName + ")");

                // Register terrain
                terrainManager.addTerrain(
                    terrainData.id,
                    terrainData.terrainName,
                    terrainData.excludeTilePicker,
                    terrainData.defense,
                    terrainData.speed,
                    terrainData.canBeDestroyed,
                    damagedTextures,
                    joinigTextures
                );
            }

            Gdx.app.log("TerrainLoader", "Terrains loaded successfully!");

        } catch (IOException e) {
            Gdx.app.error("TerrainLoader", "Failed to load terrains", e);
        }
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

    private static List<Texture> loadJoinigTextures(TerrainData data) {
        List<Texture> jT = new ArrayList<>();
        {
            for (String joiningTexture : data.joinTexturePath) {
                if(!joiningTexture.equals("")){
                    jT.add(new Texture(joiningTexture));

                }else{
                    jT.add(null);
                }
            }
        }
        return jT;
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
        public float defense;
        public float speed;
        public boolean canBeDestroyed;
        public List<String> damagedTextures;
        public List<String> joinTexturePath;
    }
}
