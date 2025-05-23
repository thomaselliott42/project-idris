package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.*;

public class Map {

    private final int MAP_WIDTH;
    private final int MAP_HEIGHT;
    private Tile[][] map;
    private AtlasManager terrainAtlas;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private ShaderManager shaderManager;
    List<int[]> mergedCoords = new ArrayList<>();

    BitmapFont font = new BitmapFont(); // You can load a custom font too
    SpriteBatch batch = new SpriteBatch(); // Ideally, reuse this outside the method

    // shaders
    private float time = 0f;



    // testing
    private float alpha = 1.0f;
    private Texture plainForest = new Texture(Gdx.files.internal("plainForest4x4.png"));
    private Texture plain = new Texture(Gdx.files.internal("mergeTest.png"));
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

    public void generateMapRandom() {
        String[] tiles = {"P", "FD","FP", "MP","MD", "D","S"};
        Random rand = new Random();

        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                String tile = tiles[rand.nextInt(tiles.length)];
                map[y][x] = new Tile(TerrainManager.getInstance().getTerrain(tile), null, null);
            }
        }

        Gdx.app.log("Map", "Generated map");
    }

    public void generateMapSelected(String tile) {


        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                map[y][x] = new Tile(TerrainManager.getInstance().getTerrain(tile), null, null);
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
        time += Gdx.graphics.getDeltaTime(); // Keep increasing time
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
                    Gdx.app.log("Map", "Merged tile : " + x + "," + y + "," + tile);
                    continue;
                }

                String terrainTextureId = tile.getTerrain().getTextureId();
                String baseTextureId = tile.getBaseTerrainId();
                if (!terrainTextureId.equals(previousTextureId)) {
                    if (terrainTextureId.contains("S")) {
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
//                    mergedCoords.add(new int[]{x, y});
//                    mergedCoords.add(new int[]{x + 1, y});
//                    mergedCoords.add(new int[]{x, y + 1});
//                    mergedCoords.add(new int[]{x + 1, y + 1});
//
//
//                    if (terrainTextureId.equals("P")) {
//                        drawCallCounter++;
//                        batch.draw(plain, x * TILE_SIZE + offsetX, y * TILE_SIZE + offsetY, TILE_SIZE * 2, TILE_SIZE * 2);
//                    }
//
//                    continue;
//                }

                float drawX = x * TILE_SIZE + offsetX;
                float drawY = y * TILE_SIZE + offsetY;

                if (!terrainTextureId.startsWith("S")) {
                    drawCallCounter++;
                    batch.draw(baseTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                } else if (terrainTextureId.equals("S")) {
                    final TextureRegion region = terrainTexture;
                    // Sea shader draw
                    drawCallCounter++;
                    applyShader(batch, "sea", () -> {
                        shaderManager.setUniformf("u_time", time);
                        batch.draw(region, drawX, drawY, TILE_SIZE, region.getRegionHeight() * (TILE_SIZE / 16f));
                    });
                }

                if (terrainTexture != null && !terrainTextureId.equals("P") && !terrainTextureId.equals("D") && !terrainTextureId.equals("W") && !terrainTextureId.equals("S")) {
                    // Overlay draw, if needed
                    drawCallCounter++;
                    batch.draw(terrainTexture, drawX, drawY, TILE_SIZE, terrainTexture.getRegionHeight() * (TILE_SIZE / 16f));
                }


                if(tile.getBuilding() != null) {
                    drawCallCounter++;
                    final TextureRegion buildingTexture = AtlasManager.getInstance().getBuildingTextureRegion(tile.getBuilding().getTextureId());
                    applyShader(batch, "buildingColourChange", () -> {
                        if (tile.getBuilding().getFaction() != null && tile.getBuilding().getFaction().equals("nato"))
                        {
                            ShaderManager.getInstance().setUniformf("u_tintColor", 0.10588f, 0.51765f, 0.91765f, 1.0f); // Blue tint
                        } else if (tile.getFaction() == 2) {
                            ShaderManager.getInstance().setUniformf("u_tintColor", 1.0f, 0.0f, 0.0f, 1.0f); // Red tint
                        }
                        if(tile.getBuilding().getFaction()== null) {
                            shaderManager.setUniformi("u_ownership", 0);
                        }else {
                            shaderManager.setUniformi("u_ownership", 1);
                        }
                        batch.draw(buildingTexture,drawX, drawY, TILE_SIZE, buildingTexture.getRegionHeight() * (TILE_SIZE / 16f) );

                    });

                }
            }
        }

        // batch.setColor(1, 1, 1, 1); // Reset color back to full opacity

    }

    // debug
    private void printMergedCoords() {
        Gdx.app.log("MergedCoords", "Printing merged coordinates:");
        for (int[] coord : mergedCoords) {
            if (coord.length >= 2) {
                Gdx.app.log("MergedCoords", " - (" + coord[0] + ", " + coord[1] + ")");
            }
        }
    }

    public boolean inMergedCoords(int x, int y) {
        for (int[] mergedCoord : mergedCoords) {
            if (mergedCoord[0] == x && mergedCoord[1] == y) {
                return true;
            }
        }
        return false;
    }

    private void applyShader(Batch batch, String shaderId, Runnable drawCall) {
        shaderManager.useShader(shaderId);
        batch.setShader(shaderManager.getCurrentShader());
        drawCall.run();
        batch.setShader(null);
    }



    public Boolean isMergeable() {
        return mergeTiles;
    }

    public int getDrawCallCounter() {
        return drawCallCounter;
    }

    public int tTC() {
        return counterTexture;
    }

    public int bTC() {
        return counterBaseTexture;
    }

    public String checkSurroundingBaseTerrain(int x, int y) {
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
                //Gdx.app.log("Map", "!leftBase.equalsIgnoreCase(currentBaseType) && leftBase.equals(rightBase) New Base :" + leftBase);

                return leftBase;
            }

            else if(!topBase.equalsIgnoreCase(currentBaseType) && topBase.equals(bottomBase)){
                //Gdx.app.log("Map", "!topBase.equalsIgnoreCase(currentBaseType) && topBase.equals(bottomBase) New Base :" + topBase);

                return topBase;
            }

            else if(!leftBase.equals(rightBase) && !rightBase.equals("S") && !leftBase.equals("S")){
                //Gdx.app.log("Map", "!leftBase.equals(rightBase) New Base :" + leftBase);

                return leftBase+"/"+rightBase;
            }
            else if(!topBase.equals(bottomBase) && !topBase.equals("S") && !bottomBase.equals("S")){
                //Gdx.app.log("Map", "!topBase.equals(bottomBase) New Base :" + topBase);

                return topBase +"/"+bottomBase;
            }


            else if (!leftBase.equalsIgnoreCase(currentBaseType) && rightTile.getTerrainId().contains("S") && !rightBase.equals("S") && !leftBase.equals("S")) {
                //Gdx.app.log("Map", "!leftBase.equalsIgnoreCase(currentBaseType) && rightTile.getTerrainId().contains(\"S\") New Base :" + leftBase);
                return leftBase;
            } else if(!rightBase.equalsIgnoreCase(currentBaseType) && leftTile.getTerrainId().contains("S")&& !rightBase.equals("S") && !leftBase.equals("S")){
                //Gdx.app.log("Map", "!rightBase.equalsIgnoreCase(currentBaseType) && leftTile.getTerrainId().contains(\"S\") New Base :" + rightBase);
                return rightBase;
            }else if(topBase.equalsIgnoreCase(currentBaseType) && bottomTile.getTerrainId().contains("S")&& !topBase.equals("S") && !bottomBase.equals("S")){
                //Gdx.app.log("Map", "topBase.equalsIgnoreCase(currentBaseType) && bottomTile.getTerrainId().contains(\"S\") New Base :" + topBase);
                return topBase;
            }else if(bottomBase.equalsIgnoreCase(currentBaseType) && topTile.getTerrainId().contains("S")&& !topBase.equals("S") && !bottomBase.equals("S")){
                //Gdx.app.log("Map", "bottomBase.equalsIgnoreCase(currentBaseType) && topTile.getTerrainId().contains(\"S\") New Base :" + bottomBase);
                return bottomBase;
            }


        }

        boolean checkDesertHorizontalBaseTerrains = x < MAP_WIDTH - 1 && x > 0 && checkSeaTilesHorizontalRow(x, y, "D");
        boolean checkDesertVerticalBaseTerrains = y < MAP_HEIGHT - 1 && y > 0 && checkSeaTilesVerticalRow(x, y, "D");

        // check for plains
        boolean checkPlainsHorizontalBaseTerrains = x < MAP_WIDTH - 1 && x > 0 && checkSeaTilesHorizontalRow(x, y, "P");
        boolean checkPlainsVerticalBaseTerrains = y < MAP_HEIGHT - 1 && y > 0 && checkSeaTilesVerticalRow(x, y, "P");

        // check for winter (uncomment when winter is implemented)
        boolean checkWinterHorizontalBaseTerrains = x < MAP_WIDTH - 1 && x>0 && checkSeaTilesHorizontalRow(x, y, "W");
        boolean checkWinterVerticalBaseTerrains = y < MAP_HEIGHT - 1 && y>0 && checkSeaTilesVerticalRow(x, y, "W");


        if (checkDesertHorizontalBaseTerrains || checkDesertVerticalBaseTerrains) {return "D";}
        else if (checkPlainsHorizontalBaseTerrains || checkPlainsVerticalBaseTerrains) {return "P";}
        else if (checkWinterHorizontalBaseTerrains || checkWinterVerticalBaseTerrains) {return "W";}

        return "P";
    }


    private boolean checkSeaTilesHorizontalRow(int x, int y, String baseTerrain){
        for (int i = x; i > 0; i--) {
            if (!getTile(i, y).getTerrain().getTextureId().contains("S")) {

                for (int j=x; j < MAP_WIDTH; j++){
                    if (!getTile(j, y).getTerrain().getTextureId().contains("S")) {
                        if (getTile(i, y).getTerrainBaseType().equals(baseTerrain) && getTile(j, y).getTerrainBaseType().equals(baseTerrain)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // used to check what base terrain to set a sea tile : vertical check
    private boolean checkSeaTilesVerticalRow(int x, int y, String baseTerrain){
        for (int i = y; i > 0; i--) {
            if (!getTile(x, i).getTerrain().getTextureId().contains("S")) {
                for (int j=y; j < MAP_HEIGHT; j++){
                    if (!getTile(x, j).getTerrain().getTextureId().contains("S")) {
                        if (getTile(x, i).getTerrainBaseType().equals(baseTerrain) && getTile(x, j).getTerrainBaseType().equals(baseTerrain)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

//    public void printMiniMap(){
//        for (int y = map.length; y >= 0; y--) {
//            for (int x = 0; x >= map.length; x--) {
//                // font batch forest . dark green plain green . mountian . brown sea blue .
//                // top left corner not on the map using the ui camera and drawing rectangle lines aroudn where the camera currently is if zoomed in
//                // using int startX, int startY, int endX, int endY
//
//            }
//        }
//    }

    public void printMiniMap(int startX, int startY, int endX, int endY) {
        final int tileSize = 4; // Size of each minimap tile in pixels
        final int miniMapWidth = map[0].length * tileSize;
        final int miniMapHeight = map.length * tileSize;

        // Screen dimensions
        final int screenWidth = Gdx.graphics.getWidth();
        final int screenHeight = Gdx.graphics.getHeight();

        // Top-right corner positioning with padding
        final int offsetX = screenWidth - miniMapWidth - 10;
        final int offsetY = screenHeight - miniMapHeight - 10;

        batch.setProjectionMatrix(CameraManager.getInstance().getUiCamera().combined);
        batch.begin();

        // Draw minimap tiles
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                String terrain = map[y][x].getTerrainId();
                String symbol = ".";
                Color color = Color.LIGHT_GRAY;

                switch (terrain) {
                    default:
                        if (terrain.contains("P")) {
                            symbol = ".";
                            color = Color.GREEN;
                        }else if(terrain.contains("D")){
                            symbol = ".";
                            color = Color.YELLOW;
                        }else if(terrain.contains("S")){
                            symbol = ".";
                            color = Color.BLUE;
                        }else if(terrain.contains("W")){
                            symbol = ".";
                            color = Color.WHITE;
                        }
                        break;
                }

                //font.getData().setScale(4f); // Might need to tweak for exact fit
                font.setColor(color);
                float drawX = offsetX + x * tileSize;
                float drawY = offsetY + (y + 1) * tileSize;
                font.draw(batch, symbol, drawX, drawY);

            }
        }

        batch.end();

        // Draw camera view box on top
        shapeRenderer.setProjectionMatrix(CameraManager.getInstance().getUiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        float rectX = offsetX + startX * tileSize;
        float rectY = offsetY + startY * tileSize;
        float rectWidth = (endX - startX) * tileSize;
        float rectHeight = (endY - startY) * tileSize;

        shapeRenderer.rect(rectX, rectY, rectWidth, rectHeight);
        shapeRenderer.end();
    }

    public void printMiniMap() {
        final int tileSize = 4;
        final int miniMapWidth = MAP_WIDTH * tileSize;
        final int miniMapHeight = MAP_HEIGHT * tileSize;

        float startX = 10f; // Padding from the left edge
        float startY = (Gdx.graphics.getHeight() - miniMapHeight) / 2f; // Center vertically

        batch.setProjectionMatrix(CameraManager.getInstance().getUiCamera().combined);
        batch.begin();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                String terrain = map[y][x].getTerrainId();
                String symbol = ".";
                Color color = Color.LIGHT_GRAY;

                if (terrain.contains("P")) {
                    color = Color.GREEN;
                } else if (terrain.contains("D")) {
                    color = Color.YELLOW;
                } else if (terrain.contains("S")) {
                    color = Color.BLUE;
                } else if (terrain.contains("W")) {
                    color = Color.WHITE;
                }

                font.setColor(color);
                float drawX = startX + x * tileSize;
                float drawY = startY + (map.length - y - 1) * tileSize; // Flip Y so top of map is at top visually
                font.draw(batch, symbol, drawX, drawY);
            }
        }

        batch.end();
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

