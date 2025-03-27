package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapMaker implements Screen {

    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 20;
    private final int MAP_HEIGHT = 20;

    private final float EARTHQUAKE_INTERVAL = 2.0f; // Time between earthquakes in seconds
    private final float SHAKE_DURATION = 0.5f; // How long the shake lasts in seconds
    private final float SHAKE_INTENSITY = 10f; // Max intensity of shake (pixels)

    private Texture damageIcon = new Texture("damageIcon.png");
    private Texture fillIcon = new Texture("fillmapIcon.png");
    private Texture damageRadius = new Texture("damageRadius.png");
    private final int TILE_PICKER_HEIGHT = 64; // Height of the tile picker
    private Texture fire = new Texture("fire.png");

    private boolean isPlacing = true;
    private  Map map;

    private Texture cursor;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private String selectedTile = "P";  // Default selected tile
    private boolean switchRender = false;

    private float earthquakeTimer = 0.0f; // Timer for earthquake events
    private float shakeTimer = 0.0f; // Timer for shake duration
    private float shakeOffsetX = 0f; // X offset for shake
    private float shakeOffsetY = 0f; // Y offset for shake


    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        camera.update();

        cursor = new Texture("cursor.png");

        this.map = new Map(MAP_WIDTH, MAP_HEIGHT);

//        ImGuiHandler.initImGui();

        // generating random map
        map.generateMap();
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);


        drawToolbar();
        camera.position.set(Gdx.graphics.getWidth() / 2 + shakeOffsetX, Gdx.graphics.getHeight() / 2 + shakeOffsetY, 0);
        camera.update();
        batch.begin();



        earthquakeTimer += delta;
        shakeTimer -= delta;
        if (shakeTimer > 0) {
            Random rand = new Random();
            shakeOffsetX = (rand.nextFloat() * 2 - 1) * SHAKE_INTENSITY; // Random X offset
            shakeOffsetY = (rand.nextFloat() * 2 - 1) * SHAKE_INTENSITY; // Random Y offset
        } else {
            // Reset shake offsets after the shaking duration ends
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }

        // Trigger earthquake event periodically
//
        if (earthquakeTimer >= EARTHQUAKE_INTERVAL) {
            triggerForestFire();
            //triggerEarthquake();
            earthquakeTimer = 0.0f; // Reset the timer
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            switchRender = !switchRender;
        }


        if (!switchRender) {
            // First, render all tiles except mountain tiles
            // First, render all tiles except the mountains
            map.renderMap(TILE_SIZE, batch, fire);
        }
