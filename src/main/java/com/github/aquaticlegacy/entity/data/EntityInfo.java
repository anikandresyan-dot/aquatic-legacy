package com.github.aquaticlegacy.entity.data;

/**
 * Deserialized entity info from JSON data files.
 * Matches the Fossils & Archeology Revival entity_info JSON schema.
 */
public class EntityInfo {
    public AI ai;
    public Attributes attributes;
    public int adultAgeDays;
    public int teenAgeDays;
    public boolean breaksBlocks;
    public boolean canBeRidden;
    public String diet;
    public int maxHunger;
    public int maxPopulation;
    public float scaleBase;
    public float scaleMax;

    public static class AI {
        public String activity;
        public String attacking;
        public String climbing;
        public String moving;
        public String response;
        public String taming;
    }

    public static class Attributes {
        public double damageBase;
        public double damageMax;
        public double healthBase;
        public double healthMax;
        public double speedBase;
        public double speedMax;
        public double armorBase;
        public double armorMax;
        public double knockBackResistanceBase;
        public double knockBackResistanceMax;
    }
}
