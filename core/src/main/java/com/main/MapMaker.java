package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;


import java.util.ArrayList;
import java.util.List;

public class MapMaker implements Screen, InputProcessor {

    private final Main game;

    private final int TILE_SIZE = 32; // 32
    private final int MAP_WIDTH = 100; // 20
    private final int MAP_HEIGHT = 100; // 20

    private final TextureRegion cursor = AtlasManager.getInstance().getMapUItexture("cursor");;
    private final TextureRegion fillIcon = AtlasManager.getInstance().getMapUItexture("fillmapIcon");;
    private final TextureRegion reloadIcon = AtlasManager.getInstance().getMapUItexture("reloadIcon");;
    private final TextureRegion saveIcon = AtlasManager.getInstance().getMapUItexture("saveIcon");;
    private final TextureRegion inspectIcon = AtlasManager.getInstance().getMapUItexture("inspectIcon");;
    private final TextureRegion grabIcon = AtlasManager.getInstance().getMapUItexture("grabIcon");;
    private final TextureRegion damageIcon = AtlasManager.getInstance().getMapUItexture("damageIcon");
    private final TextureRegion damageRadius = AtlasManager.getInstance().getMapUItexture("damageRadius");;


    Cursor grabCursor;

    private boolean isGrabbing = false;

    private final int TILE_PICKER_HEIGHT = 64; // Height of the tile picker
    private boolean isPlacing = true;
    private boolean isDraggingToPlace = false;
    private  Map map;

    private SpriteBatch batch;
    private BitmapFont font;

    private ShapeRenderer shapeRenderer;
    private CameraManager cameraManager;

    private String selectedTile = "P";  // Default selected tile
    private boolean inspect = false;

    private final float ZOOM_2D_THRESHOLD = 0.5f; // Zoom level where we switch to 3D
    private boolean is3DView = false;
    private boolean mouseOverMap = false;

    // debug
    private long lastMemoryUpdateTime = 0;
    private long usedMemory = 0;
    private long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;

    public MapMaker(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        cameraManager = CameraManager.getInstance();

        // Cursor Sprite
       createGrabCursor();

        // Set this class as the input processor
        Gdx.input.setInputProcessor(this);



        // Center camera on map initially
        cameraManager.getMapCamera().position.set(
            (MAP_WIDTH * TILE_SIZE) / 2f,
            (MAP_HEIGHT * TILE_SIZE) / 2f,
            0
        );
        cameraManager.getMapCamera().update();


        this.map = new Map(MAP_WIDTH, MAP_HEIGHT);
        map.generateMap();
    }

    public void createGrabCursor() {
        // Get the texture and ensure its data is ready
        Texture texture = grabIcon.getTexture();

        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }

        // Create a Pixmap from the texture
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        grabCursor = Gdx.graphics.newCursor(pixmap, 0, 0);

        // Dispose of the Pixmap to prevent memory leaks
        pixmap.dispose();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // View switching and camera handling remains the same
        boolean shouldBe3D = cameraManager.getZoomLevel() < ZOOM_2D_THRESHOLD;
        if (shouldBe3D != is3DView) {
            is3DView = shouldBe3D;
            Gdx.app.log("MapMaker", "Switched to " + (is3DView ? "3D" : "2D") + " view");
        }

        // Updating camera
        cameraManager.handleCameraMovement();
        cameraManager.updateCameraPosition();
        cameraManager.updateCameraZoom(delta);

        // Map rendering
        renderMap();


        // UI rendering
        batch.setProjectionMatrix(cameraManager.getUiCamera().combined);
        shapeRenderer.setProjectionMatrix(cameraManager.getUiCamera().combined);
        drawToolbar();
        drawTilePicker();  // Now drawn with UI camera


        cameraManager.getUiCamera().update();

        // Initialize tile coordinates outside the if block
        int gridX = -1;
        int gridY = -1;
        boolean mouseOverMap = false;

