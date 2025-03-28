package com.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.List;


//worked on by Josh
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
    private String terrainBaseType = "Plain";

    public Tile(Terrain terrain, Unit unit, Building building) {
        this.terrain = terrain;
        this.unit = unit;
        this.building = building;

        Gdx.app.log("Tile", "Created tile " + terrain.getId() + " " + unit + " " + building);
    }

    public void updateTerrain(Terrain terrain) {
        tileHealth = 1.0f;
        damagedStateIndex = -1;
        this.terrain = terrain;
        updateTerrainBaseType();

    }

    private void updateTerrainBaseType(){
        if(!terrain.getId().contains("S")){
            if(terrain.getId().contains("P")){
                terrainBaseType = "Plain";
            }else if(terrain.getId().contains("D")){
                terrainBaseType = "Desert";
            }
        }
    }

    public float getTileHealth() {
        return tileHealth;
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

    public String getBaseType() {
        return terrainBaseType;
    }

    public Texture getBaseTerrainTexture(){
        if(terrainBaseType.equals("Plain")){
            return TerrainManager.getInstance().getTerrain("P").getTexture().get(0);
        }else if(terrainBaseType.equals("Desert")){
            return TerrainManager.getInstance().getTerrain("D").getTexture().get(0);
        }
        return null;
    }

    public void setTerrainBaseType(String base) {
        terrainBaseType = base;
    }

    public String getTerrainBaseType(){
        return terrainBaseType;
    }
    public String getTerrainId(){
        return terrain.getId();
    }

    public Texture getTerrainTexture() {
        if(terrain.canBeDestroyed() && damagedStateIndex >= 0){
            return terrain.getDamagedTextures().get(damagedStateIndex);
        }else{
            if(terrain.getTexture().size() == 1){
                return terrain.getTexture().get(0);
            }else{
                if(terrainBaseType.equals("Plain")){
                    return terrain.getTexture().get(0);
                }else if(terrainBaseType.equals("Desert")){
                    return terrain.getTexture().get(1);
                }else if(terrainBaseType.equals("Winter")){
                    return terrain.getTexture().get(2);
                }
            }
        }
        return null;
    }
}
