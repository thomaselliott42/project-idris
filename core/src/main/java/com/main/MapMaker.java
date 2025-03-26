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

import java.util.Random;

public class MapMaker implements Screen {

    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 20;
    private final int MAP_HEIGHT = 20;
    private final float EARTHQUAKE_INTERVAL = 2.0f; // Time between earthquakes in seconds
    private final float SHAKE_DURATION = 0.5f; // How long the shake lasts in seconds
    private final float SHAKE_INTENSITY = 10f; // Max intensity of shake (pixels)


    private final int TILE_PICKER_HEIGHT = 64; // Height of the tile picker
    private String[][] map = new String[MAP_HEIGHT][MAP_WIDTH];
    private String[][] shadowMap = new String[TILE_SIZE][TILE_SIZE];
    private Texture cursor;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    // Example textures (make sure to put them in assets folder)
    private Texture tileP;
    private Texture tileD;
    private Texture tileF;
    private Texture tileFD;

    private Texture tileM;
    private Texture tileDM;
    private Texture tileRuptured;

    // sea
    private Texture tileS;
    private Texture tileS3L;
    private Texture tileS3R;
    private Texture tileSPS;
    private Texture tileSCT;
    private Texture tileSCB;
    private Texture tileS2;
    private Texture tileSLU;
    private Texture tileSJD;
    private Texture tileSJU;
    private Texture tileSJA;
    private Texture tileSBR;
    private Texture tileSBL;
    private Texture tileSLJ;
    private Texture tileSTL;
    private Texture tileSTR;
    private Texture tileTopS;
    private Texture tileBottomS;
    private Texture tileSR;
    private Texture tileSL;

    // desert sea
    private Texture tileSDO;
    private Texture tileSDCB;
    private Texture tileSDCL;
    private Texture tileSDCR;
    private Texture tileSDCT;
    private Texture tileSDHS;
    private Texture tileSDVS;



    // road
    private Texture tileRH;
    private Texture tileRV;
    private Texture tileBH;
    private Texture tileBV;



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

        // Load textures
        tileP = new Texture("plain.png");
        tileM = new Texture("mountain.png");

        // Forests
        tileF = new Texture("forest.png");
        tileFD = new Texture("palmTrees.png");

        tileD = new Texture("desert.png");
        tileDM = new Texture("desertMountain.png");
        tileRuptured = new Texture("earthquake.png"); // Texture for ruptured tiles

        // Sea Textures
        tileS = new Texture("sea/sea.png");
        tileSPS = new Texture("sea/plains/sps.png");
        tileS3L = new Texture("sea/plains/s3l.png");
        tileS3R = new Texture("sea/plains/s3r.png");
        tileSCT = new Texture("sea/plains/sct.png");
        tileSCB = new Texture("sea/plains/scb.png");
        tileS2 = new Texture("sea/plains/s2.png");
        tileSLU = new Texture("sea/plains/slu.png");
        tileSJD = new Texture("sea/plains/sjd.png");
        tileSJU = new Texture("sea/plains/sju.png");
        tileSBR = new Texture("sea/plains/sbr.png");
        tileSBL = new Texture("sea/plains/sbl.png");
        tileSLJ = new Texture("sea/plains/slj.png");
        tileSTL = new Texture("sea/plains/stl.png");
        tileSTR = new Texture("sea/plains/str.png");
        tileTopS = new Texture("sea/plains/topS.png");
        tileBottomS = new Texture("sea/plains/bottomS.png");
        tileSR = new Texture("sea/plains/sr.png");
        tileSL = new Texture("sea/plains/sl.png");
        tileSJA = new Texture("sea/plains/sja.png");

        // desert sea
        tileSDO = new Texture("sea/desert/oasis.png");
        tileSDCB = new Texture("sea/desert/sdcb.png");
        tileSDCL = new Texture("sea/desert/sdcl.png");
        tileSDCR = new Texture("sea/desert/sdcr.png");
        tileSDCT = new Texture("sea/desert/sdct.png");
        tileSDHS = new Texture("sea/desert/sdhs.png");
        tileSDVS = new Texture("sea/desert/sdvs.png");


