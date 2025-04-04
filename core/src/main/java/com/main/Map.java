package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.*;

public class Map {

    private final int MAP_WIDTH;
    private final int MAP_HEIGHT;
    private Tile[][] map;
    private AtlasManager terrainAtlas;
    private ShaderManager shaderManager;
    List<int[]> mergedCoords = new ArrayList<>();

    // shaders
    private float time = 0f;;


    // testing
    private float alpha = 1.0f;
    private Texture plainForest = new Texture(Gdx.files.internal("plainForest4x4.png"));
    private Texture plain = new Texture(Gdx.files.internal("plain4x4.png"));
    private Texture desert = new Texture(Gdx.files.internal("desert4x4.png"));



    // debuging
    private boolean mergeTiles = false;
    private int counterTexture = 0;
    private int counterBaseTexture = 0;
    private int drawCallCounter = 0;

    public Map(int width, int height) {
        this.MAP_WIDTH = width;
        this.MAP_HEIGHT = height;
        this.terrainAtlas = AtlasManager.getInstance();
        this.shaderManager = ShaderManager.getInstance();

        this.map = new Tile[MAP_HEIGHT][MAP_WIDTH];
    }


    public void generateMap() {
        String[] tiles = {"P", "F", "M", "D"};
        Random rand = new Random();

        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                map[y][x] = new Tile(TerrainManager.getInstance().getTerrain("P"), null, null);
                //Gdx.app.log("Map", map[y][x].toString());
            }
        }

        Gdx.app.log("Map", "Generated map");
    }

    public boolean checkBounds(int x, int y) {
        if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
            return true;
        }
        return false;
    }

    public Tile getTile(int x, int y) {
        return map[y][x];
    }

    public void renderMap(int TILE_SIZE, Batch batch, boolean is3DView, int startX, int startY, int endX, int endY, float delta) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int offsetX = (screenWidth - MAP_WIDTH * TILE_SIZE) / 2;
        int offsetY = (screenHeight - MAP_HEIGHT * TILE_SIZE) / 2;
        time += Gdx.graphics.getDeltaTime(); // Keep increasing time

        render2DMap(TILE_SIZE, batch, offsetX, offsetY, startX, startY, endX, endY);

//        if (is3DView) {
//            render3DMap(TILE_SIZE, batch, offsetX, offsetY, startX, startY, endX, endY);
//        } else {
////            alpha = CameraManager.getInstance().getZoomLevel() - 0.000001f;
//
//            render2DMap(TILE_SIZE, batch, offsetX, offsetY, startX, startY, endX, endY);
//        }

