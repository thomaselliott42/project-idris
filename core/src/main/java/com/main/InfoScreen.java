package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class InfoScreen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont infoFont;

    public InfoScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        createFonts();

    }

    private void createFonts() {
        // Title font
        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/orangeJuice.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 72;
        titleParameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        titleFont = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();

        // Info text font
        FreeTypeFontGenerator infoGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/happySelfie.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter infoParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        infoParameter.size = 32;
        infoParameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        infoFont = infoGenerator.generateFont(infoParameter);
        infoGenerator.dispose();
    }

  public void render() {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    camera.update();

    // Draw semi-transparent black background
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(0, 0, 0, 0.8f); // Black with 80% transparency
    shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    shapeRenderer.end();
    shapeRenderer.dispose();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    // Draw title
    titleFont.draw(batch, "Info Screen",
        Gdx.graphics.getWidth() / 2 - 200,
        Gdx.graphics.getHeight() - 100);

    // Draw info text
    String[] infoLines = {
        "- Moving: use your WASD or arrow keys",
        "- Zooming In: press Z or scroll wheel",
        "- Zooming Out: press X or scroll wheel",
        "",
        "Press any key or click anywhere to return"
    };

    float yPos = Gdx.graphics.getHeight() - 200;
    for (String line : infoLines) {
        infoFont.draw(batch, line,
            Gdx.graphics.getWidth() / 2 - 200,
            yPos);
        yPos -= 50;
    }

    batch.end();

    Gdx.gl.glDisable(GL20.GL_BLEND);
}

    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        infoFont.dispose();

    }
}