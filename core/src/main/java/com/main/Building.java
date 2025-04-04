package com.main;

import com.badlogic.gdx.graphics.Texture;
import java.util.List;

public class Building {
    private String textureId;
    private String name;
    private float defense;
    private float speed;
    private boolean canHeal;
    private boolean canRefuel;
    private boolean canBeDestroyed;
    private transient List<Texture> damagedTextures;

    // Constructor
    public Building(String name, String textureId, float defense, float speed, boolean canHeal, boolean canRefuel, boolean canBeDestroyed) {
        this.name = name;
        this.textureId = textureId;
        this.defense = defense;
        this.speed = speed;
        this.canHeal = canHeal;
        this.canRefuel = canRefuel;
        this.canBeDestroyed = canBeDestroyed;
    }

    // Getters
    public String getTextureId() {
        return textureId;
    }

    public String getName() {
        return name;
    }

    public float getDefense() {
        return defense;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean canHeal() {
        return canHeal;
    }

    public boolean canRefuel() {
        return canRefuel;
    }

    public boolean canBeDestroyed() {
        return canBeDestroyed;
    }

    // Setters
    public void setTextureId(String textureId) {
        this.textureId = textureId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefense(float defense) {
        this.defense = defense;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setCanHeal(boolean canHeal) {
        this.canHeal = canHeal;
    }

    public void setCanRefuel(boolean canRefuel) {
        this.canRefuel = canRefuel;
    }

    public void setCanBeDestroyed(boolean canBeDestroyed) {
        this.canBeDestroyed = canBeDestroyed;
    }

    // Method to display info
    public void displayInfo() {
        System.out.println("Building Name: " + name);
        System.out.println("Texture ID: " + textureId);
        System.out.println("Defense: " + defense);
        System.out.println("Speed: " + speed);
        System.out.println("Can Heal: " + canHeal);
        System.out.println("Can Refuel: " + canRefuel);
        System.out.println("Can Be Destroyed: " + canBeDestroyed);
    }

    // Example method for when the building takes damage and updates its damaged textures
    public void takeDamage() {
        if (damagedTextures != null && !damagedTextures.isEmpty()) {
            // Update to show damaged textures (logic can vary)
            System.out.println("Building is damaged! Using damaged textures.");
        }
    }

    // Example method to heal the building (only if it can heal)
    public void heal() {
        if (canHeal) {
            System.out.println("Building is healing...");
            // Add healing logic here
        } else {
            System.out.println("Building cannot heal.");
        }
    }

    // Example method to refuel the building (only if it can refuel)
    public void refuel() {
        if (canRefuel) {
            System.out.println("Building is refueling...");
            // Add refueling logic here
        } else {
            System.out.println("Building cannot refuel.");
        }
    }
}
