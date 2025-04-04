package com.main;

public class Capital extends Building {
    private String faction;

    // Constructor
    public Capital(String name, String textureId, float defense, float speed, boolean canHeal, boolean canRefuel, boolean canBeDestroyed, String faction) {
        super(name, textureId, defense, speed, canHeal, canRefuel, canBeDestroyed); // Call the parent constructor
        this.faction = faction;
    }

    // Getter
    public String getFaction() {
        return faction;
    }

    // Setter
    public void setFaction(String faction) {
        this.faction = faction;
    }

    // Method to display info, including faction
    @Override
    public void displayInfo() {
        super.displayInfo(); // Call the parent class method
        System.out.println("Faction: " + faction);
    }
}
