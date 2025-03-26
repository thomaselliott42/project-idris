//import com.badlogic.gdx.Gdx;
//
//private void smartPlaceSea(int x, int y) {
//    int count = 0;
//    int sCount = 0;
//
//    // Define the directional labels and corresponding (dx, dy) offsets
//    int[][] directions = {
//        {-1, 1}, // TL: Top-left
//        {0, 1}, // T: Top
//        {1, 1}, // TR: Top-right
//        {-1, 0}, // L: Left
//        {1, 0}, // R: Right
//        {-1, -1}, // BL: Bottom-left
//        {0, -1}, // B: Bottom
//        {1, -1}  // BR: Bottom-right
//    };
//
//    boolean allPlains = true; // Flag to track if all surrounding tiles are plain
//    boolean allSea = true; // Flag to track if all surrounding tiles are plain
//
//    boolean seaLeft = false;  // Flag to track if there is a sea tile to the left
//    boolean seaRight = false; // Flag to track if there is a sea tile to the right
//    boolean threeAround = false;
//    boolean seaDown = false;
//    boolean seaUp = false;
//    boolean extendSeaLeft = false;
//    boolean extendSeaRight = false;
//
//    boolean extendSeaUp = false;
//    boolean extendSeaDown = false;
//    boolean joinSeaDown = false;
//    boolean joinSeaUp = false;
//    boolean joinSeaUpCornerRight = false;
//    boolean joinSeaUpCornerLeft = false;
//    boolean jsd = false;
//    boolean extendSeaDownJoin = false;
//    boolean joinUpperSea = false;
//    boolean jSDP = false;
//    boolean jSDPR = false;
//    boolean extendSeaDownLL = false;
//
//    // Loop through each direction
//    for (int i = 0; i < directions.length; i++) {
//        int dx = directions[i][0];
//        int dy = directions[i][1];
//        int checkX = x + dx;
//        int checkY = y + dy;
//
//        // Skip out-of-bound tiles
//        if (checkX < 0 || checkX >= MAP_WIDTH || checkY < 0 || checkY >= MAP_HEIGHT) continue;
//
//        // Check if the adjacent tile is a "P" (plain)
//        if (map[checkY][checkX].equals("P") || map[checkY][checkX].equals("M") || map[checkY][checkX].equals("F") || map[checkY][checkX].equals("D") || map[checkY][checkX].equals("DM")|| map[checkY][checkX].equals("FD")) {
//            count++;
//        } else {
//            allPlains = false; // At least one surrounding tile is not plain
//        }
//
//
//        if (map[checkY][checkX].startsWith("S")) {
//            count++;
//        } else {
//            allSea = false; // At least one surrounding tile is not plain
//        }
//
//        if(sCount > 2){
//            allSea = true; // At least one surrounding tile is not plain
//
//        }
//
//
//
//        // Check for sea tiles to the left and right
//        if (dy == 0) {
//            if (dx == -1 && map[checkY][checkX].contains("SS") || map[checkY][checkX].contains("SDO")) {
//                seaLeft = true;
//            } else if (dx == 1 && map[checkY][checkX].contains("SS")) {
//                seaRight = true;
//            }else if (dx == 1 && map[checkY][checkX].contains("S3L") || map[checkY][checkX].contains("SDCL")) {
//                extendSeaLeft = true;
//            }else if (dx == -1 && map[checkY][checkX].contains("S3R")){
//                extendSeaRight = true;
//            }
//            if(dx == -1 && map[checkY][checkX].contains("SCT")){
//                Gdx.app.log("MapMaker", "checking ");
//                map[y][x] = "S3R";
//                map[y][x-1] = "SBL";
//
//            }
//
//            if(x-1 < 0 && x+1 >= MAP_WIDTH){
//                if (map[y][x - 1].contains("SCB") && map[y][x + 1].contains("SCB")) {
//                    Gdx.app.log("MapMaker", "joinUpperSea");
//                    joinUpperSea = true;
//                }
//
//            }
//
//            if(dx == 1 && map[checkY][checkX].contains("SCB")) {
//                jSDP = true;
//            }
//            if(dx == -1 && map[checkY][checkX].contains("SCB")) {
//                jSDPR = true;
//            }
//
//
////                if (dx == -1 && map[y][x].contains("S2")  && (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
////                    Gdx.app.log("MapMaker", "fix issue");
////                    map[y][x] = "S3L";
////
////                } else if (dx == 1 && map[y][x].contains("S2") && (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
////                    Gdx.app.log("MapMaker", "fix issue 2");
////                    map[y][x] = "S3R";
////                }
////
////                if(dx == -1 && map[y][x].equals("S")) {
////                    map[y][x+1] = "SR";
////                }
////                if(dx == 1 && map[y][x].equals("S")) {
////                    map[y][x-1] = "SL";
////                }
//        }
//
//        if (dx == 0) {
//            if (dy == -1 && map[checkY][checkX].contains("SS") || map[checkY][checkX].contains("SDO")) {
//                seaUp = true;
//            } else if (dy == 1 && map[checkY][checkX].contains("SS")) {
//                seaDown = true;
//            }else if (dy == -1 && map[checkY][checkX].contains("SCB")) {
//                extendSeaUp = true;
//            } else if (dy == 1 && map[checkY][checkX].contains("SCT")) {
//
//                extendSeaDown = true;
//            }
//            if(dy == 1 && map[checkY][checkX].contains("S2")) {
//                joinSeaDown = true;
//            }
//
//            else if(dy == -1 && map[checkY][checkX].contains("S2")) {
//                joinSeaUp = true;
//            }else if(dy == -1 && map[checkY][checkX].contains("S3L")) {
//                joinSeaUpCornerLeft = true;
//            } else if(dy == -1 && map[checkY][checkX].contains("S3R")) {
//                joinSeaUpCornerRight = true;
//
//            }
//
//            if(dy == 1 && map[checkY][checkX].contains("S3L")) {
//                Gdx.app.log("MapMaker", "extendSeaDownLL");
//                extendSeaDownLL = true;
//            }
//
////                if (dy == -1 && map[y][x].contains("SLU") && (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
////                    Gdx.app.log("MapMaker", "fix issue");
////                    map[y][x] = "SCT";
////                } else if (dy == 1 && map[y][x].contains("SLU") &&
////                    (map[checkY][checkX].contains("P") || map[checkY][checkX].contains("M") || map[checkY][checkX].contains("F"))) {
////                    Gdx.app.log("MapMaker", "fix issue 2");
////                    map[y][x] = "SCB";
////                }
//
////                if(dy == -1 && map[y][x].equals("S")) {
////                    map[y+1][x] = "STOPS";
////                }
////                if(dy == 1 && map[y][x].equals("S")) {
////                    map[y-1][x] = "SBOTTOMS";
////                }
//
////                if(dy == -1 && (map[checkY][checkX].equals("SJD")) || map[checkY][checkX].equals("SJU")) {
////                    map[y-1][x] = "SJA";
////                } else if(dy == 1 && (map[checkY][checkX].equals("SJD")) || map[checkY][checkX].equals("SJU")) {
////                    map[y+1][x] = "SJA";
////
////                }
//
//        }
//
//
//    }
//
//    // If all surrounding tiles are plain, set the current tile to "SS"
//    if (allPlains) {
//        if (seaType(y, x).equals("D")){
//            map[y][x] = "SDO";
//
//        }
//        else if(seaType(y, x).equals("P")){
//            map[y][x] = "SS";
//        }
//    }
//
////        if(allSea){
////            map[y][x] = "S";
////        }
//
//    // Check for sea tiles to the left or right and update map
//    if (seaLeft && x > 0 ) {
//        if (seaType(y, x).equals("D")){
//            map[y][x] = "SDCR";
//            if(seaType(y, (x-1)).equals("D")){
//                map[y][x - 1] = "SDCL";   // Tile to the left is sea-left
//            }else{
//                map[y][x - 1] = "S3L";   // Tile to the left is sea-left
//            }
//
//        }
//        else if(seaType(y, x).equals("P")){
//            map[y][x] = "S3R";     // Current tile is sea-right
//            if(seaType(y, (x-1)).equals("D")){
//                map[y][x - 1] = "SDCL";   // Tile to the left is sea-left
//            }else{
//                map[y][x - 1] = "S3L";   // Tile to the left is sea-left
//            }
//        }
//
//    }
//    else if (seaRight && x < MAP_WIDTH - 1) {
//        // Set the map tile to "S3R" if there is a sea tile to the right
//        map[y][x] = "S3L";     // Current tile is sea-left
//        map[y][x + 1] = "S3R";   // Tile to the right is sea-right
//    }
//    else if (extendSeaLeft && x > 0) {
//        if (seaType(y, x).equals("D")){
//            map[y][x] = "SDCL";
//            if(seaType(y, (x+1)).equals("D")){
//                map[y][x + 1] = "SDHS";   // Tile to the left is sea-left
//            }else{
//                map[y][x + 1] = "S3L";   // Tile to the left is sea-left
//            }
//
//        }else if(seaType(y, x).equals("P")){
//            map[y][x] = "S3L";     // Current tile is sea-right
//            if(seaType(y, (x+1)).equals("D")){
//                map[y][x + 1] = "SDHS";   // Tile to the left is sea-left
//            }else{
//                map[y][x + 1] = "S2";   // Tile to the left is sea-left
//            }
//        }
//
//    }
//    else if (extendSeaRight && x < MAP_WIDTH - 1) {
//        map[y][x] = "S3R";
//        map[y][x - 1] = "S2";
//    }
//
//    if(seaUp && y > 0){
//        map[y][x] = "SCB";     // Current tile is sea-right
//        map[y-1][x] = "SCT";   // Tile to the left is sea-left
//    }
//    else if (seaDown && y < MAP_HEIGHT - 1) {
//        map[y][x] = "SCT";     // Current tile is sea-right
//        map[y + 1][x] = "SCB";   // Tile to the left is sea-left
//    }
//    else if (extendSeaUp && y > 0){
//        map[y][x] = "SCB";     // Current tile is sea-right
//        map[y-1][x] = "SLU";   // Tile to the left is sea-left
//    }
//    else if (extendSeaDown && y < MAP_HEIGHT - 1) {
//
//        map[y][x] = "SCT";     // Current tile is sea-right
//        map[y + 1][x] = "SLU";   // Tile to the left is sea-left
//    }
//
//
//    if(joinSeaUp && y > 0){
//        map[y][x] = "SCB";
//        map[y-1][x] = "SJU";
//    } else if(joinSeaDown && y < MAP_HEIGHT - 1){
//        map[y][x] = "SCT";
//        map[y+1][x] = "SJD";
//    }
////
//    if(joinSeaUpCornerLeft && y > 0){
//        map[y][x] = "SCB";
//        map[y-1][x] = "SBL";
//    }else if(joinSeaUpCornerRight && y > 0){
//        map[y][x] = "SCB";
//        map[y-1][x] = "SBR";
//    }
//
//    if(jSDP){
//        map[y][x+1] = "STR";
//        map[y][x] = "S3L";
//    }else if(jSDPR){
//        map[y][x-1] = "STL";
//        map[y][x] = "S3R";
//    }
//
//
//    if(joinUpperSea){
//        map[y][x+1] = "SCB";
//        map[y][x-1] = "SCB";
//        map[y][x] = "S2";
//
//    }
//
//    if(extendSeaDownLL){
//        map[y][x] = "SCT";
//        map[y+1][x] = "STL";
//
//    }
//
//
//
//
//
//}