//        else {
//
//            float horizontalOffset = 10 * TILE_SIZE;  // 10 tiles to the right
//
//            for (int y = 0; y < MAP_HEIGHT; y++) {
//                for (int x = 0; x < MAP_WIDTH; x++) {
//                    Texture tex = getTileTexture(map[y][x]);
//
//                    // Apply fake 3D tilt to X and Y position of each tile
//                    float drawX = (x - y) * TILE_SIZE + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2 + horizontalOffset;
//                    ;
//                    float drawY = (x + y) * TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
//                    // float drawX = (x - y) * TILE_SIZE + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2 + 32*5;
////                    float drawY = (x + y) * TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2 + 32*5;
//                    // Render non-mountain tiles first
//                    if (!map[y][x].equals("M")) {
//                        if (map[y][x].equals("F")) {
//                            batch.draw(getTileTexture("P"), drawX, drawY, TILE_SIZE, TILE_SIZE);
//                        }
//                        batch.draw(tex, drawX, drawY, TILE_SIZE, TILE_SIZE);
//                    }
//                }
//            }
//
//            // Then, render the mountain tiles starting from bottom-right to top-left with the fake 3D tilt
//            for (int y = MAP_HEIGHT - 1; y >= 0; y--) { // Start from the bottom row
//                for (int x = MAP_WIDTH - 1; x >= 0; x--) { // Start from the rightmost column
//                    if (map[y][x].equals("M")) {
//                        Texture tex = getTileTexture(map[y][x]);
//
//                        // Apply fake 3D tilt to X and Y position of each mountain tile
//                        float drawX = (x - y) * TILE_SIZE + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2 + horizontalOffset;
//                        ;
//                        float drawY = (x + y) * TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
//                        batch.draw(getTileTexture("P"), drawX, drawY, TILE_SIZE, TILE_SIZE);
//                        batch.draw(tex, drawX, drawY, TILE_SIZE, TILE_SIZE);
//                    }
//                }
//            }
//
//        }

        batch.end();

        drawTilePicker();

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int gridX = (mouseX - (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2) / TILE_SIZE;
        int gridY = (mouseY - (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2) / TILE_SIZE;

        if (gridX >= 0 && gridX < MAP_WIDTH && gridY >= 0 && gridY < MAP_HEIGHT) {

            // draw cursor
            batch.begin();
            batch.draw(cursor, (gridX * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2,
                (gridY * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2);
            batch.end();

            if(isPlacing){

                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    if(selectedTile.equals("S")){
                        smartPlaceSea(gridX, gridY);
                    }else{
                        map.getTile(gridX, gridY).updateTerrain(TerrainManager.getInstance().getTerrain(selectedTile));

                    }

//                else if  (selectedTile.equals("RH")) {
//                    smartPlaceRoad(gridX, gridY);
//                }

                    //updateBoard();
                }
            }else{
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    applyDamageInRadius(mouseX, mouseY);
                }
                drawDamageRadius(mouseX, mouseY);

            }

        }


        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            // Check if the damage icon was clicked
            if (mouseX >= 0 && mouseX <= 64) {
                if (mouseY >= Gdx.graphics.getHeight() - 128 && mouseY <= Gdx.graphics.getHeight() - 64) {
                    Gdx.app.log("MapMaker", "Damage Mode");
                    isPlacing = !isPlacing;
                }
            }

            // Check if the fill icon was clicked
            if (mouseX >= 0 && mouseX <= 64) {
                if (mouseY >= Gdx.graphics.getHeight() - 192 && mouseY <= Gdx.graphics.getHeight() - 128) {
                    Gdx.app.log("MapMaker", "Fill Mode");
                    fillBoard();
                }
            }
        }


//        ImGuiHandler.startImGui();
//        ImGuiHandler.renderUI();
//        ImGuiHandler.endImGui();

    }


    private void smartPlaceSea(int x, int y) {
        int count = 0;

        // Define the directional labels and corresponding (dx, dy) offsets
        int[][] directions = {
            {-1, 1}, // TL: Top-left
            {0, 1},  // T: Top
            {1, 1},  // TR: Top-right
            {-1, 0}, // L: Left
            {1, 0},  // R: Right
            {-1, -1}, // BL: Bottom-left
            {0, -1}, // B: Bottom
            {1, -1}  // BR: Bottom-right
        };

        boolean allPlains = true; // Flag to track if all surrounding tiles are plain

        boolean seaLeft = false;  // Flag to track if there is a sea tile to the left
        boolean seaRight = false; // Flag to track if there is a sea tile to the right
        boolean seaDown = false;  // Flag for sea tile down
        boolean seaUp = false;    // Flag for sea tile up

        boolean extendSeaLeft = false;
        boolean extendSeaRight = false;
        boolean extendSeaUp = false;
        boolean extendSeaDown = false;

        // Loop through each direction
        for (int i = 0; i < directions.length; i++) {
            Gdx.app.log("SmartPlacement", "Checking direction: dx = " + directions[i][0] + ", dy = " + directions[i][1]);

            int dx = directions[i][0];
            int dy = directions[i][1];
            int checkX = x + dx;
            int checkY = y + dy;

            // Skip out-of-bound tiles
            if (!map.checkBounds(checkX, checkY)) { continue; }

            String terrainId = map.getTile(checkX, checkY).getTerrainId();

            // Check left and right directions for specific terrain condition
            if (dy == 0 && (dx == -1 || dx == 1)) {
                if (!terrainId.contains("S")) {
                    count++;
                } else {
                    allPlains = false;
                }
            }

            // Check for sea tiles to the left and right
            if (dy == 0) {
                if (dx == -1 && terrainId.contains("SS")) {
                    Gdx.app.log("SmartPlacement", "Found sea left");
                    seaLeft = true;
                } else if (dx == 1 && terrainId.contains("SS")) {
                    Gdx.app.log("SmartPlacement", "Found sea right");
                    seaRight = true;
                }

                if (dx == 1 && terrainId.contains("SSCL")) {
                    extendSeaLeft = true;
                } else if (dx == -1 && terrainId.contains("SSCR")) {
                    extendSeaRight = true;
                }
            }

            // Check for sea tiles up and down
            if (dx == 0) {
                if (dy == -1 && terrainId.contains("SS")) {
                    seaUp = true;
                } else if (dy == 1 && terrainId.contains("SS")) {
                    seaDown = true;
                }

                if (dy == -1 && terrainId.contains("SSCB")) {
                    extendSeaUp = true;
                } else if (dy == 1 && terrainId.contains("SSCT")) {
                    extendSeaDown = true;
                }
            }
        }

        // If all surrounding tiles are plain, set the current tile to "SS"
        if (allPlains) {
            if (!map.checkBounds(x, y + 1)) {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCB"));
            } else if (!map.checkBounds(x, y - 1)) {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCT"));
            } else if (!map.checkBounds(x + 1, y)) {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCL"));
            } else if (!map.checkBounds(x - 1, y)) {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCR"));
            } else {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SS"));
            }
        }

        // Check for sea tiles to the left or right and update map
        if (seaLeft && x > 0) {
            if (!map.checkBounds(x - 2, y)) {
                Gdx.app.log("SmartPlacement", "Updating sea left edge");
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCR"));
                map.getTile(x - 1, y).updateTerrain(TerrainManager.getInstance().getTerrain("SHS"));
            } else {
                Gdx.app.log("SmartPlacement", "Updating sea left");
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCR"));
                map.getTile(x - 1, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCL"));
            }
        } else if (seaRight && x < MAP_WIDTH - 1) {
            if (!map.checkBounds(x + 2, y)) {
                Gdx.app.log("SmartPlacement", "Updating sea right edge");
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCL"));
                map.getTile(x + 1, y).updateTerrain(TerrainManager.getInstance().getTerrain("SHS"));
            } else {
                Gdx.app.log("SmartPlacement", "Updating sea right");
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCL"));
                map.getTile(x + 1, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCR"));
            }
        }
        // Extend the sea in the left or right directions if needed
        if (extendSeaLeft && x > 0) {
            Gdx.app.log("SmartPlacement", "Extending sea left");
            map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCL"));
            map.getTile(x + 1, y).updateTerrain(TerrainManager.getInstance().getTerrain("SHS"));
        }

        if (extendSeaRight && x < MAP_WIDTH - 1) {
            Gdx.app.log("SmartPlacement", "Extending sea right");
            map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SSCR"));
            map.getTile(x - 1, y).updateTerrain(TerrainManager.getInstance().getTerrain("SHS"));
        }

    }

    private void triggerForestFire() {
        Random rand = new Random();

        List<int[]> storeData = new ArrayList<>(); // Store fire spread locations


        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (map.getTile(x, y).getTerrain().getId().startsWith("F")&& map.getTile(x, y).hasFire()) {

                        map.getTile(x, y).updateTileHealth(0.5f);

                        int[][] directions = {
                            {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // Up, Down, Left, Right
                            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Diagonals
                        };

                        for (int i = 0; i < directions.length; i++) {
                            int dx = directions[i][0];
                            int dy = directions[i][1];

                            int newX = x + dx;
                            int newY = y + dy;

                            if (map.checkBounds(newX, newY) && map.getTile(newX, newY).getTerrain().getId().startsWith("F")) {
                                if (rand.nextDouble() < 0.4) {
                                    if(!map.getTile(newX, newY).isDestroyed()){storeData.add(new int[]{newX, newY});}
                                }
                            }
                        }
                    }
                }

        }

        for (int[] coord : storeData) {
            map.getTile(coord[0], coord[1]).attachFire();
        }
    }

    private void drawDamageRadius(int mouseX, int mouseY) {
        // Radius of damage effect in tiles
        int radius = TILE_SIZE;  // Radius in tiles (adjustable)

        // Convert mouse coordinates to map coordinates
        int gridX = (mouseX - (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2) / TILE_SIZE;
        int gridY = (mouseY - (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2) / TILE_SIZE;

        // Calculate the center of the damage radius in pixel coordinates
        float centerX = gridX * TILE_SIZE + TILE_SIZE / 2 + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
        float centerY = gridY * TILE_SIZE + TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;

        // Adjust the damage radius texture size if needed
        float scale = (float) radius / damageRadius.getWidth();

        // Set the transparency for the damage radius (alpha can range from 0.0f to 1.0f)
        float alpha = 0.5f; // Adjust this value to change transparency (0.0f = fully transparent, 1.0f = fully opaque)
        batch.setColor(1.0f, 1.0f, 1.0f, alpha);  // Set color with adjusted alpha

        // Draw the damage radius texture behind the cursor
        batch.begin();
        batch.draw(damageRadius, centerX - radius / 2, centerY - radius / 2, radius, radius);  // Adjust texture position and scale
        batch.end();

        // Reset color to default (no alpha change for other drawings)
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);  // Reset to default (fully opaque)
    }

    private void applyDamageInRadius(int mouseX, int mouseY) {
        int radius = TILE_SIZE;

        int gridX = (mouseX - (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2) / TILE_SIZE;
        int gridY = (mouseY - (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2) / TILE_SIZE;

        for (int y = gridY - radius / TILE_SIZE; y <= gridY + radius / TILE_SIZE; y++) {
            for (int x = gridX - radius / TILE_SIZE; x <= gridX + radius / TILE_SIZE; x++) {
                if (map.checkBounds(x,y)) {
                    float distance = (float) Math.sqrt(Math.pow(x - gridX, 2) + Math.pow(y - gridY, 2));

                    if (distance <= radius / TILE_SIZE) {
                        if(map.getTile(x,y).getTerrain().getId().startsWith("F") && !map.getTile(x, y).isDestroyed()) {
                            map.getTile(x,y).attachFire();

                        }
                        //map.getTile(x, y).updateTileHealth(0.5f);
                    }
                }
            }
        }
    }

    private void drawToolbar() {
        int toolbarWidth = 64;
        int toolbarHeight = Gdx.graphics.getHeight();
        int iconSize = 64;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1f));  // Dark background
        shapeRenderer.rect(0, 0, toolbarWidth, toolbarHeight);
        shapeRenderer.end();

        // Get mouse position
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();  // Invert Y coordinate to match screen space


        // Highlight damage icon if mouse is over it
        if (mouseX >= 0 && mouseX <= iconSize && mouseY >= Gdx.graphics.getHeight() - 128 && mouseY <= Gdx.graphics.getHeight() - 64) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);  // Highlight color (yellow border)
            shapeRenderer.rect(0, Gdx.graphics.getHeight() - 128, iconSize, iconSize);  // Draw border
            shapeRenderer.end();
        }
        batch.begin();
        batch.draw(damageIcon, 0, Gdx.graphics.getHeight() - (iconSize * 2), iconSize, iconSize);
        batch.end();

        // Highlight fill icon if mouse is over it
        if (mouseX >= 0 && mouseX <= iconSize && mouseY >= Gdx.graphics.getHeight() - 192 && mouseY <= Gdx.graphics.getHeight() - 128) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);  // Highlight color (yellow border)
            shapeRenderer.rect(0, Gdx.graphics.getHeight() - 192, iconSize, iconSize);  // Draw border
            shapeRenderer.end();
        }
        batch.begin();
        batch.draw(fillIcon, 0, Gdx.graphics.getHeight() - (iconSize * 3), iconSize, iconSize);
        batch.end();
    }




    private void fillBoard() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain(selectedTile));
              // map[y][x] = selectedTile;
            }
        }
    }

    private void updateBoard() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {

                if(map.getTile(x,y).getTerrainId().contains("S")) {
                    smartPlaceSea(x, y);

                }
            }
        }
    }


    private void triggerEarthquake() {
        Random rand = new Random();
        int numRuptures = 5 + rand.nextInt(10); // Number of ruptures per earthquake (between 5 and 15)

        for (int i = 0; i < numRuptures; i++) {
            int x = rand.nextInt(MAP_WIDTH);
            int y = rand.nextInt(MAP_HEIGHT);

            // If the tile isn't already ruptured, rupture it
            if (!map.getTile(x,y).getTerrain().getId().equals("R")) {
                map.getTile(x,y).updateTerrain(TerrainManager.getInstance().getTerrain("R"));

                // get the adjacent tiles
                if(map.checkBounds(x+1,y)){  map.getTile(x+1, y).updateTileHealth(1f);}
                if(map.checkBounds(x-1,y)){  map.getTile(x-1, y).updateTileHealth(1f);}
                if(map.checkBounds(x,y+1)){  map.getTile(x, y+1).updateTileHealth(1f);}
                if(map.checkBounds(x,y-1)){  map.getTile(x, y-1).updateTileHealth(1f);}

                // further away tiles damage by .5
                if(map.checkBounds(x+2,y)){  map.getTile(x+2, y).updateTileHealth(.5f);}
                if(map.checkBounds(x-2,y)){  map.getTile(x-2, y).updateTileHealth(.5f);}
                if(map.checkBounds(x,y+2)){  map.getTile(x, y+2).updateTileHealth(.5f);}
                if(map.checkBounds(x,y-2)){  map.getTile(x, y-2).updateTileHealth(.5f);}

            }
        }

        shakeTimer = SHAKE_DURATION;

    }

    private void drawTilePicker() {
        int pickerWidth = 4 * TILE_SIZE;
        int startX = (Gdx.graphics.getWidth() - pickerWidth) / 2;
        int startY = Gdx.graphics.getHeight() - TILE_PICKER_HEIGHT;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1f));
        shapeRenderer.rect(startX - 2, startY - 2, pickerWidth + 4, TILE_PICKER_HEIGHT + 4);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        shapeRenderer.rect(startX, startY, pickerWidth, TILE_PICKER_HEIGHT);
        shapeRenderer.end();

        TerrainManager terrainManager = TerrainManager.getInstance();
        batch.begin();

        int terrainIndex = 0;
        for (Terrain terrain : terrainManager.getTerrains()) {
            if (!terrain.isExcludeTilePicker()) {
                batch.draw(terrain.getTexture().get(0), startX + terrainIndex * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT);
            }
            terrainIndex++;

        }

        batch.end();

        int selectedTileX = getSelectedTileX(selectedTile);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(startX + selectedTileX * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT);
        shapeRenderer.end();

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && mouseY >= startY && mouseY <= startY + TILE_PICKER_HEIGHT) {

            int clickedTerrainIndex = (mouseX - startX) / TILE_SIZE;
            int currentIndex = 0;

            for (Terrain terrain : terrainManager.getTerrains()) {
                if (!terrain.isExcludeTilePicker() && clickedTerrainIndex == currentIndex) {
                    isPlacing = true;
                    selectedTile = terrain.getId();
                    break;
                }
                currentIndex++;
            }
        }
    }

    private int getSelectedTileX(String selectedTile) {
        TerrainManager terrainManager = TerrainManager.getInstance();
        int index = 0;
        for (Terrain terrain : terrainManager.getTerrains()) {
            if (terrain.getId().equals(selectedTile)) {
                return index;
            }
            index++;
        }
        return 0;
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();

        cursor.dispose();

        //   ImGuiHandler.disposeImGui();

    }
}