//        Gdx.app.log("Map", "3d :" + is3DView);

    }

    public void render2DMap(int TILE_SIZE, Batch batch, int offsetX, int offsetY, int startX, int startY, int endX, int endY) {

        TextureRegion baseTexture = null;
        TextureRegion terrainTexture = null;
        String previousTextureId = null;
        String previousBaseTextureId = null;
        counterTexture = 0;
        counterBaseTexture = 0;
        drawCallCounter = 0;


        float zoomLevel = CameraManager.getInstance().getZoomLevel();
        mergeTiles = zoomLevel > 1; // D
        mergedCoords.clear();

//        batch.setColor(1, 1, 1, alpha); // Set alpha to 50%

        for (int y = endY; y >= startY; y--) {
            for (int x = endX; x >= startX; x--) {
                if (x < 0 || y < 0 || x >= MAP_WIDTH || y >= MAP_HEIGHT) {
                    continue;
                }

                Tile tile = getTile(x, y);

                if (tile == null) {
                    continue;
                }

                if (inMergedCoords(x, y)) {
                    continue;
                }

                String terrainTextureId = tile.getTerrain().getTextureId();
                String baseTextureId = tile.getBaseTerrainId();
                if (!terrainTextureId.equals(previousTextureId)) {
                    if (terrainTextureId.startsWith("S")) {

                        terrainTexture = terrainAtlas.getTexture(terrainTextureId, checkSurroundingBaseTerrain(x, y));
                    } else {
                        terrainTexture = terrainAtlas.getTexture(terrainTextureId);

                    }
                    previousTextureId = terrainTextureId;
                    counterTexture++;
                }

                if (!baseTextureId.equals(previousBaseTextureId)) {
                    baseTexture = terrainAtlas.getTexture(baseTextureId);
                    previousBaseTextureId = baseTextureId;
                    counterBaseTexture++;
                }

//                if (mergeTiles && x % 2 == 0 && y % 2 == 0 && checkMergeable(x, y)) {
//                    // Render a 2x2 merged tile
//                    mergedCoords.add(new int[]{x+1, y});
//                    mergedCoords.add(new int[]{x, y+1});
//                    mergedCoords.add(new int[]{x+1, y+1});
//
//                    Gdx.app.log("Map", "merge tiles");
//                    if (terrainTextureId.equals("P")) {
//                        drawCallCounter ++;
//                        batch.draw(desert, x * TILE_SIZE + offsetX, y * TILE_SIZE + offsetY, TILE_SIZE * 2, TILE_SIZE * 2);
//
//                    }
//
//
//
//                    continue;
//                }

                float drawX = x * TILE_SIZE + offsetX;
                float drawY = y * TILE_SIZE + offsetY;

                if (!terrainTextureId.startsWith("S")) {
                    drawCallCounter++;
                    batch.draw(baseTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                } // if the terrain doesn't begin with S then we draw a base texture

                drawCallCounter++;
                if(terrainTextureId.equals("S")){
                    shaderManager.useShader("sea");
                    batch.setShader(shaderManager.getCurrentShader());

                    shaderManager.setUniformf("u_time", time);
                    batch.draw(terrainTexture, drawX, drawY, TILE_SIZE, terrainTexture.getRegionHeight() * (TILE_SIZE / 16f));
                    batch.setShader(null);
                }else{
                    batch.draw(terrainTexture, drawX, drawY, TILE_SIZE, terrainTexture.getRegionHeight() * (TILE_SIZE / 16f));
                }
            }
        }

       // batch.setColor(1, 1, 1, 1); // Reset color back to full opacity

    }

    public boolean inMergedCoords(int x, int y) {
        for (int[] mergedCoord : mergedCoords ) {
            if (mergedCoord[0] == x && mergedCoord[1] == y) {
                return true;
            }
        }
        return false;
    }


    public Boolean isMergeable() {
        return mergeTiles;
    }

    public int getDrawCallCounter() {
        return drawCallCounter;
    }

    public int tTC(){
        return counterTexture;
    }

    public int bTC(){
        return counterBaseTexture;
    }

    private String checkSurroundingBaseTerrain(int x, int y) {
        if (checkBounds(x + 1, y) && checkBounds(x - 1, y) && checkBounds(x, y - 1) && checkBounds(x, y + 1)) {
            Tile currentTile = getTile(x, y); // Access current tile directly
            String currentBaseType = currentTile.getTerrainBaseType();

            // Get surrounding terrain base types
            Tile leftTile = getTile(x - 1, y);
            Tile rightTile = getTile(x + 1, y);
            Tile topTile = getTile(x, y - 1);
            Tile bottomTile = getTile(x, y + 1);


            String leftBase = leftTile.getTerrainBaseType();
            String rightBase = rightTile.getTerrainBaseType();
            String topBase = topTile.getTerrainBaseType();
            String bottomBase = bottomTile.getTerrainBaseType();

            if(!leftBase.equalsIgnoreCase(currentBaseType) && leftBase.equals(rightBase)){
                Gdx.app.log("Map", "!leftBase.equalsIgnoreCase(currentBaseType) && leftBase.equals(rightBase) New Base :" + leftBase);

                return leftBase;
            }

            else if(!topBase.equalsIgnoreCase(currentBaseType) && topBase.equals(bottomBase)){
                Gdx.app.log("Map", "!topBase.equalsIgnoreCase(currentBaseType) && topBase.equals(bottomBase) New Base :" + topBase);

                return topBase;
            }

            else if(!leftBase.equals(rightBase) && !rightBase.equals("S") && !leftBase.equals("S")){
                Gdx.app.log("Map", "!leftBase.equals(rightBase) New Base :" + leftBase);

                return leftBase+"/"+rightBase;
            }
            else if(!topBase.equals(bottomBase) && !topBase.equals("S") && !bottomBase.equals("S")){
                Gdx.app.log("Map", "!topBase.equals(bottomBase) New Base :" + topBase);

                return topBase +"/"+bottomBase;
            }


            else if (!leftBase.equalsIgnoreCase(currentBaseType) && rightTile.getTerrainId().contains("S") && !rightBase.equals("S") && !leftBase.equals("S")) {
                Gdx.app.log("Map", "!leftBase.equalsIgnoreCase(currentBaseType) && rightTile.getTerrainId().contains(\"S\") New Base :" + leftBase);
                return leftBase;
            } else if(!rightBase.equalsIgnoreCase(currentBaseType) && leftTile.getTerrainId().contains("S")&& !rightBase.equals("S") && !leftBase.equals("S")){
                Gdx.app.log("Map", "!rightBase.equalsIgnoreCase(currentBaseType) && leftTile.getTerrainId().contains(\"S\") New Base :" + rightBase);
                return rightBase;
            }else if(topBase.equalsIgnoreCase(currentBaseType) && bottomTile.getTerrainId().contains("S")&& !topBase.equals("S") && !bottomBase.equals("S")){
                Gdx.app.log("Map", "topBase.equalsIgnoreCase(currentBaseType) && bottomTile.getTerrainId().contains(\"S\") New Base :" + topBase);
                return topBase;
            }else if(bottomBase.equalsIgnoreCase(currentBaseType) && topTile.getTerrainId().contains("S")&& !topBase.equals("S") && !bottomBase.equals("S")){
                Gdx.app.log("Map", "bottomBase.equalsIgnoreCase(currentBaseType) && topTile.getTerrainId().contains(\"S\") New Base :" + bottomBase);
                return bottomBase;
            }


        }
        return "P";
    }

    private boolean checkMergeable(int x, int y) {
        if (x + 1 >= MAP_WIDTH || y + 1 >= MAP_HEIGHT) return false;

        Tile left = getTile(x, y);
        Tile right = getTile(x + 1, y);
        Tile below = getTile(x, y + 1);
        Tile belowRight = getTile(x + 1, y + 1);

        if (left == null || right == null || below == null || belowRight == null) {
            return false;
        }

        String terrainId = left.getTerrain().getTextureId();
        return terrainId.equals(right.getTerrain().getTextureId()) &&
            terrainId.equals(below.getTerrain().getTextureId()) &&
            terrainId.equals(belowRight.getTerrain().getTextureId());
    }


    public void render3DMap(int TILE_SIZE, Batch batch, int offsetX, int offsetY, int startX, int startY, int endX, int endY) {

        TextureRegion baseTexture = null;
        TextureRegion terrainTexture = null;
        String previousTextureId = null;
        String previousBaseTextureId = null;
        int counterTexture = 0;
        int counterBaseTexture = 0;


        for (int y = endY; y >= startY; y--) {
            for (int x = endX; x >= startX; x--) {
                if (x < 0 || y < 0 || x >= MAP_WIDTH || y >= MAP_HEIGHT) {
                    continue; // Skip if out of bounds
                }

                Tile tile = getTile(x, y);
                if (tile == null) {
                    continue; // Skip if tile is null
                }

                String terrainTextureId = tile.getTerrain().getTextureId();
                String baseTextureId = tile.getBaseTerrainId();
                if (!terrainTextureId.equals(previousTextureId)) {
                    if (terrainTextureId.startsWith("S")) {
                        terrainTexture = terrainAtlas.getTexture(terrainTextureId, tile.getBaseTerrainId());
                    } else {
                        terrainTexture = terrainAtlas.getTexture(terrainTextureId);

                    }
                    previousTextureId = terrainTextureId;
                    counterTexture++;
                }

                if (!baseTextureId.equals(previousBaseTextureId)) {
                    baseTexture = terrainAtlas.getTexture(baseTextureId);
                    previousBaseTextureId = baseTextureId;
                    counterBaseTexture++;
                }

                float drawX = x * TILE_SIZE + offsetX;
                float drawY = y * TILE_SIZE + offsetY;

                if (!terrainTextureId.startsWith("S")) {
                    batch.draw(baseTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                }

                if (terrainTextureId.startsWith("F")) {
                    batch.draw(terrainTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                } else if (terrainTextureId.equals("SS") && baseTextureId.equals("D")) {
                    batch.draw(terrainTexture, drawX, drawY, TILE_SIZE, terrainTexture.getRegionHeight() * (TILE_SIZE / 16f));
                } else if (terrainTextureId.startsWith("S")) {
//                        Texture cT = checkSurroundingBaseTerrain(tile, x, y);
                    batch.draw(terrainTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                } else if (terrainTextureId.startsWith("M")) {
                    batch.draw(terrainTexture, drawX, drawY, TILE_SIZE, terrainTexture.getRegionHeight() * (TILE_SIZE / 16f));
                }


            }
        }
        Gdx.app.log("Map", "Times Texture Changed :" + counterTexture);
        Gdx.app.log("Map", "Times Base Texture Changed :" + counterBaseTexture);
        batch.setColor(1, 1, 1, 1); // Reset color back to full opacity

    }

//



        // Debug functions
//    public void printMapBaseTerrain(){
//        for (int y = 0; y < MAP_HEIGHT; y++) {
//            StringBuilder row = new StringBuilder();
//            for (int x = 0; x < MAP_WIDTH; x++) {
//                Tile tile = getTile(x, y);
//                if (tile != null && tile.getTerrain() != null) {
//                    row.append(tile.getTerrainBaseType()).append(" ");
//                } else {
//                    row.append("?? "); // Unknown terrain
//                }
//            }
//            System.out.println(row.toString().trim()); // Print each row
//        }
//        System.out.println("------------------------------------");
//
//    }

//    public void printMapToTerminal() {
//        for (int y = 0; y < MAP_HEIGHT; y++) {
//            StringBuilder row = new StringBuilder();
//            for (int x = 0; x < MAP_WIDTH; x++) {
//                Tile tile = getTile(x, y);
//                if (tile != null && tile.getTerrain() != null) {
//                    row.append(tile.getTerrain().getTextureId()).append(" ");
//                } else {
//                    row.append("?? "); // Unknown terrain
//                }
//            }
//            System.out.println(row.toString().trim()); // Print each row
//        }
//        System.out.println("------------------------------------");
//
//    }

        public void dispose() {
            if (terrainAtlas != null) {
                terrainAtlas.dispose();
                Gdx.app.log("AtlasManager", "TextureAtlas disposed.");
            }
        }

    }

