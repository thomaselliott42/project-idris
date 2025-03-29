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

public class LoadingScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont loadingFont;

    private Texture gameLogo;
    private Texture loadingBarBg;
    private Texture loadingBarFg;
    private Rectangle loadingBarBounds;

    private float progress = 0;
    private final float LOAD_TIME = 3f;

    public LoadingScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        loadAssets();
        createFonts();

        gameLogo = new Texture("loadingScreen/gameLogo.png");
        loadingBarBg = new Texture("loadingScreen/loading_bar_bg.png");
        loadingBarFg = new Texture("loadingScreen/loading_bar_fg.png");
        loadingBarBounds = new Rectangle(
            Gdx.graphics.getWidth() / 2 - 200,
            100,
            400,
            30
        );
    }

    private void loadAssets() {
        TerrainLoader.loadTerrains();
    }

    private void createFonts() {
        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/radarIndex.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 100;
        titleParameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        titleFont = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();

        FreeTypeFontGenerator loadingGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/chocoWinter.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter loadingParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        loadingParameter.size = 24;
        loadingParameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        loadingFont = loadingGenerator.generateFont(loadingParameter);
        loadingGenerator.dispose();
    }

    @Override
    public void render(float delta) {
        progress += delta / LOAD_TIME;
        if (progress >= 1) {
            progress = 1;
            game.setScreen(new MapMaker(game)); // Pass game instance to MapMaker
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Calculate positions
        float titleY = Gdx.graphics.getHeight() - 100;
        float loadingBarY = loadingBarBounds.y;
        float availableHeight = titleY - 150 - loadingBarY; // Space between title and loading bar

        // Draw title
        titleFont.draw(batch, "IDRIS",
            Gdx.graphics.getWidth() / 2 - 100,
            titleY);

        // Draw logo (scaled to fit available space)
        float logoAspectRatio = (float)gameLogo.getWidth() / gameLogo.getHeight();
        float maxLogoWidth = Gdx.graphics.getWidth() * 0.8f; // Max 80% of screen width
        float maxLogoHeight = availableHeight * 0.8f; // Max 80% of available height

        float logoWidth = Math.min(maxLogoWidth, maxLogoHeight * logoAspectRatio);
        float logoHeight = logoWidth / logoAspectRatio;

        batch.draw(gameLogo,
            Gdx.graphics.getWidth() / 2 - logoWidth / 2,
            loadingBarY + (availableHeight - logoHeight)/2 + 50, // Centered vertically
            logoWidth,
            logoHeight);

        // Draw loading text
        loadingFont.draw(batch, "Loading...",
            Gdx.graphics.getWidth() / 2 - 50,
            loadingBarY - 30);

        // Draw loading bars
        batch.draw(loadingBarBg,
            loadingBarBounds.x, loadingBarBounds.y,
            loadingBarBounds.width, loadingBarBounds.height);

        batch.draw(loadingBarFg,
            loadingBarBounds.x, loadingBarBounds.y,
            loadingBarBounds.width * progress, loadingBarBounds.height);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        loadingFont.dispose();
        gameLogo.dispose();
        loadingBarBg.dispose();
        loadingBarFg.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
