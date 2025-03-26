package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.main.tooltips.Tooltip;
import com.main.tooltips.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialManager {

    private static TutorialManager instance;
    private Map<String, List<String>> tutorialSets; // Stores tutorials by ID
    private List<String> currentTutorialImages; // Active tutorial image list
    private int currentPage;
    private boolean active;
    private Texture currentTexture;
    private boolean allowNextPage; // True if more than one page
    private Texture whiteTexture;
    private boolean temp = false;
    private Batch batch;
    private float delayTimer = 0f;
    private boolean startDelay = false;

    private List<String> queue = new ArrayList<>();

    private boolean off;

    private TutorialManager() {
        Tooltip.getInstance().addTooltip("TUT","tut", "Exit Tutorial", "ui/toolTips/keyboard_key_escape.png", TooltipPosition.BOTTOM_RIGHT, false, false);

        tutorialSets = new HashMap<>();
        active = false;

        off = false; // Remember to reset

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
    }



    public void setBatch(Batch batch) {
        this.batch = batch;
    }


    public void setTemp() {
        temp = !temp;
    }

    public static TutorialManager getInstance() {
        if (instance == null) {
            instance = new TutorialManager();
        }
        return instance;
    }

    public void addToQueue(String id){
        queue.add(id);
    }

    public void setOff() {
        this.off = !this.off;
    }

    public boolean getOff(){
        return off;
    }


    public void registerTutorial(String id, List<String> imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) return;
        tutorialSets.put(id, imagePaths);
    }


    public void startTutorial(String id) {

        delayTimer = 1f;

        Gdx.app.log("TutorialManager", "Starting tutorial " + id);
        if (!tutorialSets.containsKey(id) || off) return;

        Tooltip.getInstance().setTutorialMode(true);
        currentTutorialImages = tutorialSets.get(id);
        currentPage = 0;
        active = true;
        allowNextPage = currentTutorialImages.size() > 1;

        loadCurrentImage();
        pauseGame();
        addTutorialTooltips();
    }

    private void loadCurrentImage() {
        if (currentTexture != null) {
            currentTexture.dispose(); // Free memory
        }
        currentTexture = new Texture(currentTutorialImages.get(currentPage));
    }


    public float getDelayTimer() {
        return delayTimer;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getMaxPage() {
        return currentTutorialImages.size() - 1;
    }

    public void stopTutorial() {
        if(!queue.isEmpty()){
            Gdx.app.log("TutorialManager", "Queue isn't empty");
            if (currentTexture != null) {
                currentTexture.dispose();
            }
            startTutorial(queue.remove(0));
        }else{
            startDelay = true;
            active = false;
            if(temp){
                setOff();
                setTemp();
            }
            Tooltip.getInstance().setTutorialMode(false);
            resumeGame();
            if (currentTexture != null) {
                currentTexture.dispose();
            }
        }

    }

    private void addTutorialTooltips() {
        if (allowNextPage) {
            Tooltip.getInstance().addTooltip("TUT","tut mp", "Next Page", "ui/toolTips/keyboard_key_d.png", TooltipPosition.BOTTOM_RIGHT, false, false);
            Tooltip.getInstance().addTooltip("TUT","tut mp", "< {} / {} > ",  TooltipPosition.BOTTOM, false, false, TooltipDynamic.PAGENUMBERT);
        }
    }

    private void pauseGame() {
        // Logic to pause the game (e.g., setting a game state)
    }

    private void resumeGame() {
        // Logic to resume the game
        Tooltip.getInstance().clear("tut mp");
    }

    public boolean isDelayTimer() {
        return startDelay;
    }

    public void update() {

        if(delayTimer > 0.01f && startDelay){
            delayTimer -= Gdx.graphics.getDeltaTime();
        }else if(delayTimer < 0.01f){
            Gdx.app.log("TutorialManager", "Delay timer reset and paused");
            delayTimer = 2f;
            startDelay = false;
        }

        if (!active) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stopTutorial();
        }

        if (allowNextPage && Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (currentPage < currentTutorialImages.size() - 1) {
                currentPage++;
                loadCurrentImage();
            } else {
                currentPage = 0;
                loadCurrentImage();
                loadCurrentImage();
            }
        }
    }


    public void render() {
        if (!active) return;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();


        // Draw dark transparent background

        batch.begin(); // Restart batch

        batch.setColor(0, 0, 0,0.9f); // Semi-transparent black
        batch.draw(whiteTexture,0,0, screenWidth, screenHeight);
        batch.setColor(1, 1, 1, 1); // Reset color to default


        // Draw the tutorial image centered
        // Get original image size
        float imageWidth = currentTexture.getWidth();
        float imageHeight = currentTexture.getHeight();

        // Calculate scale factor to fit screen
        float scaleX = screenWidth / imageWidth;
        float scaleY = screenHeight / imageHeight;
        float scale = Math.min(scaleX, scaleY); // Maintain aspect ratio

        // Calculate new width & height
        float newWidth = imageWidth * scale;
        float newHeight = imageHeight * scale;

        // Center the image
        float x = (screenWidth - newWidth) / 2;
        float y = (screenHeight - newHeight) / 2;

        // Draw scaled image
        batch.draw(currentTexture, x, y, newWidth, newHeight);

        batch.end();
    }

    public boolean isActive() {
        return active;
    }
}
