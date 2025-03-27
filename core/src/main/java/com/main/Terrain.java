package com.main;

import com.badlogic.gdx.graphics.Texture;
import java.io.Serializable;
import java.util.List;

public class Terrain implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String terrainName;
    private boolean excludeTilePicker;
    private transient List<Texture> texture; // Marked as transient since Texture isn't serializable
    private float defense;
    private float speed;
    private boolean canBeDestroyed;
    private transient List<Texture> damagedTextures;

    public Terrain(String id, String terrainName, boolean excludeTilePicker, List<Texture> texture, float defense, float speed, boolean canBeDestroyed, List<Texture> damagedTextures) {
        this.id = id;
        this.terrainName = terrainName;
        this.excludeTilePicker = excludeTilePicker;
        this.texture = texture;
        this.defense = defense;
        this.speed = speed;
        this.canBeDestroyed = canBeDestroyed;
        this.damagedTextures = damagedTextures;
    }

    public String getId() {
        return id;
    }

    public boolean isExcludeTilePicker() {
        return excludeTilePicker;
    }

    public String getTerrainName() {
        return terrainName;
    }

    public List<Texture> getTexture() {
        return texture;
    }


    public float getDefense() {
        return defense;
    }

    public float getSpeed() {
        return speed;
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
            id, terrainName, defense, speed);
    }
}
