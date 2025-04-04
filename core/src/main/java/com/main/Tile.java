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
    private String terrainBaseType = "P";
    private String faction =null;

    public Tile(Terrain terrain, Unit unit, Building building) {
        this.terrain = terrain;
        this.unit = unit;
        this.building = building;

        //Gdx.app.log("Tile", "Created tile " + terrain.getTextureId() + " " + unit + " " + building);
    }

    public int getFaction() {
        if(faction == null){
            return 0;
        } else{
            return 2;
        }
    }

    public void updateTerrain(Terrain terrain) {
        tileHealth = 1.0f;
        damagedStateIndex = -1;
        this.terrain = terrain;
        updateTerrainBaseType();

    }

    public void updateFaction(String faction) {
        this.faction = faction;
    }

    public void updateBuilding(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    public void removeBuilding() {
        this.building = null;
    }

    private void updateTerrainBaseType(){
        if(!terrain.getTextureId().contains("S")){
            if(terrain.getTextureId().contains("P")){
                terrainBaseType = "P";
            }else if(terrain.getTextureId().contains("D")){
                terrainBaseType = "D";
            }else if(terrain.getTextureId().contains("W")){
                terrainBaseType = "W";
            }
        }else{
            terrainBaseType = "S";
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

    public String getBaseTerrainId() {
        return terrainBaseType;
    }


    public void setTerrainBaseType(String base) {
        terrainBaseType = base;
    }

    public String getTerrainBaseType(){
        return terrainBaseType;
    }
    public String getTerrainId(){
        return terrain.getTextureId();
    }

//    public Texture getTerrainTexture() {
//        if(terrain.canBeDestroyed() && damagedStateIndex >= 0){
//            return terrain.getDamagedTextures().get(damagedStateIndex);
//        }else{
//            if(terrain.getTexture().size() == 1){
//                return terrain.getTexture().get(0);
//            }else{
//                if(terrainBaseType.equals("Plain")){
//                    return terrain.getTexture().get(0);
//                }else if(terrainBaseType.equals("Desert")){
//                    return terrain.getTexture().get(1);
//                }else if(terrainBaseType.equals("Winter") && terrain.getTexture().size() == 3){
//                    return terrain.getTexture().get(2);
//                }
//            }
//        }
//        return terrain.getTexture().get(0);
//
//    }
}
