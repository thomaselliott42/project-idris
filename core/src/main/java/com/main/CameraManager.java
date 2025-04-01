package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraManager {
    private static CameraManager instance;
    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 500;
    private final int MAP_HEIGHT = 500;
    private OrthographicCamera mapCamera;
    private Viewport mapViewport;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private static final float VIRTUAL_WIDTH = 1920;
    private static final float VIRTUAL_HEIGHT = 1080;

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
        if (movingLeft) movedX -= PAN_SPEED;
        if (movingRight) movedX += PAN_SPEED;
        if (movingDown) movedY -= PAN_SPEED;
        if (movingUp) movedY += PAN_SPEED;

        mapCamera.update();
    }


    public void updateCameraPosition() {
        float centerX = mapViewport.getWorldWidth() / 2f + movedX;
        float centerY = mapViewport.getWorldHeight() / 2f + movedY;

        mapCamera.position.set(centerX, centerY, 0);
        mapCamera.zoom = zoomLevel;
        mapCamera.update();
    }

    public void updateCameraZoom(float delta) {
        if (zoomingIn) zoomLevel = Math.max(MIN_ZOOM, zoomLevel - ZOOM_SPEED * delta * ZOOM_KEY_SPEED);
        if (zoomingOut) zoomLevel = Math.min(MAX_ZOOM, zoomLevel + ZOOM_SPEED * delta * ZOOM_KEY_SPEED);
    }

    public void scrolled(float amountX, float amountY) {
        float zoomFactor = 1 + (amountY * ZOOM_SPEED);
        zoomLevel *= zoomFactor;
        zoomLevel = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, zoomLevel));
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
}