        // Road Textures
        tileRH = new Texture("road/roadH.png");
        tileRV = new Texture("road/roadV.png");
        tileBH = new Texture("road/bridgeH.png");
        tileBV = new Texture("road/bridgeV.png");

        cursor = new Texture("cursor.png");

//        ImGuiHandler.initImGui();

        generateRandomMap();
    }

    private void generateRandomMap() {
        String[] tiles = {"P", "F", "M"};
        Random rand = new Random();

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                map[y][x] = tiles[rand.nextInt(tiles.length)];
            }
        }

        // GENERATE ANOTHER MAP OF ENVIRONMENTS
        // MADE UP OF D, P, S
        // MAKE THE SYMBOLS UNIVERSAL AND THEN WHEN FINDING THE TEXTURE CHECK THIS MAP
        // TO GET THE RIGHT SYMBOL

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if(map[y][x].equals("P") || map[y][x].equals("M") || map[y][x].equals("F")) {
                    shadowMap[y][x] = "P";
                }
            }
        }




    }

    private Texture getTileTexture(int x, int y, String tileID){
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if(shadowMap[y][x].equals("P")) {
                    switch (tileID) {
                        case "P":
                            return tileP;
                        case "S":
                            return tileS;
                        case "F":
                            return tileF;
                        case "FD":
                            return tileFD;
                        case "D":
                            return tileD;
                        case "DM":
                            return tileDM;
                        case "M":
                            return tileM;
                        case "R": // Ruptured tile texture
                            return tileRuptured;
                        case "SS":
                            return tileSPS;
                        case "S3L":
                            return tileS3L;
                        case "S3R":
                            return tileS3R;
                        case "SCT":
                            return tileSCT;
                        case "SCB":
                            return tileSCB;
                        case "S2":
                            return tileS2;
                        case "SLU":
                            return tileSLU;
                        case "SJD":
                            return tileSJD;
                        case "SJU":
                            return tileSJU;
                        case "SJA":
                            return tileSJA;
                        case "SBR":
                            return tileSBR;
                        case "SBL":
                            return tileSBL;
                        case "SLJ":
                            return tileSLJ;
                        case "STL":
                            return tileSTL;
                        case "STR":
                            return tileSTR;
                        case "STOPS":
                            return tileTopS;
                        case "SBOTTOMS":
                            return tileBottomS;
                        case "SL":
                            return tileSL;
                        case "SR":
                            return tileSR;
                        case "SDO":
                            return tileSDO;
                        case "SDCB":
                            return tileSDCB;
                        case "SDCL":
                            return tileSDCL;
                        case "SDCR":
                            return tileSDCR;
                        case "SDCT":
                            return tileSDCT;
                        case "SDHS":
                            return tileSDHS;
                        case "SDVS":
                            return tileSDVS;

                        case "RH":
                            return tileRH;
                        case "RV":
                            return tileRV;
                        case "BH":
                            return tileBH;
                        case "BV":
                            return tileBV;
                        default:
                            return tileP;
                }
            }
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);


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
//        if (earthquakeTimer >= EARTHQUAKE_INTERVAL) {
//            triggerEarthquake();
//            earthquakeTimer = 0.0f; // Reset the timer
//        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            switchRender = !switchRender;
        }


        if (!switchRender) {
            // First, render all tiles except mountain tiles
            // First, render all tiles except the mountains
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    Texture tex = getTileTexture(map[y][x]);

                    // Position tiles, offset to center the map
                    float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                    float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;

                    // Render non-mountain tiles
                    if (!map[y][x].equals("M")) {
                        if (map[y][x].equals("F")) {
                            batch.draw(getTileTexture("P"), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        }else if(map[y][x].equals("FD")) {
                            batch.draw(getTileTexture("D"), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        }
                        batch.draw(tex, drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }

// Then, render the mountain tiles starting from bottom-right to top-left
            for (int y = MAP_HEIGHT - 1; y >= 0; y--) { // Start from the bottom row
                for (int x = MAP_WIDTH - 1; x >= 0; x--) { // Start from the rightmost column
                    if (map[y][x].equals("M")) {
                        Texture tex = getTileTexture(map[y][x]);
                        float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                        float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                        batch.draw(getTileTexture("P"), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        batch.draw(tex, drawX, drawY, TILE_SIZE, tex.getHeight() * (TILE_SIZE / 16f));
                    }else if(map[y][x].equals("DM")) {
                        Texture tex = getTileTexture(map[y][x]);
                        float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                        float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                        batch.draw(getTileTexture("D"), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        batch.draw(tex, drawX, drawY, TILE_SIZE, tex.getHeight() * (TILE_SIZE / 16f));
                    }else if(map[y][x].equals("SDO")) {
                        Texture tex = getTileTexture(map[y][x]);
                        float drawX = (x * TILE_SIZE) + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2;
                        float drawY = (y * TILE_SIZE) + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                        batch.draw(getTileTexture("D"), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        batch.draw(tex, drawX, drawY, TILE_SIZE, tex.getHeight() * (TILE_SIZE / 16f));

                    }
                }
            }
        } else {

            float horizontalOffset = 10 * TILE_SIZE;  // 10 tiles to the right

            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    Texture tex = getTileTexture(map[y][x]);

                    // Apply fake 3D tilt to X and Y position of each tile
                    float drawX = (x - y) * TILE_SIZE + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2 + horizontalOffset;
                    ;
                    float drawY = (x + y) * TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                    // float drawX = (x - y) * TILE_SIZE + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2 + 32*5;
//                    float drawY = (x + y) * TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2 + 32*5;
                    // Render non-mountain tiles first
                    if (!map[y][x].equals("M")) {
                        if (map[y][x].equals("F")) {
                            batch.draw(getTileTexture("P"), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        }
                        batch.draw(tex, drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }

            // Then, render the mountain tiles starting from bottom-right to top-left with the fake 3D tilt
            for (int y = MAP_HEIGHT - 1; y >= 0; y--) { // Start from the bottom row
                for (int x = MAP_WIDTH - 1; x >= 0; x--) { // Start from the rightmost column
                    if (map[y][x].equals("M")) {
                        Texture tex = getTileTexture(map[y][x]);

                        // Apply fake 3D tilt to X and Y position of each mountain tile
                        float drawX = (x - y) * TILE_SIZE + (Gdx.graphics.getWidth() - MAP_WIDTH * TILE_SIZE) / 2 + horizontalOffset;
                        ;
                        float drawY = (x + y) * TILE_SIZE / 2 + (Gdx.graphics.getHeight() - MAP_HEIGHT * TILE_SIZE) / 2;
                        batch.draw(getTileTexture("P"), drawX, drawY, TILE_SIZE, TILE_SIZE);
                        batch.draw(tex, drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }

        }

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

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (selectedTile.equals("S")) {
                    smartPlaceSea(gridX, gridY);
                }
//                else if  (selectedTile.equals("RH")) {
//                    smartPlaceRoad(gridX, gridY);
//                }

                else {
                    map[gridY][gridX] = selectedTile;  // Place selected tile
                }
                updateBoard();
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.P)) {
            fillBoard();
        }


//        ImGuiHandler.startImGui();
//        ImGuiHandler.renderUI();
//        ImGuiHandler.endImGui();

    }

    private void fillBoard() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {

               map[y][x] = selectedTile;
            }
        }
    }

    private void updateBoard() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {

                if(map[y][x].contains("S")) {

                    smartPlaceSea(x, y);

                }
            }
        }
    }

    private void smartPlaceRoad(int x, int y) {


        // Define the directional labels and corresponding (dx, dy) offsets
        int[][] directions = {
            {-1, 1}, // TL: Top-left
            {0, 1}, // T: Top
            {1, 1}, // TR: Top-right
            {-1, 0}, // L: Left
            {1, 0}, // R: Right
            {-1, -1}, // BL: Bottom-left
            {0, -1}, // B: Bottom
            {1, -1}  // BR: Bottom-right
        };

        if (y >= 0 && y < MAP_HEIGHT && x >= 0 && x < MAP_WIDTH) {
            if (map[y][x].contains("S")) {
                Gdx.app.log("MapMaker", "smart place road");
                map[y][x] = "BH";
            }
            else {
                map[y][x] = selectedTile;
            }
        }

        // Loop through each direction
        for (int i = 0; i < directions.length; i++) {
            int dx = directions[i][0];
            int dy = directions[i][1];
            int checkX = x + dx;
            int checkY = y + dy;

            // Skip out-of-bound tiles
            if (checkX < 0 || checkX >= MAP_WIDTH || checkY < 0 || checkY >= MAP_HEIGHT) continue;



            // Check for sea tiles to the left and right
           // if (dy == 0) {
//                if (dx == -1 && map[x][checkX].contains("S")) {
//                    map[y][x - 1] = "BH";
//                }if (dx == 1 && map[checkY][checkX].contains("S")) {
//                    map[y][x + 1] = "BH";
//                }
            //}

//            if (dx == 0) {
//
//            }


        }


    }


    private void smartPlaceSea(int x, int y) {
        int count = 0;
        int sCount = 0;

        // Define the directional labels and corresponding (dx, dy) offsets
        int[][] directions = {
            {-1, 1}, // TL: Top-left
            {0, 1}, // T: Top
            {1, 1}, // TR: Top-right
            {-1, 0}, // L: Left
            {1, 0}, // R: Right
            {-1, -1}, // BL: Bottom-left
            {0, -1}, // B: Bottom
            {1, -1}  // BR: Bottom-right
        };

        boolean allPlains = true; // Flag to track if all surrounding tiles are plain
        boolean allSea = true; // Flag to track if all surrounding tiles are plain

        boolean seaLeft = false;  // Flag to track if there is a sea tile to the left
        boolean seaRight = false; // Flag to track if there is a sea tile to the right
        boolean threeAround = false;
        boolean seaDown = false;
        boolean seaUp = false;
        boolean extendSeaLeft = false;
        boolean extendSeaRight = false;

        boolean extendSeaUp = false;
        boolean extendSeaDown = false;
        boolean joinSeaDown = false;
        boolean joinSeaUp = false;
        boolean joinSeaUpCornerRight = false;
        boolean joinSeaUpCornerLeft = false;
        boolean jsd = false;
        boolean extendSeaDownJoin = false;
        boolean joinUpperSea = false;
        boolean jSDP = false;
        boolean jSDPR = false;
        boolean extendSeaDownLL = false;

        // Loop through each direction
        for (int i = 0; i < directions.length; i++) {
            int dx = directions[i][0];
            int dy = directions[i][1];
            int checkX = x + dx;
            int checkY = y + dy;

            // Skip out-of-bound tiles
            if (checkX < 0 || checkX >= MAP_WIDTH || checkY < 0 || checkY >= MAP_HEIGHT) continue;

            // Check if the adjacent tile is a "P" (plain)
            if (map[checkY][checkX].equals("P") || map[checkY][checkX].equals("M") || map[checkY][checkX].equals("F") || map[checkY][checkX].equals("D") || map[checkY][checkX].equals("DM")|| map[checkY][checkX].equals("FD")) {
                count++;
            } else {
                allPlains = false; // At least one surrounding tile is not plain
            }


            if (map[checkY][checkX].startsWith("S")) {
                count++;
            } else {
                allSea = false; // At least one surrounding tile is not plain
            }

            if(sCount > 2){
                allSea = true; // At least one surrounding tile is not plain

            }



            // Check for sea tiles to the left and right
            if (dy == 0) {
                if (dx == -1 && map[checkY][checkX].contains("SS") || map[checkY][checkX].contains("SDO")) {
                    seaLeft = true;
                } else if (dx == 1 && map[checkY][checkX].contains("SS")) {
                    seaRight = true;
                }else if (dx == 1 && map[checkY][checkX].contains("S3L") || map[checkY][checkX].contains("SDCL")) {
                    extendSeaLeft = true;
                }else if (dx == -1 && map[checkY][checkX].contains("S3R")){
                    extendSeaRight = true;
                }
                if(dx == -1 && map[checkY][checkX].contains("SCT")){
                    Gdx.app.log("MapMaker", "checking ");
                    map[y][x] = "S3R";
                    map[y][x-1] = "SBL";

                }

                if(x-1 < 0 && x+1 >= MAP_WIDTH){
                    if (map[y][x - 1].contains("SCB") && map[y][x + 1].contains("SCB")) {
                        Gdx.app.log("MapMaker", "joinUpperSea");
                        joinUpperSea = true;
                    }

                }

                if(dx == 1 && map[checkY][checkX].contains("SCB")) {
                    jSDP = true;
                }
                if(dx == -1 && map[checkY][checkX].contains("SCB")) {
                    jSDPR = true;
                }


//                if (dx == -1 && map[y][x].contains("S2")  && (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
//                    Gdx.app.log("MapMaker", "fix issue");
//                    map[y][x] = "S3L";
//
//                } else if (dx == 1 && map[y][x].contains("S2") && (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
//                    Gdx.app.log("MapMaker", "fix issue 2");
//                    map[y][x] = "S3R";
//                }
//
//                if(dx == -1 && map[y][x].equals("S")) {
//                    map[y][x+1] = "SR";
//                }
//                if(dx == 1 && map[y][x].equals("S")) {
//                    map[y][x-1] = "SL";
//                }
            }

            if (dx == 0) {
                if (dy == -1 && map[checkY][checkX].contains("SS") || map[checkY][checkX].contains("SDO")) {
                    seaUp = true;
                } else if (dy == 1 && map[checkY][checkX].contains("SS")) {
                    seaDown = true;
                }else if (dy == -1 && map[checkY][checkX].contains("SCB")) {
                    extendSeaUp = true;
                } else if (dy == 1 && map[checkY][checkX].contains("SCT")) {

                    extendSeaDown = true;
                }
                if(dy == 1 && map[checkY][checkX].contains("S2")) {
                    joinSeaDown = true;
                }

                else if(dy == -1 && map[checkY][checkX].contains("S2")) {
                    joinSeaUp = true;
                }else if(dy == -1 && map[checkY][checkX].contains("S3L")) {
                    joinSeaUpCornerLeft = true;
                } else if(dy == -1 && map[checkY][checkX].contains("S3R")) {
                    joinSeaUpCornerRight = true;

                }

                if(dy == 1 && map[checkY][checkX].contains("S3L")) {
                    Gdx.app.log("MapMaker", "extendSeaDownLL");
                    extendSeaDownLL = true;
                }

//                if (dy == -1 && map[y][x].contains("SLU") && (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
//                    Gdx.app.log("MapMaker", "fix issue");
//                    map[y][x] = "SCT";
//                } else if (dy == 1 && map[y][x].contains("SLU") &&
//                    (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
//                    Gdx.app.log("MapMaker", "fix issue 2");
//                    map[y][x] = "SCB";
//                }

//                if(dy == -1 && map[y][x].equals("S")) {
//                    map[y+1][x] = "STOPS";
//                }
//                if(dy == 1 && map[y][x].equals("S")) {
//                    map[y-1][x] = "SBOTTOMS";
//                }

//                if(dy == -1 && (map[checkY][checkX].equals("SJD")) || map[checkY][checkX].equals("SJU")) {
//                    map[y-1][x] = "SJA";
//                } else if(dy == 1 && (map[checkY][checkX].equals("SJD")) || map[checkY][checkX].equals("SJU")) {
//                    map[y+1][x] = "SJA";
//
//                }

            }


        }

        // If all surrounding tiles are plain, set the current tile to "SS"
        if (allPlains) {
            if (seaType(y, x).equals("D")){
                map[y][x] = "SDO";

            }
            else if(seaType(y, x).equals("P")){
                map[y][x] = "SS";
            }
        }

//        if(allSea){
//            map[y][x] = "S";
//        }

        // Check for sea tiles to the left or right and update map
        if (seaLeft && x > 0 ) {
            if (seaType(y, x).equals("D")){
                map[y][x] = "SDCR";
                if(seaType(y, (x-1)).equals("D")){
                    map[y][x - 1] = "SDCL";   // Tile to the left is sea-left
                }else{
                    map[y][x - 1] = "S3L";   // Tile to the left is sea-left
                }

            }
            else if(seaType(y, x).equals("P")){
                map[y][x] = "S3R";     // Current tile is sea-right
                if(seaType(y, (x-1)).equals("D")){
                    map[y][x - 1] = "SDCL";   // Tile to the left is sea-left
                }else{
                    map[y][x - 1] = "S3L";   // Tile to the left is sea-left
                }
            }

        }
        else if (seaRight && x < MAP_WIDTH - 1) {
            // Set the map tile to "S3R" if there is a sea tile to the right
            map[y][x] = "S3L";     // Current tile is sea-left
            map[y][x + 1] = "S3R";   // Tile to the right is sea-right
        }
        else if (extendSeaLeft && x > 0) {
            if (seaType(y, x).equals("D")){
                map[y][x] = "SDCL";
                if(seaType(y, (x+1)).equals("D")){
                    map[y][x + 1] = "SDHS";   // Tile to the left is sea-left
                }else{
                    map[y][x + 1] = "S3L";   // Tile to the left is sea-left
                }

            }else if(seaType(y, x).equals("P")){
                map[y][x] = "S3L";     // Current tile is sea-right
                if(seaType(y, (x+1)).equals("D")){
                    map[y][x + 1] = "SDHS";   // Tile to the left is sea-left
                }else{
                    map[y][x + 1] = "S2";   // Tile to the left is sea-left
                }
            }

        }
        else if (extendSeaRight && x < MAP_WIDTH - 1) {
            map[y][x] = "S3R";
            map[y][x - 1] = "S2";
        }

        if(seaUp && y > 0){
            map[y][x] = "SCB";     // Current tile is sea-right
            map[y-1][x] = "SCT";   // Tile to the left is sea-left
        }
        else if (seaDown && y < MAP_HEIGHT - 1) {
            map[y][x] = "SCT";     // Current tile is sea-right
            map[y + 1][x] = "SCB";   // Tile to the left is sea-left
        }
        else if (extendSeaUp && y > 0){
            map[y][x] = "SCB";     // Current tile is sea-right
            map[y-1][x] = "SLU";   // Tile to the left is sea-left
        }
        else if (extendSeaDown && y < MAP_HEIGHT - 1) {

            map[y][x] = "SCT";     // Current tile is sea-right
            map[y + 1][x] = "SLU";   // Tile to the left is sea-left
        }


        if(joinSeaUp && y > 0){
            map[y][x] = "SCB";
            map[y-1][x] = "SJU";
        } else if(joinSeaDown && y < MAP_HEIGHT - 1){
            map[y][x] = "SCT";
            map[y+1][x] = "SJD";
        }
//
        if(joinSeaUpCornerLeft && y > 0){
            map[y][x] = "SCB";
            map[y-1][x] = "SBL";
        }else if(joinSeaUpCornerRight && y > 0){
            map[y][x] = "SCB";
            map[y-1][x] = "SBR";
        }

        if(jSDP){
            map[y][x+1] = "STR";
            map[y][x] = "S3L";
        }else if(jSDPR){
            map[y][x-1] = "STL";
            map[y][x] = "S3R";
        }


        if(joinUpperSea){
            map[y][x+1] = "SCB";
            map[y][x-1] = "SCB";
            map[y][x] = "S2";

        }

        if(extendSeaDownLL){
            map[y][x] = "SCT";
            map[y+1][x] = "STL";

        }





    }

    public String seaType(int y, int x) {
        if (map[y][x].startsWith("SD") || map[y][x].contains("D")) {
            return "D";

        } else if (map[y][x].equals("P") || map[y][x].equals("F") || map[y][x].equals("M")) {
            return "P";
        }

        return "P";
    }


    public String getSelectedTile() {
        return selectedTile;
    }


    private void triggerEarthquake() {
        Random rand = new Random();
        int numRuptures = 5 + rand.nextInt(10); // Number of ruptures per earthquake (between 5 and 15)

        for (int i = 0; i < numRuptures; i++) {
            int x = rand.nextInt(MAP_WIDTH);
            int y = rand.nextInt(MAP_HEIGHT);

            // If the tile isn't already ruptured, rupture it
            if (!map[y][x].equals("R")) {
                map[y][x] = "R"; // Change the tile to "R" for ruptured
            }
        }

        shakeTimer = SHAKE_DURATION;

    }

    private void drawTilePicker() {
        int pickerWidth = 4 * TILE_SIZE;  // For 4 tiles in the picker
        int startX = (Gdx.graphics.getWidth() - pickerWidth) / 2;
        int startY = Gdx.graphics.getHeight() - TILE_PICKER_HEIGHT;

        // Draw tile picker background with a border for cleaner appearance
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1f)); // Dark background
        shapeRenderer.rect(startX - 2, startY - 2, pickerWidth + 4, TILE_PICKER_HEIGHT + 4); // Add some border
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 1f)); // Lighter background
        shapeRenderer.rect(startX, startY, pickerWidth, TILE_PICKER_HEIGHT);
        shapeRenderer.end();

        // Draw tiles in picker
        batch.begin();
        batch.draw(tileP, startX, startY, TILE_SIZE, TILE_PICKER_HEIGHT);
        batch.draw(tileS, startX + TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by TILE_SIZE
        batch.draw(tileF, startX + 2 * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by 2 * TILE_SIZE
        batch.draw(tileM, startX + 3 * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by 3 * TILE_SIZE
        batch.draw(tileRH, startX + 4 * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by 3 * TILE_SIZE
        batch.draw(tileRV, startX + 5 * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by 3 * TILE_SIZE
        batch.draw(tileD, startX + 6 * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by 3 * TILE_SIZE
        batch.draw(tileDM, startX + 7 * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by 3 * TILE_SIZE
        batch.draw(tileFD, startX + 8 * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT); // Shift by 3 * TILE_SIZE

        batch.end();

        // Highlight the selected tile
        int selectedTileX = 0;
        if (selectedTile.equals("S")) selectedTileX = 1;
        else if (selectedTile.equals("F")) selectedTileX = 2;
        else if (selectedTile.equals("M")) selectedTileX = 3;
        else if (selectedTile.equals("RH")) selectedTileX = 4;
        else if (selectedTile.equals("RV")) selectedTileX = 5;
        else if (selectedTile.equals("D")) selectedTileX = 6;
        else if (selectedTile.equals("DM")) selectedTileX = 7;
        else if (selectedTile.equals("FD")) selectedTileX = 8;




        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(startX + selectedTileX * TILE_SIZE, startY, TILE_SIZE, TILE_PICKER_HEIGHT);
        shapeRenderer.end();

        // Check for tile selection
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Check if mouse is within the tile picker area
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) &&
            mouseY >= startY && mouseY <= startY + TILE_PICKER_HEIGHT) {

            // Check if mouse X position is within the bounds of the tiles
            if (mouseX >= startX && mouseX < startX + TILE_SIZE) {
                selectedTile = "P";  // Plain tile selected
            } else if (mouseX >= startX + TILE_SIZE && mouseX < startX + 2 * TILE_SIZE) {
                selectedTile = "S";  // Sea tile selected
            } else if (mouseX >= startX + 2 * TILE_SIZE && mouseX < startX + 3 * TILE_SIZE) {
                selectedTile = "F";  // Forest tile selected
            } else if (mouseX >= startX + 3 * TILE_SIZE && mouseX < startX + 4 * TILE_SIZE) {
                selectedTile = "M";  // Mountain tile selected
            } else if (mouseX >= startX + 4 * TILE_SIZE && mouseX < startX + 5 * TILE_SIZE) {
                selectedTile = "RH";  // Mountain tile selected
            } else if (mouseX >= startX + 5 * TILE_SIZE && mouseX < startX + 6 * TILE_SIZE) {
                selectedTile = "RV";  // Mountain tile selected
            }else if (mouseX >= startX + 6 * TILE_SIZE && mouseX < startX + 7 * TILE_SIZE) {
                selectedTile = "D";  // Mountain tile selected
            }else if (mouseX >= startX + 7 * TILE_SIZE && mouseX < startX + 8 * TILE_SIZE) {
                selectedTile = "DM";  // Mountain tile selected
            }else if (mouseX >= startX + 8 * TILE_SIZE && mouseX < startX + 9 * TILE_SIZE) {
                selectedTile = "FD";  // Mountain tile selected
            }
        }
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
        tileP.dispose();
        tileD.dispose();
        tileS.dispose();
        tileF.dispose();

        cursor.dispose();

        //   ImGuiHandler.disposeImGui();

    }
}
