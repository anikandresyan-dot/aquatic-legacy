package com.github.aquaticlegacy.entity.prehistoric;

import com.github.aquaticlegacy.entity.ai.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

/**
 * Shonisaurus — Giant Triassic ichthyosaur, one of the largest marine reptiles.
 * Gentle giant. Tameable and rideable when adult. Eats fish in large quantities.
 * Very high HP and knock-back resistance.
 */
public class Shonisaurus extends AquaticPrehistoric {

    public Shonisaurus(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticBreathGoal(this));
        this.goalSelector.addGoal(1, new AquaticPanicGoal(this, 1.3));
        this.goalSelector.addGoal(2, new AquaticFollowOwnerGoal(this, 1.0, 12.0f, 4.0f));
        this.goalSelector.addGoal(3, new AquaticStayGoal(this));
        this.goalSelector.addGoal(4, new AquaticEatGoal(this));
        this.goalSelector.addGoal(5, new AquaticMatingGoal(this, 0.8));
        this.goalSelector.addGoal(6, new AquaticSwimGoal(this));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override public String getEntityName() { return "shonisaurus"; }
    @Override public int getMaxHunger() { return 200; }
    @Override public int getAdultAgeDays() { return 14; }
    @Override public int getTeenAgeDays() { return 6; }
    @Override public float getScaleBase() { return 0.5f; }
    @Override public float getScaleMax() { return 2.5f; }
    @Override public double getHealthBase() { return 20.0; }
    @Override public double getHealthMax() { return 80.0; }
    @Override public double getDamageBase() { return 1.0; }
    @Override public double getDamageMax() { return 5.0; }
    @Override public double getSpeedBase() { return 0.18; }
    @Override public double getSpeedMax() { return 0.3; }
    @Override public boolean canBeRidden() { return true; }
    @Override public boolean breaksBlocks() { return true; }
    @Override public String getDiet() { return "PISCIVORE"; }
    @Override public String getResponseType() { return "PASSIVE"; }
}
