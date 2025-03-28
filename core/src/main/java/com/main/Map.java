package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Random;

public class Map {

    private final int MAP_WIDTH;
    private final int MAP_HEIGHT;
    private Tile[][] map;


    public Map(int width, int height) {
        this.MAP_WIDTH = width;
        this.MAP_HEIGHT = height;

        this.map = new Tile[MAP_HEIGHT][MAP_WIDTH];

    }


    public void generateMap() {
        String[] tiles = {"P", "F", "M", "D"};
        Random rand = new Random();

        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                map[y][x] = new Tile(TerrainManager.getInstance().getTerrain("P"), null, null);
                Gdx.app.log("Map", map[y][x].toString());
            }
        }

        Gdx.app.log("Map", "Generated map");
    }

    public boolean checkBounds(int x, int y) {
        if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT){
            Gdx.app.log("Map", "Inbounds");
            return true;
        }
        Gdx.app.log("Map", "Outbounds");

        return false;
    }

    public Tile getTile(int x, int y) {
        return map[y][x];
    }

    public void renderMap(int TILE_SIZE, Batch batch) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int offsetX = (screenWidth - MAP_WIDTH * TILE_SIZE) / 2;
        int offsetY = (screenHeight - MAP_HEIGHT * TILE_SIZE) / 2;

        // Render non-mountain tiles first
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTile(x, y);
                String terrainId = tile.getTerrain().getId();
                float drawX = x * TILE_SIZE + offsetX;
                float drawY = y * TILE_SIZE + offsetY;

                if (!terrainId.startsWith("M")) {
                    if (terrainId.startsWith("F")) {
                        String textureKey = terrainId.endsWith("P") ? "P" : terrainId.endsWith("D") ? "D" : null;
                        if (textureKey != null) {
                            batch.draw(TerrainManager.getInstance().getTerrain(textureKey).getTexture().get(0), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        }
                    } else if (terrainId.startsWith("S")) {
                        Texture cT = checkSurroundingBaseTerrain(tile, x, y);
                        batch.draw(cT, drawX, drawY, TILE_SIZE, TILE_SIZE);

                    }else{
                        batch.draw(tile.getTerrainTexture(), drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Render mountain tiles and buildings from bottom-right to top-left
        for (int y = MAP_HEIGHT - 1; y >= 0; y--) {
            for (int x = MAP_WIDTH - 1; x >= 0; x--) {
                Tile tile = getTile(x, y);
                String terrainId = tile.getTerrain().getId();
                float drawX = x * TILE_SIZE + offsetX;
                float drawY = y * TILE_SIZE + offsetY;

                if (terrainId.startsWith("M")) {
                    String textureKey = terrainId.endsWith("P") ? "P" : terrainId.endsWith("D") ? "D" : null;
                    if (textureKey != null) {
                        batch.draw(TerrainManager.getInstance().getTerrain(textureKey).getTexture().get(0), drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                    batch.draw(tile.getTerrainTexture(), drawX, drawY, TILE_SIZE, tile.getTerrain().getTexture().get(0).getHeight() * (TILE_SIZE / 16f));
                }

                if (terrainId.equals("SS") && tile.getBaseType().equals("Desert")) {
                    batch.draw(tile.getBaseTerrainTexture(), drawX, drawY, TILE_SIZE, TILE_SIZE);
                    batch.draw(tile.getTerrainTexture(), drawX, drawY, TILE_SIZE, tile.getTerrainTexture().getHeight() * (TILE_SIZE / 16f));
                }
            }
        }

        // render units
    }
    
    private Texture checkSurroundingBaseTerrain(Tile tile, int x, int y) {
        if(checkBounds(x+1, y) && checkBounds(x-1,y)){
            String lBT = getTile(x+1,y).getTerrainBaseType();
            String rBt = getTile(x-1,y).getTerrainBaseType();
            if(!lBT.equals(rBt)){
                if(lBT.equals("Plain")){
                    if(tile.getTerrain().getJoinigTextures().get(2) != null){
                        return tile.getTerrain().getJoinigTextures().get(2);
                    }
                    return tile.getTerrainTexture();

                } else if (lBT.equals("Desert")) {
                    if(tile.getTerrain().getJoinigTextures().get(0) != null){
                        return tile.getTerrain().getJoinigTextures().get(0);
                    }
                    return tile.getTerrainTexture();
                }
            }else if(!tile.getTerrainBaseType().equals(lBT)){
               tile.setTerrainBaseType(lBT);
            }
        }
        return tile.getTerrainTexture();
    }

    // Debug functions
    public void printMapBaseTerrain(){
        for (int y = 0; y < MAP_HEIGHT; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTile(x, y);
                if (tile != null && tile.getTerrain() != null) {
                    row.append(tile.getTerrainBaseType()).append(" ");
                } else {
                    row.append("?? "); // Unknown terrain
                }
            }
            System.out.println(row.toString().trim()); // Print each row
        }
        System.out.println("------------------------------------");

    }

    public void printMapToTerminal() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTile(x, y);
                if (tile != null && tile.getTerrain() != null) {
                    row.append(tile.getTerrain().getId()).append(" ");
                } else {
                    row.append("?? "); // Unknown terrain
                }
            }
            System.out.println(row.toString().trim()); // Print each row
        }
    }



}
