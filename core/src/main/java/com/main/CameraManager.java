package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraManager {
    private static CameraManager instance;


    private OrthographicCamera mapCamera;
    private Viewport mapViewport;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private static final float VIRTUAL_WIDTH = Gdx.graphics.getWidth();
    private static final float VIRTUAL_HEIGHT = Gdx.graphics.getHeight();

    private float zoomLevel = 1.0f;
    private final float MIN_ZOOM = 0.1f;
    private final float MAX_ZOOM = 10.0f;
    private final float ZOOM_SPEED = 0.1f;

    private boolean zoomingIn = false;
    private boolean zoomingOut = false;
    private final float ZOOM_KEY_SPEED = 10.0f;

    private boolean movingUp, movingDown, movingLeft, movingRight;
    private final float PAN_SPEED = 10f;
    private float movedX = 0f;
    private float movedY = 0f;

    private boolean sprinting = false; // New field to track sprinting

    private CameraManager() {
        mapCamera = new OrthographicCamera();
        mapViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, mapCamera);
        mapViewport.apply();
        mapCamera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        mapCamera.update();

        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);

        updateCameras();
    }

    public static CameraManager getInstance() {
        if (instance == null) {
            instance = new CameraManager();
        }
        return instance;
    }

    public OrthographicCamera getMapCamera() {
        return mapCamera;
    }


    public Viewport getMapViewport() {
        return mapViewport;
    }


    public OrthographicCamera getUiCamera() {
        return uiCamera;
    }

    public Viewport getUiViewport() {
        return uiViewport;
    }


    public void resize(int width, int height) {
        mapViewport.update(width, height);
        uiViewport.update(width, height, true);
        updateCameras();
    }

    private void updateCameras() {
        mapCamera.update();
        uiCamera.update();
    }

    public void handleCameraMovement() {
        float currentPanSpeed = sprinting ? PAN_SPEED * 2 : PAN_SPEED; // Double speed if sprinting

        if (movingLeft) movedX -= currentPanSpeed;
        if (movingRight) movedX += currentPanSpeed;
        if (movingDown) movedY -= currentPanSpeed;
        if (movingUp) movedY += currentPanSpeed;

        mapCamera.update();
    }

    public void updateCameraPosition() {
        // Calculate the camera's position
        float centerX = mapViewport.getWorldWidth() / 2f + movedX;
        float centerY = mapViewport.getWorldHeight() / 2f + movedY;

        // Set the camera's position and zoom
        mapCamera.position.set(centerX, centerY, 0);
        mapCamera.zoom = zoomLevel;
        mapCamera.update();
    }

    public void updateCameraZoom(float delta) {
        if (zoomingIn) zoomLevel = Math.max(MIN_ZOOM, zoomLevel - ZOOM_SPEED * delta * ZOOM_KEY_SPEED);
        if (zoomingOut) zoomLevel = Math.min(MAX_ZOOM, zoomLevel + ZOOM_SPEED * delta * ZOOM_KEY_SPEED);
    }

    public void scrolled(float amountX, float amountY) {
        // Adjust zoom level based on scroll input
        float zoomFactor = 1 + (amountY * ZOOM_SPEED);
        float newZoomLevel = zoomLevel * zoomFactor;

        // Clamp the zoom level to the allowed range
        zoomLevel = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, newZoomLevel));

        // Update the camera to reflect the new zoom level
        updateCameraPosition();
    }

    public float getZoomLevel() {
        return zoomLevel;
    }


    public float getZoomSpeed() {
        return ZOOM_SPEED;
    }

    public float getZOOM_KEY_SPEED() {
        return ZOOM_KEY_SPEED;
    }

    public float getMinZoomLevel() {
        return MIN_ZOOM;
    }

    public float getMaxZoomLevel() {
        return MAX_ZOOM;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, zoomLevel));
    }

    public boolean isZoomingIn() {
        return zoomingIn;
    }

    public void setZoomingIn(boolean zoomingIn) {
        this.zoomingIn = zoomingIn;
    }

    public boolean isZoomingOut() {
        return zoomingOut;
    }

    public void setZoomingOut(boolean zoomingOut) {
        this.zoomingOut = zoomingOut;
    }

    public boolean isMovingUp() {
        return movingUp;
    }

    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
    }

    public boolean isMovingDown() {
        return movingDown;
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public float getMovedX() {
        return movedX;
    }

    public void setMovedX(float movedX) {
        this.movedX = movedX;
    }

    public float getMovedY() {
        return movedY;
    }

    public void setMovedY(float movedY) {
        this.movedY = movedY;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return sprinting;
    }
}
