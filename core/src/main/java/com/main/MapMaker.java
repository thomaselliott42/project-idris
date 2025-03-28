package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapMaker implements Screen {

    private final int TILE_SIZE = 32; // 32
    private final int MAP_WIDTH = 20; // 20
    private final int MAP_HEIGHT = 20; // 20

    private Texture cursor = new Texture("ui/cursor.png");
    private Texture fillIcon = new Texture("ui/fillmapIcon.png");
    private Texture reloadIcon = new Texture("ui/reloadIcon.png");
    private Texture saveIcon = new Texture("ui/saveIcon.png");
    private Texture inspectIcon = new Texture("ui/inspectIcon.png");

    private Texture damageIcon = new Texture("ui/damageIcon.png");
    private Texture damageRadius = new Texture("ui/damageRadius.png");

    private final int TILE_PICKER_HEIGHT = 64; // Height of the tile picker
    private boolean isPlacing = true;
    private  Map map;

    private SpriteBatch batch;
    private BitmapFont font;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private String selectedTile = "P";  // Default selected tile
    private boolean inspect = false;



    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        camera.update();

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
        camera.update();
        batch.begin();


        map.renderMap(TILE_SIZE, batch);

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
                        forceUpdateAllSeaTiles();
                    }

                    map.printMapToTerminal();

                }
            }else{
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    applyDamageInRadius(mouseX, mouseY);
                }
                drawDamageRadius(mouseX, mouseY);

            }

        }


        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mouseX >= 0 && mouseX <= 64) {
                if (mouseY >= Gdx.graphics.getHeight() - 128 && mouseY <= Gdx.graphics.getHeight() - 64) {
                    Gdx.app.log("MapMaker", "Inspect Mode");
                    inspect = !inspect;


                }
                else if (mouseY >= Gdx.graphics.getHeight() - 192 && mouseY <= Gdx.graphics.getHeight() - 128) {
                    Gdx.app.log("MapMaker", "Damage Mode");
                    isPlacing = !isPlacing;

                }
                else if (mouseY >= Gdx.graphics.getHeight() - 256 && mouseY <= Gdx.graphics.getHeight() - 192) {
                    Gdx.app.log("MapMaker", "Fill Mode");
                    fillBoard();

                }
                else if (mouseY >= Gdx.graphics.getHeight() - 320 && mouseY <= Gdx.graphics.getHeight() - 256) {
                    Gdx.app.log("MapMaker", "Reload Json");
                    reloadJson();

                }
                else if (mouseY >= Gdx.graphics.getHeight() - 384 && mouseY <= Gdx.graphics.getHeight() - 320) {
                    Gdx.app.log("MapMaker", "Save Map");
                    fillBoard();
                }
            }

        }


        if(inspect){
            if(map.checkBounds(gridX, gridY)){
                batch.begin();
                font.draw(batch, map.getTile(gridX,gridY).getTerrainId(), Gdx.graphics.getWidth() - 50, 40);
                batch.end();
            }
        }
        batch.begin();
        String coords = gridY +"," +gridX;
        font.draw(batch, coords, Gdx.graphics.getWidth() - 50, 20);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),Gdx.graphics.getWidth() - 150, 20 );
        batch.end();

