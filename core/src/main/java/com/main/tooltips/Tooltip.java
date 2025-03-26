package com.main.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.main.TutorialManager;

import java.util.HashMap;
import java.util.Map;

public class Tooltip {
    private static Tooltip instance;
    private final Array<TooltipEntry> tooltips = new Array<>();
    private final BitmapFont font;
    private final SpriteBatch batch;
    private boolean visible = true;
    private Texture whiteTexture;
    private boolean tutorialMode = false;
    String displayText = "";


    private Tooltip() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        batch = new SpriteBatch();

        // Create a 1x1 white texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void updateText(String id, String text){
        for (TooltipEntry entry : tooltips) {
            if (entry.id.equals(id)) {
                entry.text = text;
            }
        }
    }

    public void setVisible(String id, boolean visible) {
        for (TooltipEntry entry : tooltips) {
            if (entry.id.equals(id)) {
                entry.visible = visible;
            }
        }
    }


    public static Tooltip getInstance() {
        if (instance == null) {
            instance = new Tooltip();
        }
        return instance;
    }

    public void setTutorialMode(boolean tutorialMode){
        this.tutorialMode = tutorialMode;
    }

    public void addTooltip(String location, String id, String text, TooltipPosition position, boolean clear, boolean highlight) {
        tooltips.add(new TooltipEntry(location, id, text, null, position, clear, highlight, TooltipDynamic.DEFAULT));
    }

    public void addTooltip(String location, String id, String text, TooltipPosition position, boolean clear, boolean highlight, TooltipDynamic dynamicType) {
        tooltips.add(new TooltipEntry(location, id, text, null, position, clear, highlight, dynamicType));
    }

    // Add tooltip with text, image, and position
    public void addTooltip(String location, String id, String text, String imagePath, TooltipPosition position) {
        Texture image = new Texture(Gdx.files.internal(imagePath));
        tooltips.add(new TooltipEntry(location, id, text, image, position, true, false, TooltipDynamic.DEFAULT));
    }

    // Add tooltip with text, image, position, clear and highlight
    public void addTooltip(String location, String id, String text, String imagePath, TooltipPosition position, boolean clear, boolean highlight) {
        Texture image = new Texture(Gdx.files.internal(imagePath));
        tooltips.add(new TooltipEntry(location, id, text, image, position, clear, highlight, TooltipDynamic.DEFAULT));
    }

    public void clear() {
        for(TooltipEntry entry : tooltips) {
            if (entry.clear){
                entry.visible = false;
                //tooltips.removeValue(entry, true);
            }
        }
    }

    // Clears via id
    public void clear(String id) {
        for(TooltipEntry entry : tooltips) {
            if (entry.clear && entry.id.equals(id)) {
                tooltips.removeValue(entry, true);
            }
        }
    }

    public void setVisible(String id){
        for (TooltipEntry entry : tooltips) {
            if (entry.id.equals(id)) {
                entry.clear = false;
            }
        }
    }

    public static void resetInstance() {
        instance = null;
    }

    public void render(Camera uiCamera, float screenWidth, float screenHeight) {
        if (tooltips.isEmpty()) return;

        //float scale = GameState.getInstance().getTextScale();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // Store last used Y positions for each TooltipPosition
        Map<TooltipPosition, Float> lastYPositions = new HashMap<>();

        for (TooltipEntry entry : tooltips) {
            if (tutorialMode && (!entry.id.equals("tut") && !entry.id.equals("tut mp"))) continue;
            if (!tutorialMode && (entry.id.equals("tut") || entry.id.equals("tut mp"))) continue;
           // if (!tutorialMode && GameState.getInstance().getCurrentScreen().equals("MS") && !(entry.location.equals("MS") || entry.location.equals("A"))) continue;
           // if (!tutorialMode && GameState.getInstance().getCurrentScreen().equals("MC") && !(entry.location.equals("MC") || entry.location.equals("A") || entry.location.equals("AMC"))) continue;
           // if (!tutorialMode && GameState.getInstance().getCurrentScreen().equals("MCZ") && !(entry.location.equals("A") || entry.location.equals("AMC"))) continue;
           // if (!tutorialMode && GameState.getInstance().getCurrentScreen().equals("PTS") && !(entry.id.equals("EMC"))) continue;


            if (entry.visible) {
                // Apply text scaling
                //font.getData().setScale(scale);

                if (entry.dynamicType == TooltipDynamic.PAGENUMBERT) {
                    displayText = entry.text
                        .replaceFirst("\\{}", String.valueOf(TutorialManager.getInstance().getCurrentPage()))
                        .replaceFirst("\\{}", String.valueOf(TutorialManager.getInstance().getMaxPage()));
                } else {
                    displayText = entry.text;
                }

                GlyphLayout layout = new GlyphLayout(font, displayText);
                float textWidth = layout.width;
                float textHeight = layout.height;

                float x, y;

                // Determine the base position
                switch (entry.position) {
                    case BOTTOM:
                        x = screenWidth / 2 - textWidth / 2;
                        y = 20;
                        break;
                    case BOTTOM_RIGHT:
                        x = screenWidth - 50 - textWidth;
                        y = lastYPositions.getOrDefault(TooltipPosition.BOTTOM_RIGHT, 40f);
                        break;
                    case BOTTOM_LEFT:
                        x = 20;
                        y = lastYPositions.getOrDefault(TooltipPosition.BOTTOM_LEFT, 40f);
                        break;
                    case TOP_LEFT:
                        x = 20;
                        y = lastYPositions.getOrDefault(TooltipPosition.TOP_LEFT, screenHeight - 20);
                        break;
                    case TOP_RIGHT:
                        x = screenWidth - 20 - textWidth;
                        y = lastYPositions.getOrDefault(TooltipPosition.TOP_RIGHT, screenHeight - 20);
                        break;
                    case CENTER:
                        x = screenWidth / 2 - textWidth / 2;
                        y = screenHeight / 2;
                        break;
                    case CLICK_ROLL:
                        x = screenWidth / 2 - textWidth / 2;
                        y = screenHeight / 2 - 100;
                        break;
                    default:
                        continue;
                }

                if (entry.highlight) {
                    batch.setColor(0, 0, 0, 0.5f);
                    batch.draw(whiteTexture, x, y - textHeight, textWidth, textHeight);
                    batch.setColor(1, 1, 1, 1);
                }

                font.draw(batch, displayText, x, y);

                if (entry.image != null) {
                    batch.draw(entry.image, x + textWidth, y - textHeight - 10, 32, 32);
                }

                lastYPositions.put(entry.position, y + textHeight + 20);

                // Reset scale after drawing each tooltip (optional if font is reused elsewhere)
                font.getData().setScale(1f);
            }
        }

        batch.end();
    }


    public void setVisible() {
        this.visible = !this.visible;
    }

    public boolean isVisible() {
        return visible;
    }



    private static class TooltipEntry {
        String location; // A : ALL , MC : MAKERS CENTER , MS : main screen
        String id;
        String text;
        Texture image;
        TooltipPosition position;
        TooltipDynamic dynamicType;
        boolean clear;
        boolean highlight;
        boolean visible = true;

        TooltipEntry(String location, String id, String text, Texture image, TooltipPosition position, Boolean clear, boolean highlight, TooltipDynamic dynamicType) {
            this.location = location;
            this.id = id;
            this.text = text;
            this.image = image;
            this.position = position;
            this.dynamicType = dynamicType;
            this.clear = clear;
            this.highlight = highlight;
        }
    }
}
