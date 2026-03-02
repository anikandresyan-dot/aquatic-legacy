package com.github.aquaticlegacy.entity.prehistoric;

import com.github.aquaticlegacy.entity.ai.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

/**
 * Dunkleosteus — Armored Devonian fish.
 * Heavily armored with massive bite force. Territorial but won't chase far.
 * Medium-sized predator. Tameable via imprinting (babies).
 */
public class Dunkleosteus extends AquaticPrehistoric {

    public Dunkleosteus(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticBreathGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.6, true));
        this.goalSelector.addGoal(2, new AquaticFollowOwnerGoal(this, 1.2, 10.0f, 3.0f));
        this.goalSelector.addGoal(3, new AquaticStayGoal(this));
        this.goalSelector.addGoal(4, new AquaticEatGoal(this));
        this.goalSelector.addGoal(5, new AquaticMatingGoal(this, 1.0));
        this.goalSelector.addGoal(6, new AquaticSwimGoal(this));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AquaticHuntGoal(this, false));
    }

    @Override public String getEntityName() { return "dunkleosteus"; }
    @Override public int getMaxHunger() { return 100; }
    @Override public int getAdultAgeDays() { return 9; }
    @Override public int getTeenAgeDays() { return 4; }
    @Override public float getScaleBase() { return 0.3f; }
    @Override public float getScaleMax() { return 1.3f; }
    @Override public double getHealthBase() { return 12.0; }
    @Override public double getHealthMax() { return 40.0; }
    @Override public double getDamageBase() { return 2.0; }
    @Override public double getDamageMax() { return 20.0; }
    @Override public double getSpeedBase() { return 0.18; }
    @Override public double getSpeedMax() { return 0.3; }
    @Override public boolean canBeRidden() { return false; }
    @Override public boolean breaksBlocks() { return false; }
    @Override public String getDiet() { return "CARNIVORE"; }
    @Override public String getResponseType() { return "TERRITORIAL"; }
}