//        ImGuiHandler.startImGui();
//        ImGuiHandler.renderUI();
//        ImGuiHandler.endImGui();

    }

    private void reloadJson() {
        TerrainLoader.loadTerrains();
        forceUpdateAllSeaTiles();
    }

    private void smartPlaceSea(int x, int y) {
        if (!map.checkBounds(x, y) || map.getTile(x, y).getTerrain().getId().contains("S")) {
            return;
        }

        // Place initial sea tile
        map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SS"));

        // Immediately update all sea tiles on the map
        forceUpdateAllSeaTiles();
    }

    private void forceUpdateAllSeaTiles() {
        // Create a copy of all sea tile positions
        List<int[]> seaTiles = new ArrayList<>();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map.getTile(x, y).getTerrain().getId().contains("S")) {
                    seaTiles.add(new int[]{x, y});
                }
            }
        }

        // Update each sea tile with guaranteed neighbor checks
        for (int[] pos : seaTiles) {
            updateSeaTileGuaranteed(pos[0], pos[1]);
        }
    }

    private void updateSeaTileGuaranteed(int x, int y) {
        // Safe neighbor checking with boundary verification

        boolean north = (y < MAP_HEIGHT - 1) && map.getTile(x, y + 1).getTerrain().getId().contains("S");
        boolean east = (x < MAP_WIDTH - 1) && map.getTile(x + 1, y).getTerrain().getId().contains("S");
        boolean south = (y > 0) && map.getTile(x, y - 1).getTerrain().getId().contains("S");
        boolean west = (x > 0) && map.getTile(x - 1, y).getTerrain().getId().contains("S");

        // Diagonal checks - MUST validate BOTH x and y bounds!
        boolean northeast = (x < MAP_WIDTH - 1) && (y < MAP_HEIGHT - 1) &&
            map.getTile(x + 1, y + 1).getTerrain().getId().contains("S");
        boolean northwest = (x > 0) && (y < MAP_HEIGHT - 1) &&
            map.getTile(x - 1, y + 1).getTerrain().getId().contains("S");
        boolean southeast = (x < MAP_WIDTH - 1) && (y > 0) &&
            map.getTile(x + 1, y - 1).getTerrain().getId().contains("S");
        boolean southwest = (x > 0) && (y > 0) &&
            map.getTile(x - 1, y - 1).getTerrain().getId().contains("S");


        // checking to see if the tile is at the edges of the terrain
        // north edge straight
        if ((y == MAP_HEIGHT-1) && (x < MAP_WIDTH-1) && (x > 0)){
            north = true;
            northeast = true;
            northwest = true;
        }
        // south edge straight
        if ((y == 0) && (x < MAP_WIDTH-1) && (x > 0)){
            south = true;
            southeast = true;
            southwest = true;
        }
        // right edge straight
        if ((y < MAP_HEIGHT-1) && (y>0) && (x == MAP_WIDTH-1)){
            northeast = true;
            east = true;
            southeast = true;
        }
        // left edge straight
        if ((y < MAP_HEIGHT-1) && (y>0) && (x == 0)){
            northwest = true;
            west = true;
            southwest = true;
        }
        // top right edge corner
        if ((y == MAP_HEIGHT-1) && (x == MAP_WIDTH-1)){
            north = true;
            east = true;
            southeast = true;
            northwest = true;
            northeast = true;
        }
        // top left edge corner
        if ((y == MAP_HEIGHT-1) && (x == 0)){
            north = true;
            west = true;
            southwest = true;
            northwest = true;
            northeast = true;
        }
        // bottom left edge corner
        if ((y == 0) && (x == 0)){
            south = true;
            west = true;
            southeast = true;
            southwest = true;
            northwest = true;
        }
        // bottom right edge corner
        if ((y == 0) && (x == MAP_WIDTH-1)){
            south = true;
            east = true;
            southeast = true;
            southwest = true;
            northeast = true;
        }

        String newTileId = determineTileType(north, east, south, west, northeast, northwest, southeast, southwest);
        Terrain newTerrain = TerrainManager.getInstance().getTerrain(newTileId);
        if (newTerrain != null) {
            map.getTile(x, y).updateTerrain(newTerrain);
        }
    }



    private String determineTileType(boolean north, boolean east, boolean south, boolean west,
                                     boolean northeast, boolean northwest, boolean southeast, boolean southwest) {
        int directConnections = (north ? 1 : 0) + (east ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0);
        int allConnections = (north ? 1 : 0) + (east ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0) + (northwest ? 1 : 0) + (southwest ? 1 : 0) + (northeast ? 1 : 0) + (southeast ? 1 : 0);

        String returnString = "SS";

        // Handle all direct connection cases
        switch (directConnections) {

            case 1: // semi-circular straights
                if (north) {
                    returnString = "SSCB";
                    break;
                }
                if (south){
                    returnString = "SSCT";
                    break;
                }
                if (east){
                    returnString = "SSCL";
                    break;
                }
                if (west){
                    returnString = "SSCR";
                    break;
                }
                break;

            case 2: // Corners or straight
                if (east && west){
                    returnString = "SHS";
                }
                if (north && south){
                    returnString = "SVS";
                }
                if (north && east) {
                    returnString = "SSCBL";
                }
                if (north && west) {
                    returnString = "SSCBR";
                }
                if (south && east) {
                    returnString = "SSCTL";
                }
                if (south && west) {
                    returnString = "SSCTR";
                }
                break;

            case 3: // T-junctions
                if (south && east && west) returnString = "STFT";
                if (north && east && west) returnString = "STFB";
                if (north && south && east) returnString = "STFL";
                if (north && south && west) returnString = "STFR";
                break;

            case 4:
                if (north && south && east && west) returnString = "STFFS"; // Cross
                break;
        }

        // handle all connections including diagonals
        switch (allConnections) {
            case 3:
                // lake corners
                if (north && northwest && west){
                    returnString = "SLCBR";
                    break;
                }
                if (north && northeast && east){
                    returnString = "SLCBL";
                    break;
                }
                if (south && southeast && east){
                    returnString = "SLCTL";
                    break;
                }
                if (south && southwest && west){
                    returnString = "SLCTR";
                    break;
                }
                break;
            case 4:

                // lake extending corners
                if (north && east && southeast && south){
                    returnString = "SLECTLV";
                    break;
                }
                if (west && east && southeast && south){
                    returnString = "SLECTLH";
                    break;
                }
                if (east && west && north && northwest){
                    returnString = "SLECBRH";
                    break;
                }
                if (south && west && north && northwest){
                    returnString = "SLECBRV";
                    break;
                }
                if (north && west && southwest && south){
                    returnString = "SLECTRV";
                    break;
                }
                if (!southeast && !north && !northwest && !northeast){
                    returnString = "SLECTRH";
                    break;
                }
                if (west && east && north && northeast){
                    returnString = "SLECBLH";
                    break;
                }
                if (south && east && north && northeast){
                    returnString = "SLECBLV";
                    break;
                }

                // lake corners of t functions
                if ((!south && !southeast && !east && !northeast) || (!south && !southeast && !east && !southwest)){
                    returnString = "SLCBR";
                    break;
                }
                if ((!south && !southwest && !west && !northwest) || (!south && !southwest && !west && !southeast)){
                    returnString = "SLCBL";
                    break;
                }
                if ((!north && !northeast && !east && !northwest) || (!north && !northeast && !east && !southeast)){
                    returnString = "SLCTR";
                    break;
                }
                if ((!north && !northwest && !west && !northeast) || (!north && !northwest && !west && !southwest)){
                    returnString = "SLCTL";
                    break;
                }


                break;


            case 5:

                // lake extending corners
                if (!northwest && !west && !southeast){
                    returnString = "SLECBLV";
                    break;
                }
                if (!northeast && !east && !southwest){
                    returnString = "SLECBRV";
                    break;
                }
                if (!south && !southeast && !northwest){
                    returnString = "SLECBLH";
                    break;
                }
                if (!south && !southwest && !northeast){
                    returnString = "SLECBRH";
                    break;
                }
                if (!west && !southwest && !northeast){
                    returnString = "SLECTLV";
                    break;
                }
                if (!east && !southeast && !northwest){
                    returnString = "SLECTRV";
                    break;
                }

                //lake corners of t functions
                if (!south && !southeast && !east){
                    returnString = "SLCBR";
                    break;
                }
                if (!south && !southwest && !west){
                    returnString = "SLCBL";
                    break;
                }
                if (!north && !northeast && !east){
                    returnString = "SLCTR";
                    break;
                }
                if (!north && !northwest && !west){
                    returnString = "SLCTL";
                    break;
                }

                // lake straights
                if ((northeast && north && northwest && east && west) || (southwest && east && south && north && northwest)
                    || (southwest && east && south && north && northeast)){
                    returnString = "SLHB";
                    break;
                }
                if ((southeast && south && southwest && east && west) || (northwest && west && east && south && southeast)
                    || (northwest && west && east && south && southwest)){
                    returnString = "SLHT";
                    break;
                }
                if (northeast && north && east && south && southeast){
                    returnString = "SLVL";
                    break;
                }
                if (northwest && north && west && south && southwest){
                    returnString = "SLVR";
                    break;
                }

                // lake middle corners
                if (!northwest && !southeast && !northeast){
                    returnString = "SLMCPCR";
                    break;
                }
                if (!northwest && !southeast && !southwest){
                    returnString = "SLMCPCL";
                    break;
                }
                if (!northeast && !southwest && !northwest){
                    returnString = "SLMCNCL";
                    break;
                }
                if (!northeast && !southwest && !southeast){
                    returnString = "SLMCNCR";
                    break;
                }
                

                break;
            case 6:
                // lake t-functions
                if (north && northeast && east && west && south && southeast) {
                    returnString = "SLTFR";
                    break;
                }
                if (north && northwest && east && west && south && southwest) {
                    returnString = "SLTFL";
                    break;
                }
                if (north && northeast && northwest && east && west && south) {
                    returnString = "SLTFT";
                    break;
                }
                if (south && southeast && southwest && east && west && north) {
                    returnString = "SLTFB";
                    break;
                }

                // tiles next to t-functions
                if ((north && northwest && west && southwest && south && southeast) || (northwest && north && northeast && west && southwest && south)){
                    returnString = "SLVR";
                    break;
                }
                if ((north && northeast && northwest && east && southeast && south) || (south && southwest && southeast && east && north && northeast)){
                    returnString = "SLVL";
                    break;
                }
                if ((southwest && east && west && northwest && north && northeast) || (southeast && east && west && northwest && north && northeast)){
                    returnString = "SLHB";
                    break;
                }
                if ((northeast && east && west && southwest && south && southeast) || (northwest && east && west && south && southeast &&southwest)){
                    returnString = "SLHT";
                    break;
                }

                // lake middle corners
                if (!northwest && !southeast){
                    returnString = "SLMCP";
                    break;
                }
                if (!northeast && !southwest){
                    returnString = "SLMCN";
                    break;
                }
                break;
            case 7:
                // lake inside corners
                if (!southeast){
                    returnString = "SLICTL";
                    break;
                }
                if (!southwest){
                    returnString = "SLICTR";
                    break;
                }
                if (!northeast){
                    returnString = "SLICBL";
                    break;
                }
                if (!northwest){
                    returnString = "SLICBR";
                    break;
                }

                // lake straights
                if (!south){
                    returnString = "SLHB";
                    break;
                }
                if (!north){
                    returnString = "SLHT";
                    break;
                }
                if (!east){
                    returnString = "SLVR";
                    break;
                }
                if (!west){
                    returnString = "SLVL";
                    break;
                }
            case 8:
                // sea
                if (north && northwest && northeast && east && west && south && southwest && southeast) {
                    returnString = "S";
                    break;
                }
                break;
        }

        return returnString;
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
                        map.getTile(x, y).updateTileHealth(0.5f);
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

        // Draw and highlight icons with a helper function
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 128, inspectIcon, iconSize); // Damage icon
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 192, damageIcon, iconSize); // Damage icon
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 256, fillIcon, iconSize);   // Fill icon
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 320, reloadIcon, iconSize); // Reload icon
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 384, saveIcon, iconSize);   // Save icon
    }

    private void drawAndHighlightIcon(int mouseX, int mouseY, int x, int y, Texture icon, int iconSize) {
        // Highlight icon if mouse is over it
        if (mouseX >= x && mouseX <= x + iconSize && mouseY >= y && mouseY <= y + iconSize) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);  // Highlight color (yellow border)
            shapeRenderer.rect(x, y, iconSize, iconSize);  // Draw border
            shapeRenderer.end();
        }

        batch.begin();
        batch.draw(icon, x, y, iconSize, iconSize);
        batch.end();
    }

    private void fillBoard() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain(selectedTile));
            }
        }
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
        List<Terrain> includedTerrains = new ArrayList<>();

        // Collect only the terrains that are not excluded from the tile picker
        for (Terrain terrain : terrainManager.getTerrains()) {
            if (!terrain.isExcludeTilePicker()) {
                includedTerrains.add(terrain);
            }
        }

        // Draw the terrains
        for (int i = 0; i < includedTerrains.size(); i++) {
            Terrain terrain = includedTerrains.get(i);
            batch.draw(terrain.getTexture().get(0), startX + i * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT);
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

            if (clickedTerrainIndex >= 0 && clickedTerrainIndex < includedTerrains.size()) {
                Terrain clickedTerrain = includedTerrains.get(clickedTerrainIndex);
                isPlacing = true;
                selectedTile = clickedTerrain.getId();
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
