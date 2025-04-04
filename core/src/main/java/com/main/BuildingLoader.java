package com.main;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class BuildingLoader {
    private static final String BUILDINGS_JSON_PATH = "assets/data/terrain.json";
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void loadBuildings() {
        try {
            // Read JSON file and parse it into a JsonNode
            JsonNode rootNode = objectMapper.readTree(new File(BUILDINGS_JSON_PATH));

            // Load Capital buildings
            JsonNode capitalsNode = rootNode.path("capitals");
            Iterator<JsonNode> capitalIterator = capitalsNode.elements();
            while (capitalIterator.hasNext()) {
                JsonNode capitalNode = capitalIterator.next();
                String id = capitalNode.path("id").asText();
                String name = capitalNode.path("name").asText();
                String faction = capitalNode.path("faction").asText();
                float defense = capitalNode.path("defense").floatValue();
                float speed = capitalNode.path("speed").floatValue();
                boolean canHeal = capitalNode.path("canHeal").asBoolean();
                boolean canRefuel = capitalNode.path("canReful").asBoolean();
                boolean canBeDestroyed = capitalNode.path("canBeDestroyed").asBoolean();

                Gdx.app.log("BuildingLoader", "Loading capital: " + id + " (" + name + ")");

                // Create Capital object
                Capital capital = new Capital(name, id, defense, speed, canHeal, canRefuel, canBeDestroyed, faction);

                // Add Capital to BuildingManager
                BuildingManager.getInstance().addBuilding(id, name, id, defense, speed, canHeal, canRefuel, canBeDestroyed, null, null, faction);
            }

            // Load regular buildings
            JsonNode buildingsNode = rootNode.path("buildings");
            Iterator<JsonNode> buildingIterator = buildingsNode.elements();
            while (buildingIterator.hasNext()) {
                JsonNode buildingNode = buildingIterator.next();
                String id = buildingNode.path("id").asText();
                String name = buildingNode.path("name").asText();
                float defense = buildingNode.path("defense").floatValue();
                float speed = buildingNode.path("speed").floatValue();
                boolean canHeal = buildingNode.path("canHeal").asBoolean();
                boolean canRefuel = buildingNode.path("canReful").asBoolean();
                boolean canBeDestroyed = buildingNode.path("canBeDestroyed").asBoolean();

                Gdx.app.log("BuildingLoader", "Loading buildings: " + id + " (" + name + ")");

                // Create Building object
                Building building = new Building(name, id, defense, speed, canHeal, canRefuel, canBeDestroyed);

                // Add Building to BuildingManager
                BuildingManager.getInstance().addBuilding(id, name, id, defense, speed, canHeal, canRefuel, canBeDestroyed, null, null, null);

            }
        } catch (IOException e) {
            Gdx.app.log("BuildingLoader", "Failed to load buildings");
            e.printStackTrace();
        }

        Gdx.app.log("BuildingLoader", "Buildings loaded successfully!");

    }
}
