package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SplashScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private Texture logoTexture;
    private Texture lineTexture;
    private float alpha = 0f;
    private float songDuration = 5.35f; // Replace with your MP3’s length in seconds



    private float timer = 0f;
    private float revealDuration = 1.5f; // Time for the line to expand
    private float fadeInDuration = 1f;
    private float totalDuration = 5.32f; // Total time before transitioning
    private Music splashSound;


    private float lineWidth = 1f;
    private float lineHeight = 8f;

    private float barHeight = 0f;
    private float maxBarHeight = 120f; // You can tweak this
    private float titleAlpha = 0f;

    private boolean showTitle = false;
    private BitmapFont titleFont;


    public SplashScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/radarIndex.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48; // Set the font size you want
        titleFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose after use


        batch = new SpriteBatch();
        logoTexture = new Texture(Gdx.files.internal("loadingScreen/gameLogo.png"));
        lineTexture = new Texture(Gdx.files.internal("lightLine.png"));
        splashSound = Gdx.audio.newMusic(Gdx.files.internal("audio/logoSplashScreen.mp3"));
        splashSound.setVolume(1f);
        splashSound.play();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        timer += delta;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;

        // === Line expansion: reach edge in 3 seconds ===
        float expandDuration = 3f;
        float progress = Math.min(timer / expandDuration, 1f);
        lineWidth = progress * screenWidth;

        if (timer >= expandDuration && !showTitle) {
            showTitle = true;
        }

        // === Letterbox bars slide in starting slightly before the title ===
        if (timer >= 2.5f && barHeight < maxBarHeight) {
            barHeight += delta * 100f; // Speed of sliding bars
            barHeight = Math.min(barHeight, maxBarHeight);
        }

        // === Title fade-in ===
        if (showTitle) {
            titleAlpha += delta;
            titleAlpha = Math.min(titleAlpha, 1f);
        }

        batch.begin();

        // Draw line
        batch.draw(
            lineTexture,
            centerX - lineWidth / 2f,
            centerY - lineHeight / 2f,
            lineWidth,
            lineHeight
        );

        // Draw "Project Idris" title
        if (showTitle) {
            Color original = batch.getColor();
            batch.setColor(1f, 1f, 1f, titleAlpha);
            GlyphLayout layout = new GlyphLayout(titleFont, "PROJECT IDRIS");
            titleFont.draw(batch, layout, centerX - layout.width / 2f, centerY + 100f);
            batch.setColor(original);
        }

        batch.end();

        // === Draw letterboxing bars ===
        ShapeRenderer shape = new ShapeRenderer();
        shape.setProjectionMatrix(batch.getProjectionMatrix());
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        shape.rect(0, screenHeight - barHeight, screenWidth, barHeight); // Top bar
        shape.rect(0, 0, screenWidth, barHeight);                        // Bottom bar
        shape.end();

        // Transition after the song ends
        if (timer >= songDuration) {
            splashSound.stop();
            game.setScreen(new MainMenu(game));
        }
    }


    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        logoTexture.dispose();
        lineTexture.dispose();
        splashSound.dispose();
    }
}

