package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;

public class InfoScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont infoFont;

    public InfoScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        createFonts();
    }

    private void createFonts() {
        // Title font
        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/radarIndex.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 72;
        titleParameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        titleFont = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();

        // Info text font
        FreeTypeFontGenerator infoGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/chocoWinter.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter infoParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        infoParameter.size = 32;
        infoParameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        infoFont = infoGenerator.generateFont(infoParameter);
        infoGenerator.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Draw title
        titleFont.draw(batch, "Info",
            Gdx.graphics.getWidth() / 2 - 100,
            Gdx.graphics.getHeight() - 100);

        // Draw info text
        String[] infoLines = {
            "",
            "",
            "- Moving: use your WASD or arrow keys",
            "- Zooming In: press Z or scroll wheel",
            "- Zooming Out: press X or scroll wheel",
            "",
            "Click anywhere to return"
        };

        float yPos = Gdx.graphics.getHeight() - 200;
        for (String line : infoLines) {
            infoFont.draw(batch, line,
                Gdx.graphics.getWidth() / 2 - 200,
                yPos);
            yPos -= 50;
        }

        batch.end();

        // Return to map maker on any click
        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            game.setScreen(new MapMaker(game)); // Pass game instance to MapMaker
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        infoFont.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
