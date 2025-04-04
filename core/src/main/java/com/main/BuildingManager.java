package com.main;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingManager {
    private static BuildingManager instance;
    private Map<String, Building> buildings;

    // Private constructor to prevent instantiation
    private BuildingManager() {
        buildings = new HashMap<>();
    }

    // Singleton pattern to get the instance of the manager
    public static BuildingManager getInstance() {
        if (instance == null) {
            instance = new BuildingManager();
        }
        return instance;
    }

    // Method to add a building to the map
    public void addBuilding(String id, String name, String textureId, float defense, float speed, boolean canHeal, boolean canRefuel, boolean canBeDestroyed, List<Texture> damagedTextures, List<Texture> joiningTextures, String faction) {
        // Create a Building or Capital object based on the provided information
        Building building;
        if (faction != null && !faction.isEmpty()) {
            // This is a Capital, create a Capital object
            building = new Capital(name, textureId, defense, speed, canHeal, canRefuel, canBeDestroyed, faction);
        } else {
            // This is a regular Building
            building = new Building(name,textureId,defense,speed,canHeal,canRefuel,canBeDestroyed);
        }

        // Add the building to the map
        buildings.put(id, building);
    }

    // Method to get all buildings
    public Building[] getBuildings() {
        return buildings.values().toArray(new Building[0]);
    }

    // Method to get a specific building by ID
    public Building getBuilding(String id) {
        return buildings.get(id);
    }
}