        // Mouse position handling
        Vector3 mouseWorldPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cameraManager.getMapCamera().unproject(mouseWorldPos);

        float mapStartX = (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2f;
        float mapStartY = (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2f;
        float mapEndX = mapStartX + MAP_WIDTH * TILE_SIZE;
        float mapEndY = mapStartY + MAP_HEIGHT * TILE_SIZE;

        if (mouseWorldPos.x >= mapStartX && mouseWorldPos.x <= mapEndX &&
            mouseWorldPos.y >= mapStartY && mouseWorldPos.y <= mapEndY) {

            gridX = (int)((mouseWorldPos.x - mapStartX) / TILE_SIZE);
            gridY = (int)((mouseWorldPos.y - mapStartY) / TILE_SIZE);
            mouseOverMap = map.checkBounds(gridX, gridY);

            if (mouseOverMap) {
                if(!isGrabbing){
                    // Draw cursor
                    batch.setProjectionMatrix(cameraManager.getMapCamera().combined);
                    batch.begin();
                    float cursorSize = TILE_SIZE;
                    float cursorX = mapStartX + gridX * TILE_SIZE;
                    float cursorY = mapStartY + gridY * TILE_SIZE;
                    batch.draw(cursor,
                        cursorX - (cursorSize - TILE_SIZE)/2,
                        cursorY - (cursorSize - TILE_SIZE)/2,
                        cursorSize, cursorSize);
                    batch.end();
                }


                // Handle continuous placement while dragging
                if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) && isDraggingToPlace && isPlacing)) {
                    int mouseX = Gdx.input.getX();
                    int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

                    if (!isClickInTilePicker(mouseX, mouseY)) {
                        if (selectedTile.equals("S")) {
                            smartPlaceSea(gridX, gridY);
                        } else {
                            map.getTile(gridX, gridY).updateTerrain(TerrainManager.getInstance().getTerrain(selectedTile));
                            forceUpdateAllSeaTiles(gridX, gridY);
                        }
                        //map.printMapToTerminal();
                    }
                }else if(isGrabbing){


                } else if(!isPlacing){
                    int mouseX = Gdx.input.getX();
                    int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
                    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        applyDamageInRadius(mouseX, mouseY);
                    }
                    drawDamageRadius(mouseX, mouseY);
                }


            }
        }

        // Toolbar handling (using screen coordinates)
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Handle toolbar clicks (UI camera coordinates)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mouseX >= 0 && mouseX <= 64) {
                if (mouseY >= Gdx.graphics.getHeight() - 128 && mouseY <= Gdx.graphics.getHeight() - 64) {
                    isGrabbing = !isGrabbing;

                    if(isGrabbing){
                        Gdx.graphics.setCursor(grabCursor);

                        isPlacing = false;
                    }else{
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); // Revert to the default cursor

                        isPlacing = true;
                    }
                }
                else if (mouseY >= Gdx.graphics.getHeight() - 192 && mouseY <= Gdx.graphics.getHeight() - 128) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); // Revert to the default cursor
                    isGrabbing = false;
                    isPlacing = false;
                }
                else if (mouseY >= Gdx.graphics.getHeight() - 256 && mouseY <= Gdx.graphics.getHeight() - 192) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); // Revert to the default cursor

                    fillBoard();
                }
                else if (mouseY >= Gdx.graphics.getHeight() - 320 && mouseY <= Gdx.graphics.getHeight() - 256) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); // Revert to the default cursor

                    reloadJson();
                }
                else if (mouseY >= Gdx.graphics.getHeight() - 384 && mouseY <= Gdx.graphics.getHeight() - 320) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); // Revert to the default cursor

                    // Save Map
                    fillBoard();
                }
                else if (mouseY >= Gdx.graphics.getHeight() - 448 && mouseY <= Gdx.graphics.getHeight() - 384) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); // Revert to the default cursor

                    inspect = !inspect;
                }
            }
        }




        // Debug info
        renderDebugInfo(gridX, gridY);

    }

    public void renderDebugInfo(int gridX, int gridY) {
        long currentTime = System.currentTimeMillis();
        mouseOverMap = map.checkBounds(gridX, gridY);

        // **Update memory usage every 500ms instead of every frame**
        if (currentTime - lastMemoryUpdateTime > 500) {
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            usedMemory = (totalMemory - freeMemory) / 1024 / 1024;
            lastMemoryUpdateTime = currentTime;
        }

        // **Render debug background**
        shapeRenderer.setProjectionMatrix(cameraManager.getUiCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f); // Black with 50% transparency
        shapeRenderer.rect(cameraManager.getUiCamera().viewportWidth - 225, 5, 225, 150);
        shapeRenderer.end();


        batch.setProjectionMatrix(cameraManager.getUiCamera().combined);
        batch.begin();

        // **Use StringBuilder to reduce font.draw() calls**
        StringBuilder debugText = new StringBuilder();

        if (inspect && mouseOverMap) {
            debugText.append("Tile: ").append(map.getTile(gridX, gridY).getTerrainId()).append("\n");
        }

        if (mouseOverMap) {
            debugText.append("Mouse: ").append(gridY).append(",").append(gridX).append("\n");
        }

        debugText.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append("\n")
            .append("Used Mem: ").append(usedMemory).append(" MB\n")
            .append("Max Mem: ").append(maxMemory).append(" MB");

        font.draw(batch, debugText.toString(), cameraManager.getUiCamera().viewportWidth - 220, 100);

        batch.end();
    }

    public void renderMap(){
        batch.setProjectionMatrix(cameraManager.getMapCamera().combined);
        batch.begin();

        float camX = cameraManager.getMapCamera().position.x;
        float camY = cameraManager.getMapCamera().position.y;

        // Get viewport dimensions (assuming an orthographic camera)
        float viewportWidth = cameraManager.getMapCamera().viewportWidth * cameraManager.getMapCamera().zoom;
        float viewportHeight = cameraManager.getMapCamera().viewportHeight * cameraManager.getMapCamera().zoom;
        int offsetX = (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
        int offsetY = (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;

        // Convert to tile indices (clamp values to avoid out-of-bounds errors)
        int startX = Math.max(0, (int) ((camX - viewportWidth / 2 - offsetX) / TILE_SIZE));
        int startY = Math.max(0, (int) ((camY - viewportHeight / 2 - offsetY) / TILE_SIZE));
        int endX = Math.min(MAP_WIDTH - 1, (int) ((camX + viewportWidth / 2 - offsetX) / TILE_SIZE));
        int endY = Math.min(MAP_HEIGHT - 1, (int) ((camY + viewportHeight / 2 - offsetY) / TILE_SIZE));

        // Ensure these values are within valid bounds
        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(MAP_WIDTH - 1, endX);
        endY = Math.min(MAP_HEIGHT - 1, endY);

        //Gdx.app.log("MapMaker", "StartX " + startX + " StartY " + startY + " EndX " + endX + " EndY " + endY);


        map.renderMap(TILE_SIZE, batch, is3DView, startX, startY, endX, endY);
        batch.end();
    }

    private void fillBoard() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain(selectedTile));
            }
        }
    }


    private void reloadJson() {
        TerrainLoader.loadTerrains();
        forceUpdateAllSeaTiles();
    }

    private void smartPlaceSea(int x, int y) {
        if (!map.checkBounds(x, y) || map.getTile(x, y).getTerrain().getTextureId().contains("S")) {
            return;
        }

        // Place initial sea tile
        map.getTile(x, y).updateTerrain(TerrainManager.getInstance().getTerrain("SS"));

        // Immediately update all sea tiles on the map
        forceUpdateAllSeaTiles(x, y);
    }

    private void forceUpdateAllSeaTiles(int gridX, int gridY) {
        List<int[]> seaTiles = new ArrayList<>();

        int startX = Math.max(0, gridX - 5);
        int endX = Math.min(MAP_WIDTH - 1, gridX + 5);
        int startY = Math.max(0, gridY - 5);
        int endY = Math.min(MAP_HEIGHT - 1, gridY + 5);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (map.getTile(x, y).getTerrain().getTextureId().contains("S")) {
                    seaTiles.add(new int[]{x, y});
                }
            }
        }

        // Update each sea tile
        for (int[] pos : seaTiles) {
            updateSeaTileGuaranteed(pos[0], pos[1]);
        }
    }


    private void forceUpdateAllSeaTiles() {
        // Create a copy of all sea tile positions
        List<int[]> seaTiles = new ArrayList<>();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map.getTile(x, y).getTerrain().getTextureId().contains("S")) {
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

        boolean north = (y < MAP_HEIGHT - 1) && map.getTile(x, y + 1).getTerrain().getTextureId().contains("S");
        boolean east = (x < MAP_WIDTH - 1) && map.getTile(x + 1, y).getTerrain().getTextureId().contains("S");
        boolean south = (y > 0) && map.getTile(x, y - 1).getTerrain().getTextureId().contains("S");
        boolean west = (x > 0) && map.getTile(x - 1, y).getTerrain().getTextureId().contains("S");

        // Diagonal checks - MUST validate BOTH x and y bounds!
        boolean northeast = (x < MAP_WIDTH - 1) && (y < MAP_HEIGHT - 1) &&
            map.getTile(x + 1, y + 1).getTerrain().getTextureId().contains("S");
        boolean northwest = (x > 0) && (y < MAP_HEIGHT - 1) &&
            map.getTile(x - 1, y + 1).getTerrain().getTextureId().contains("S");
        boolean southeast = (x < MAP_WIDTH - 1) && (y > 0) &&
            map.getTile(x + 1, y - 1).getTerrain().getTextureId().contains("S");
        boolean southwest = (x > 0) && (y > 0) &&
            map.getTile(x - 1, y - 1).getTerrain().getTextureId().contains("S");


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

                // lake corners
                if ((!south && !southeast && !east && !northeast) || (!south && !southeast && !east && !southwest)
                    || (!south && !southwest && !east && !northeast)) {
                    returnString = "SLCBR";
                    break;
                }
                if ((!south && !southwest && !west && !northwest) || (!south && !southwest && !west && !southeast)
                    || (!south && !southeast && !west && !northwest)) {
                    returnString = "SLCBL";
                    break;
                }
                if ((!north && !northeast && !east && !northwest) || (!north && !northeast && !east && !southeast)
                    || (!north && !northwest && !east && !southeast)) {
                    returnString = "SLCTR";
                    break;
                }
                if ((!north && !northwest && !west && !northeast) || (!north && !northwest && !west && !southwest)
                    || (!north && !northeast && !west && !southwest)) {
                    returnString = "SLCTL";
                    break;
                }
                break;


            case 5:

                // lake extending corners
                if ((!northwest && !west && !southeast) || (!west && !southwest && !southeast)){
                    returnString = "SLECBLV";
                    break;
                }
                if ((!northeast && !east && !southwest) || (!southwest && !east && !southeast)){
                    returnString = "SLECBRV";
                    break;
                }
                if ((!south && !southeast && !northwest) || (!south && !southwest && !northwest)){
                    returnString = "SLECBLH";
                    break;
                }
                if ((!south && !southwest && !northeast) || (!south && !southeast && !northeast)){
                    returnString = "SLECBRH";
                    break;
                }
                if ((!west && !southwest && !northeast) || (!west && !northwest && !northeast)){
                    returnString = "SLECTLV";
                    break;
                }
                if (!north && !northwest && !southwest){
                    returnString = "SLECTLH";
                    break;
                }
                if ((!east && !southeast && !northwest) || (!east && !northeast && !northwest)){
                    returnString = "SLECTRV";
                    break;
                }
                if (!north && !northwest && !southeast){
                    returnString = "SLECTRH";
                    break;
                }


                // t functions
                if (!west && !northeast && !southeast){
                    returnString = "STFL";
                    break;
                }

                //lake corners
                if ((!south && !southeast && !east) || (!south && !east && !northeast)
                    || (!south && !southwest && !east)){
                    returnString = "SLCBR";
                    break;
                }
                if ((!south && !southwest && !west) ||(!west && !south && !southeast)
                    || (!west && !northwest && !south)){
                    returnString = "SLCBL";
                    break;
                }
                if ((!north && !northeast && !east) || (!north && !east && !southeast)
                    || (!north && !northwest && !east)){
                    returnString = "SLCTR";
                    break;
                }
                if ((!north && !northwest && !west) || (!west && !north && !northeast)
                    || (!north && !west && !southwest)){
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

                // lake corners
                if (!north && !west){
                    returnString = "SLCTL";
                    break;
                }
                if (!north && !east){
                    returnString = "SLCTR";
                    break;
                }
                if (!east && !south){
                    returnString = "SLCBR";
                    break;
                }
                if (!west && !south){
                    returnString = "SLCBL";
                    break;
                }

                // lake extending corners
                if (!northeast && !south){
                    returnString = "SLECBRH";
                    break;
                }
                if (!southwest && !east){
                    returnString = "SLECBRV";
                    break;
                }
                if (!south && !northwest){
                    returnString = "SLECBLH";
                    break;
                }
                if (!southeast && !west) {
                    returnString = "SLECBLV";
                    break;
                }
                if (!west && !northeast){
                    returnString = "SLECTLV";
                    break;
                }
                if (!north && !southwest){
                    returnString = "SLECTLH";
                    break;
                }
                if (!north && !southeast){
                    returnString = "SLECTRH";
                    break;
                }
                if (!east && !northwest){
                    returnString = "SLECTRV";
                    break;
                }

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
    // Helper method to get the index of the selected tile
    private int getSelectedTileIndex(String selectedTileId, List<Terrain> terrains) {
        for (int i = 0; i < terrains.size(); i++) {
            if (terrains.get(i).getTextureId().equals(selectedTileId)) {
                return i;
            }
        }
        return -1;
    }

    private void drawDamageRadius(int mouseX, int mouseY) {
        // Radius of damage effect in tiles, scaled by zoom level
        int radius = (int)(TILE_SIZE * (1.0f / cameraManager.getZoomLevel()));

        // Convert mouse coordinates to map coordinates
        int gridX = (mouseX - (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2) / TILE_SIZE;
        int gridY = (mouseY - (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2) / TILE_SIZE;

        // Calculate the center of the damage radius in pixel coordinates
        float centerX = gridX * TILE_SIZE + TILE_SIZE / 2 + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
        float centerY = gridY * TILE_SIZE + TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;

        // Set the transparency for the damage radius
        float alpha = 0.5f;
        batch.setColor(1.0f, 1.0f, 1.0f, alpha);

        // Draw the damage radius texture
        batch.begin();
        batch.draw(damageRadius,
            centerX - radius / 2,
            centerY - radius / 2,
            radius, radius);  // Use scaled size
        batch.end();

        // Reset color to default
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void applyDamageInRadius(int mouseX, int mouseY) {
        int radius = (int)(TILE_SIZE * (1.0f / cameraManager.getZoomLevel()));  // Scaled radius

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
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1f));
        shapeRenderer.rect(0, 0, toolbarWidth, toolbarHeight);
        shapeRenderer.end();

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();




//        if (isInfoIconClicked && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
//            game.setScreen(new InfoScreen(game));
//        }


        // Existing icons (moved up to make space)
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 128, grabIcon, iconSize);
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 192, damageIcon, iconSize);
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 256, fillIcon, iconSize);
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 320, reloadIcon, iconSize);
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 384, saveIcon, iconSize);
        drawAndHighlightIcon(mouseX, mouseY, 0, Gdx.graphics.getHeight() - 448, inspectIcon, iconSize);
    }

    private boolean drawAndHighlightIcon(int mouseX, int mouseY, int x, int y, TextureRegion icon, int iconSize) {
        boolean isMouseOver = mouseX >= x && mouseX <= x + iconSize && mouseY >= y && mouseY <= y + iconSize;

        if (isMouseOver) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(x, y, iconSize, iconSize);
            shapeRenderer.end();
        }

        batch.begin();
        batch.draw(icon, x, y, iconSize, iconSize);
        batch.end();

        return isMouseOver;
    }

    private void drawTilePicker() {
        // Set up for UI rendering
        batch.setProjectionMatrix(cameraManager.getUiCamera().combined);
        shapeRenderer.setProjectionMatrix(cameraManager.getUiCamera().combined);

        TerrainManager terrainManager = TerrainManager.getInstance();
        List<Terrain> includedTerrains = new ArrayList<>();

        // Collect only the terrains that are not excluded from the tile picker
        for (Terrain terrain : terrainManager.getTerrains()) {
            if (!terrain.isExcludeTilePicker()) {
                includedTerrains.add(terrain);
            }
        }

        // Calculate width based on number of tiles
        int numTiles = includedTerrains.size();
        int pickerWidth = numTiles * TILE_SIZE + 20; // 10px padding on each side
        int pickerHeight = TILE_PICKER_HEIGHT + 20; // Add some extra height for padding
        int startX = (Gdx.graphics.getWidth() - pickerWidth) / 2;
        int startY = Gdx.graphics.getHeight() - pickerHeight;

        // Draw background for the tile picker
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1f));  // Dark background
        shapeRenderer.rect(startX - 2, startY - 2, pickerWidth + 4, pickerHeight + 4);  // Outer border
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));  // Slightly lighter inner area
        shapeRenderer.rect(startX, startY, pickerWidth, pickerHeight);  // Inner area
        shapeRenderer.end();

        batch.begin();

        // Draw the terrains with some padding from the edges
        int tilePickerContentStartY = startY + 10; // Add 10px padding from bottom
        for (int i = 0; i < numTiles; i++) {
            Terrain terrain = includedTerrains.get(i);
            batch.draw(AtlasManager.getInstance().getTexture(terrain.getTextureId()),
                startX + i * TILE_SIZE + 10, // Add 10px padding from left
                tilePickerContentStartY,
                TILE_SIZE,
                TILE_PICKER_HEIGHT);
        }

        batch.end();

        // Draw selection highlight
        int selectedTileIndex = getSelectedTileIndex(selectedTile, includedTerrains);
        if (selectedTileIndex >= 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(
                startX + selectedTileIndex * TILE_SIZE + 10, // Match the padding from left
                tilePickerContentStartY,
                TILE_SIZE,
                TILE_PICKER_HEIGHT);
            shapeRenderer.end();
        }

        // Handle tile selection (using screen coordinates)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Convert mouse coordinates to screen coordinates
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Flip Y coordinate

            // Check if click is within the tile picker area
            if (mouseX >= startX && mouseX <= startX + pickerWidth &&
                mouseY >= startY && mouseY <= startY + pickerHeight) {

                // Calculate which terrain was clicked
                int clickedTerrainIndex = (mouseX - startX - 10) / TILE_SIZE; // Account for padding

                if (clickedTerrainIndex >= 0 && clickedTerrainIndex < numTiles) {
                    Terrain clickedTerrain = includedTerrains.get(clickedTerrainIndex);
                    isPlacing = true;
                    isGrabbing = false;
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); // Revert to the default cursor
                    selectedTile = clickedTerrain.getTextureId();

                    // Optional: Play a sound or provide feedback
                    Gdx.app.log("TilePicker", "Selected tile: " + selectedTile);
                }
            }
        }
    }


    @Override
    public boolean scrolled(float amountX, float amountY) {
        cameraManager.scrolled(amountX, amountY);
        return true;
    }
    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);

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
    public boolean keyDown(int keycode) {

        // Movement keys
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                cameraManager.setMovingUp(true);
                return true;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                cameraManager.setMovingDown(true);
                return true;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                cameraManager.setMovingLeft(true);
                return true;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                cameraManager.setMovingRight(true);
                return true;
        }

        // Track zoom key states
        if (keycode == Input.Keys.Z) {
            cameraManager.setZoomingIn(true);
            return true;
        }
        if (keycode == Input.Keys.X) {
            cameraManager.setZoomingOut(true);
            return true;
        }

        // Sprinting key
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
            cameraManager.setSprinting(true); // Enable sprinting
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        // Movement keys
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.W:
                cameraManager.setMovingUp(false);
                return true;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                cameraManager.setMovingDown(false);
                return true;
            case Input.Keys.LEFT:
            case Input.Keys.A:
                cameraManager.setMovingLeft(false);
                return true;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                cameraManager.setMovingRight(false);
                return true;
        }

        // Stop zooming when keys are released
        if (keycode == Input.Keys.Z) {
            cameraManager.setZoomingIn(false);
            return true;
        }
        if (keycode == Input.Keys.X) {
            cameraManager.setZoomingOut(false);
            return true;
        }

        // Sprinting key
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
            cameraManager.setSprinting(false); // Disable sprinting
            return true;
        }

        return false;
    }

    @Override public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            int flippedY = Gdx.graphics.getHeight() - screenY;

            // Check if click is in tilepicker first
            if (isClickInTilePicker(screenX, flippedY)) {
                List<Terrain> includedTerrains = getIncludedTerrains();
                int numTiles = includedTerrains.size();
                int pickerWidth = numTiles * TILE_SIZE + 20;
                int startX = (Gdx.graphics.getWidth() - pickerWidth) / 2;

                // Calculate which terrain was clicked
                int clickedTerrainIndex = (screenX - startX - 10) / TILE_SIZE;

                if (clickedTerrainIndex >= 0 && clickedTerrainIndex < numTiles) {
                    Terrain clickedTerrain = includedTerrains.get(clickedTerrainIndex);
                    isPlacing = true;
                    selectedTile = clickedTerrain.getTextureId();
                    Gdx.app.log("TilePicker", "Selected tile: " + selectedTile);
                }
                return true; // Consume the event
            }

            // Check if click is in toolbar
            if (screenX >= 0 && screenX <= 64) {
                // Handle toolbar clicks...
                return true; // Consume the event
            }

            // If we get here, it's a map click - start dragging
            isDraggingToPlace = true;
            return true; // Important: return true to get subsequent drag events
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDraggingToPlace = false;
            return true; // Consume the event
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Continue dragging if we're in that state
        return isDraggingToPlace;
    }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    private boolean isClickInTilePicker(int screenX, int screenY) {
        int numTiles = getIncludedTerrains().size();
        int pickerWidth = numTiles * TILE_SIZE + 20;
        int pickerHeight = TILE_PICKER_HEIGHT + 20;
        int startX = (Gdx.graphics.getWidth() - pickerWidth) / 2;
        int startY = Gdx.graphics.getHeight() - pickerHeight;

        return screenX >= startX &&
            screenX <= startX + pickerWidth &&
            screenY >= startY &&
            screenY <= startY + pickerHeight;
    }

    private List<Terrain> getIncludedTerrains() {
        TerrainManager terrainManager = TerrainManager.getInstance();
        List<Terrain> includedTerrains = new ArrayList<>();
        for (Terrain terrain : terrainManager.getTerrains()) {
            if (!terrain.isExcludeTilePicker()) {
                includedTerrains.add(terrain);
            }
        }
        return includedTerrains;
    }

    @Override
    public void dispose() {

        Gdx.input.setInputProcessor(null);

        batch.dispose();
        shapeRenderer.dispose();
        if (grabCursor != null) {
            grabCursor.dispose(); // Dispose of the cursor properly
        }

        AtlasManager.getInstance().dispose();
        font.dispose();
        map.dispose();
    }
}
