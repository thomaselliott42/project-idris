package com.main;

import com.badlogic.gdx.graphics.Texture;
import java.io.Serializable;
import java.util.List;

public class Terrain implements Serializable {
    private static final long serialVersionUID = 1L;

    private String textureId;
    private String terrainName;
    private boolean excludeTilePicker;
    private float defense;
    private float speed;
    private boolean canBeDestroyed;
    private transient List<Texture> damagedTextures;
    private transient List<Texture> joinigTextures;


    public Terrain(String id, String terrainName, boolean excludeTilePicker, float defense, float speed, boolean canBeDestroyed, List<Texture> damagedTextures, List<Texture> joinigTextures) {
        this.textureId = id;
        this.terrainName = terrainName;
        this.excludeTilePicker = excludeTilePicker;
        this.defense = defense;
        this.speed = speed;
        this.canBeDestroyed = canBeDestroyed;
        this.damagedTextures = damagedTextures;
        this.joinigTextures = joinigTextures;
    }

    public String getTextureId() {
        return textureId;
    }

    public boolean isExcludeTilePicker() {
        return excludeTilePicker;
    }

    public String getTerrainName() {
        return terrainName;
    }

    public float getDefense() {
        return defense;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean hasJoinigTextures() {
        return joinigTextures != null && !joinigTextures.isEmpty();
    }

    public List<Texture> getJoinigTextures() {
        return joinigTextures;
    }

    public boolean canBeDestroyed() {
        return canBeDestroyed;
    }

    public int getDamagedTextureCount() {
        return damagedTextures.size();
    }

    public List<Texture> getDamagedTextures() {
        return damagedTextures;
    }

    @Override
    public String toString() {
        return String.format("Terrain{id='%s', name='%s', defense=%.1f, speed=%.1f}",
            textureId, terrainName, defense, speed);
    }
}
