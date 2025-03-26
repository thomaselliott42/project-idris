package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Tile {

    private Unit unit;
    private Terrain terrain;
    private Building building;

    // stats
    private float tileHealth = 1.0f; // between 1 and 0
    private boolean IsWalkable;
    private float damageSwitchPercentage = 0.0f;
    private int damagedStateIndex = -1;
    private boolean fire= false;

    public Tile(Terrain terrain, Unit unit, Building building) {
        this.terrain = terrain;
        this.unit = unit;
        this.building = building;

        Gdx.app.log("Tile", "Created tile " + terrain.getId() + " " + unit + " " + building);
    }

    public void updateTerrain(Terrain terrain) {
        tileHealth = 1.0f;
        damagedStateIndex = -1;
        removeFire();
        this.terrain = terrain;

    }

    public float getTileHealth() {
        return tileHealth;
    }

    public void removeFire(){
        fire = false;
    }

    public void attachFire(){
        fire = true;
    }
    public boolean hasFire(){
        return fire;
    }

    public void updateTileHealth(float damage) {
        if(terrain.canBeDestroyed()){
            this.tileHealth -= damage;
            Gdx.app.log("Tile", "Damage : " + tileHealth);
            checkAndSwitchToDamagedState();
        }

    }

    public boolean isDestroyed(){
        return tileHealth <= 0.0f;
    }

    public void checkAndSwitchToDamagedState() {
        if (terrain.canBeDestroyed()) {

            damageSwitchPercentage = 1.0f / terrain.getDamagedTextureCount();
            Gdx.app.log("Tile", "Damage Switch Percentage: " + damageSwitchPercentage);

            damagedStateIndex = (int) ((1.0f - tileHealth) / damageSwitchPercentage)-1;
            damagedStateIndex = Math.min(damagedStateIndex, terrain.getDamagedTextureCount() - 1);

            if (tileHealth <= damageSwitchPercentage * (damagedStateIndex + 1)) {
                Gdx.app.log("Tile", "Tile damaged, switched to texture " + damagedStateIndex);
            }
        }

        if (tileHealth <= 0.0f) {
            Gdx.app.log("Tile", "Tile Destroyed");
            removeFire();
        }
    }

    public void updateUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Texture getTerrainTexture() {
        if(terrain.canBeDestroyed() && damagedStateIndex >= 0){
            return terrain.getDamagedTextures().get(damagedStateIndex);
        }else{
            return terrain.getTexture();
        }
    }
}
