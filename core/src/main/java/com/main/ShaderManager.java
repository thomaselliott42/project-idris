package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static ShaderManager instance;  // Singleton instance
    private final Map<String, ShaderProgram> shaders = new HashMap<>();
    private ShaderProgram currentShader;

    // Private constructor to prevent instantiation
    private ShaderManager() {}

    // Get the singleton instance
    public static ShaderManager getInstance() {
        if (instance == null) {
            instance = new ShaderManager();
        }
        return instance;
    }

    // Load and store a shader with a key
    public void loadShader(String key, String vertexPath, String fragmentPath) {
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));

        if (!shader.isCompiled()) {
            Gdx.app.error("ShaderManager", "Shader Compilation Failed (" + key + "):\n" + shader.getLog());
        } else {
            shaders.put(key, shader);
            Gdx.app.log("ShaderManager", "Shader loaded successfully: " + key);
        }
    }

    // Set the current active shader by key
    public void useShader(String key) {
        ShaderProgram shader = shaders.get(key);
        if (shader != null) {
            currentShader = shader;
        } else {
            Gdx.app.error("ShaderManager", "Shader not found: " + key);
        }
    }

    // Get the currently active shader
    public ShaderProgram getCurrentShader() {
        return currentShader;
    }

    // Set a uniform float value for the current shader
    public void setUniformf(String name, float value) {
        if (currentShader != null) {
            currentShader.setUniformf(name, value);
        }
    }

    // Set a uniform vector (2D)
    public void setUniformf(String name, float x, float y) {
        if (currentShader != null) {
            currentShader.setUniformf(name, x, y);
        }
    }

    // Set a uniform vector (4D, for colors)
    public void setUniformf(String name, float r, float g, float b, float a) {
        if (currentShader != null) {
            currentShader.setUniformf(name, r, g, b, a);
        }
    }

    // Set an integer uniform
    public void setUniformi(String name, int value) {
        if (currentShader != null) {
            currentShader.setUniformi(name, value);
        }
    }

    // Dispose all shaders when no longer needed
    public void dispose() {
        for (ShaderProgram shader : shaders.values()) {
            shader.dispose();
        }
        shaders.clear();
    }
}
