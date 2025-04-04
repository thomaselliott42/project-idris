package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.TimeUtils;

public class shaderExample implements Screen {
    private SpriteBatch batch;
    private Texture texture;
    private ShaderProgram shader;
    private Sprite sprite;
    private int ownership = 1; // 0 = Unowned, 1 = Red, 2 = Blue
    private float time;

    private final Main game;

    public shaderExample(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Initialize resources
        batch = new SpriteBatch();
        texture = new Texture("sea/sea.png");

        // Load the shader program
        shader = new ShaderProgram(Gdx.files.internal("shaders/buildings/default.vert"), Gdx.files.internal("shaders/buildings/colourChange.frag"));

        // Check if shader compiled successfully
        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + shader.getLog());
        }

        // Create the sprite
        sprite = new Sprite(texture);
        sprite.scale(4f);
        sprite.setPosition(100, 100);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        time += delta; // delta is the time passed since the last frame
        batch.begin();
        batch.setShader(shader);

        // Set shader uniforms for immediate ownership-based tinting
        //shader.setUniformi("u_ownership", ownership);
        shader.setUniformf("u_time", time);

        if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
            ownership++;

            if(ownership > 2){
                ownership = 0;
            }
        }



        // Directly set the tint color based on ownership
//        if (ownership == 1) {
//            shader.setUniformf("u_tintColor", 1.0f, 0.0f, 0.0f, 0.8f); // Red for Red Ownership
//        } else if (ownership == 2) {
//            shader.setUniformf("u_tintColor", 0.0f, 0.0f, 1.0f, 1.0f); // Blue for Blue Ownership
//        }
//        else {
//            shader.setUniformf("u_tintColor", 1.0f, 1.0f, 1.0f, 1.0f); // No tint (White) for Unowned
//        }

        sprite.draw(batch); // Draw the sprite with the applied shader

        batch.setShader(null); // Reset the shader
        batch.end();
    }

    @Override
    public void hide() {
        // Dispose of resources when the screen is hidden
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        shader.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // Handle screen resizing (if necessary)
    }

    @Override
    public void pause() {
        // Handle pause logic
    }

    @Override
    public void resume() {
        // Handle resume logic
    }


}
