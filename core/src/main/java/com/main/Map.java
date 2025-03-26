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
                map[y][x] = new Tile(TerrainManager.getInstance().getTerrain(tiles[0]), null, null);
                Gdx.app.log("Map", map[y][x].toString());
            }
        }

        Gdx.app.log("Map", "Generated map");
    }

    public boolean checkBounds(int x, int y) {
        if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT){
            return true;
        }
        return false;
    }

    public Tile getTile(int x, int y) {
        return map[y][x];
    }

    public void renderMap(int TILE_SIZE, Batch batch, Texture fire) {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTile(x, y);

                // Position tiles, offset to center the map
                float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;

                // Render non-mountain tiles
                if (!tile.getTerrain().getId().startsWith("M")) {

                    if(tile.getTerrain().getId().startsWith("F")) {
                        if (tile.getTerrain().getId().endsWith("P")) {
                            batch.draw(TerrainManager.getInstance().getTerrain("P").getTexture().get(0), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        }else if (tile.getTerrain().getId().endsWith("D")) {
                            batch.draw(TerrainManager.getInstance().getTerrain("D").getTexture().get(0), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        }
                    }
                    batch.draw(tile.getTerrainTexture(), drawX, drawY, TILE_SIZE, TILE_SIZE);

                    if(tile.hasFire()){
                        batch.draw(fire, drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

// Then, render the mountain tiles starting from bottom-right to top-left
        for (int y = MAP_HEIGHT - 1; y >= 0; y--) { // Start from the bottom row
            for (int x = MAP_WIDTH - 1; x >= 0; x--) { // Start from the rightmost column
                Tile tile = getTile(x, y);
                if (tile.getTerrain().getId().startsWith("M")) {
                    if (tile.getTerrain().getId().endsWith("P")) {
                        float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                        float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                        batch.draw(TerrainManager.getInstance().getTerrain("P").getTexture().get(0), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        batch.draw(tile.getTerrainTexture(), drawX, drawY, TILE_SIZE, tile.getTerrain().getTexture().get(0).getHeight() * (TILE_SIZE / 16f));
                    }
                    else if (tile.getTerrain().getId().endsWith("D")) {
                        float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                        float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                        batch.draw(TerrainManager.getInstance().getTerrain("D").getTexture().get(0), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        batch.draw(tile.getTerrainTexture(), drawX, drawY, TILE_SIZE, tile.getTerrain().getTexture().get(0).getHeight() * (TILE_SIZE / 16f));
                    }

                }
                if(tile.getTerrain().getId().equals("SS") && tile.getBaseType().equals("Desert")) {
                    Gdx.app.log("Map", "SS desert");
                        float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                        float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                        batch.draw(tile.getBaseTerrainTexture(), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        batch.draw(tile.getTerrainTexture(), drawX, drawY, TILE_SIZE, tile.getTerrainTexture().getHeight() * (TILE_SIZE / 16f));

                    }
            }
        }
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
