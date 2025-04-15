package com.main.tools;


import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AtlasPacker {
    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;   // Set atlas max width
        settings.maxHeight = 2048;  // Set atlas max height
        settings.duplicatePadding = true; // Prevents edge bleeding issues

        // Input folder: "assets/terrain"
        // Output folder: "assets/atlas"
        // Atlas name: "terrain"
        TexturePacker.process(settings, "assets/environment/animals", "assets/environment", "seagull");
    }
}
