package gui;

import java.io.File;

public class CharacterDetail {
    private String characterName;
    private double initiative;
    private int initiativeBonus, armorClass, hitPoints;
    private final File FILE;

    CharacterDetail(String characterName, double initiative, int initiativeBonus, int armorClass, int hitPoints, File file) {
        this.characterName = characterName;
        this.initiative = initiative;
        this.initiativeBonus = initiativeBonus;
        this.armorClass = armorClass;
        this.hitPoints = hitPoints;
        FILE = file;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public double getInitiative() {
        return initiative;
    }

    public void setInitiative(double initiative) {
        this.initiative = initiative;
    }

    public int getInitiativeBonus() {
        return initiativeBonus;
    }

    public void setInitiativeBonus(int initiativeBonus) {
        this.initiativeBonus = initiativeBonus;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public void setArmorClass(int armorClass) {
        this.armorClass = armorClass;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public String toString() {
        return characterName + ","
                + initiative + ","
                + initiativeBonus + ","
                + armorClass + ","
                + hitPoints + ","
                + FILE.getAbsolutePath();
    }
}