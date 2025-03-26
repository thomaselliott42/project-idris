package com.main;

import com.badlogic.gdx.Gdx;

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


}
