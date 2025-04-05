package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;

public class InfoScreen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont infoFont;
    private ShapeRenderer shapeRenderer;

    private Texture leftArrowKey;
    private Texture rightArrowKey;
    private Texture escapeKey;

    // Movement key textures
    private Texture wKey;
    private Texture aKey;
    private Texture sKey;
    private Texture dKey;
    private Texture zKey;
    private Texture xKey;
    private Texture ctrlZKey;


    // Tool icons
    private TextureRegion grabToolIcon;
    private TextureRegion damageToolIcon;
    private TextureRegion fillToolIcon;
    private Texture infoScreenIcon;

    // Palette icon
    private TextureRegion firstTerrainIcon;

    private int currentScreen = 1; // Tracks the current screen (1-based index)
    private final int totalScreens = 3; // Total number of screens

    public InfoScreen(TextureRegion grabToolIcon, TextureRegion damageToolIcon, TextureRegion fillToolIcon, TextureRegion firstTerrainIcon) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        createFonts();

        // Load arrow key textures
        leftArrowKey = new Texture(Gdx.files.internal("keyboardKeys/leftArrowKey.png"));
        rightArrowKey = new Texture(Gdx.files.internal("keyboardKeys/rightArrowKey.png"));
        escapeKey = new Texture(Gdx.files.internal("keyboardKeys/escapeKey.png"));

        // Load movement key textures
        wKey = new Texture(Gdx.files.internal("keyboardKeys/wKey.png"));
        aKey = new Texture(Gdx.files.internal("keyboardKeys/aKey.png"));
        sKey = new Texture(Gdx.files.internal("keyboardKeys/sKey.png"));
        dKey = new Texture(Gdx.files.internal("keyboardKeys/dKey.png"));
        zKey = new Texture(Gdx.files.internal("keyboardKeys/zKey.png"));
        xKey = new Texture(Gdx.files.internal("keyboardKeys/xKey.png"));
        ctrlZKey = new Texture(Gdx.files.internal("keyboardKeys/ctrlZKey.png"));

        // load info screen icon
        // Load the InfoScreen icon texture
        infoScreenIcon = new Texture(Gdx.files.internal("ui/infoIcon.png"));

        // Assign tool icons
        this.grabToolIcon = grabToolIcon;
        this.damageToolIcon = damageToolIcon;
        this.fillToolIcon = fillToolIcon;

        // Assign the first terrain icon
        this.firstTerrainIcon = firstTerrainIcon;
    }

    private void createFonts() {
        int screenHeight = Gdx.graphics.getHeight();

        // Title font
        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/friendInfo.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = screenHeight / 15; // Scale title font size
        titleParameter.color = Color.WHITE;
        titleFont = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();

        // Info text font
        FreeTypeFontGenerator infoGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/happySelfie.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter infoParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        infoParameter.size = screenHeight / 30; // Scale info font size
        infoParameter.color = Color.WHITE;
        infoFont = infoGenerator.generateFont(infoParameter);
        infoGenerator.dispose();
    }

    public void render() {
        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();

        // Draw the background with semi-transparency
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f)); // Semi-transparent black
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // Disable blending after rendering the background
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw the title and content
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Determine the title based on the current screen
        String title = "";
        switch (currentScreen) {
            case 1:
                title = "Movement";
                break;
            case 2:
                title = "Tools";
                break;
            case 3:
                title = "Palette";
                break;
        }

        // Draw the title at the top center
        float titleX = (Gdx.graphics.getWidth() - titleFont.getRegion().getRegionWidth()) / 2f;
        float titleY = Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 8f;
        titleFont.draw(batch, title, titleX, titleY);

        // Draw the screen number (X/N) next to the title
        String screenNumber = currentScreen + "/" + totalScreens;
        float screenNumberX = titleX + titleFont.getRegion().getRegionWidth() + 20; // Add spacing after the title
        infoFont.draw(batch, screenNumber, screenNumberX, titleY);

        // Draw the content for the current screen in the center
        float contentY = Gdx.graphics.getHeight() / 2f + infoFont.getLineHeight() * 2; // Start slightly above the center
        switch (currentScreen) {
            case 1:
                // Movement screen with PNGs and explanations
                contentY = drawKeyWithText(batch, wKey, "Move up", contentY);
                contentY = drawKeyWithText(batch, aKey, "Move left", contentY);
                contentY = drawKeyWithText(batch, sKey, "Move down", contentY);
                contentY = drawKeyWithText(batch, dKey, "Move right", contentY);
                contentY = drawKeyWithText(batch, zKey, "Zoom in (scroll up)", contentY);
                contentY = drawKeyWithText(batch, xKey, "Zoom out (scroll down)", contentY);
                break;
            case 2:
                // Tools screen with PNGs and explanations
                contentY = drawKeyWithText(batch, grabToolIcon, "Grab Tool: Move the map", contentY);
                contentY = drawKeyWithText(batch, damageToolIcon, "Damage Tool: Apply damage to tiles", contentY);
                contentY = drawKeyWithText(batch, fillToolIcon, "Fill Tool: Fill the map with the selected terrain", contentY);
                contentY = drawCombinedKeysWithText(batch, infoScreenIcon, ctrlZKey, "Undo: Revert the last action", contentY); 
                break;
            case 3:
                // Palette screen with the first terrain icon and explanation
                if (firstTerrainIcon != null) {
                    contentY = drawKeyWithText(batch, firstTerrainIcon, "This is where you can select different tiles to place", contentY);
                } else {
                    // Fallback if the texture is missing
                    contentY = drawCenteredText(batch, infoFont, "Terrains: Select different terrains", contentY);
                }
                break;
        }

        // Draw the left arrow key indicator at the bottom-left corner
        float arrowSize = 64; // Size of the arrow key icons
        float leftArrowX = 20; // Padding from the left edge
        float leftArrowY = 20; // Padding from the bottom edge
        batch.draw(leftArrowKey, leftArrowX, leftArrowY, arrowSize, arrowSize);

        // Draw the right arrow key indicator at the bottom-right corner
        float rightArrowX = Gdx.graphics.getWidth() - arrowSize - 20; // Padding from the right edge
        float rightArrowY = 20; // Padding from the bottom edge
        batch.draw(rightArrowKey, rightArrowX, rightArrowY, arrowSize, arrowSize);

        // Draw the escape key indicator at the top-left corner
        float escapeKeySize = 64; // Size of the escape key icon
        float escapeKeyX = 20; // Padding from the left edge
        float escapeKeyY = Gdx.graphics.getHeight() - escapeKeySize - 20; // Padding from the top edge
        batch.draw(escapeKey, escapeKeyX, escapeKeyY, escapeKeySize, escapeKeySize);

        // Draw the "Press ESC to exit" text next to the escape key
        String escapeText = "Press ESC to exit";
        float escapeTextX = escapeKeyX + escapeKeySize + 10; // Position to the right of the escape key
        float escapeTextY = escapeKeyY + escapeKeySize / 2f + infoFont.getLineHeight() / 2f; // Vertically center the text with the icon
        infoFont.draw(batch, escapeText, escapeTextX, escapeTextY);

        batch.end();

        // Handle input for switching screens
        handleInput();
    }

    private float drawCombinedKeysWithText(SpriteBatch batch, Texture key1, Texture key2, String text, float y) {
        float keySize = 64; // Size of each key icon
        float spacing = 10; // Spacing between the keys
        float keyX = Gdx.graphics.getWidth() / 4f; // Position the first key on the left
        float textX = keyX + keySize * 2 + spacing + 10; // Position the text to the right of the keys
    
        // Draw the first key texture (InfoScreen icon)
        batch.draw(key1, keyX, y - keySize, keySize, keySize);
    
        // Draw the second key texture (Ctrl+Z key) next to the first
        batch.draw(key2, keyX + keySize + spacing, y - keySize, keySize, keySize);
    
        // Draw the text
        infoFont.draw(batch, text, textX, y - keySize / 2f);
    
        return y - keySize - 20; // Move down for the next line
    }

    private float drawKeyWithText(SpriteBatch batch, Object keyTexture, String text, float y) {
        float keySize = 64; // Size of the key icon
        float keyX = Gdx.graphics.getWidth() / 4f; // Position the key on the left
        float textX = keyX + keySize + 10; // Position the text to the right of the key
    
        // Draw the key texture
        if (keyTexture instanceof TextureRegion) {
            batch.draw((TextureRegion) keyTexture, keyX, y - keySize, keySize, keySize);
        } else if (keyTexture instanceof Texture) {
            batch.draw((Texture) keyTexture, keyX, y - keySize, keySize, keySize);
        }
    
        // Draw the text
        infoFont.draw(batch, text, textX, y - keySize / 2f);
    
        return y - keySize - 20; // Move down for the next line
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            currentScreen = (currentScreen - 1 < 1) ? totalScreens : currentScreen - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            currentScreen = (currentScreen + 1 > totalScreens) ? 1 : currentScreen + 1;
        }
    }

    private float drawCenteredText(SpriteBatch batch, BitmapFont font, String text, float y) {
        float textWidth = font.getRegion().getRegionWidth();
        float x = (Gdx.graphics.getWidth() - textWidth) / 2f; // Center horizontally
        font.draw(batch, text, x, y);
        return y - font.getLineHeight() - 10; // Move down for the next line
    }

    public boolean shouldHide() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    }

    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        infoFont.dispose();
        shapeRenderer.dispose();
        leftArrowKey.dispose();
        rightArrowKey.dispose();
        escapeKey.dispose();
        wKey.dispose();
        aKey.dispose();
        sKey.dispose();
        dKey.dispose();
        zKey.dispose();
        xKey.dispose();
        ctrlZKey.dispose();
        infoScreenIcon.dispose();
    }
}