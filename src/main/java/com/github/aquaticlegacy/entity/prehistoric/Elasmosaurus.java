package com.github.aquaticlegacy.entity.prehistoric;

import com.github.aquaticlegacy.entity.ai.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

/**
 * Elasmosaurus — Large long-necked plesiosaur.
 * Calm piscivore, tameable by imprinting (baby) or nautilus shell (adult).
 * Swims gracefully, hunts fish. Does not attack players unless provoked.
 */
public class Elasmosaurus extends AquaticPrehistoric {

    public Elasmosaurus(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticBreathGoal(this));
        this.goalSelector.addGoal(1, new AquaticPanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new AquaticFollowOwnerGoal(this, 1.2, 10.0f, 3.0f));
        this.goalSelector.addGoal(3, new AquaticStayGoal(this));
        this.goalSelector.addGoal(4, new AquaticEatGoal(this));
        this.goalSelector.addGoal(5, new AquaticMatingGoal(this, 1.0));
        this.goalSelector.addGoal(6, new AquaticSwimGoal(this));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AquaticHuntGoal(this, true));
    }

    @Override public String getEntityName() { return "elasmosaurus"; }
    @Override public int getMaxHunger() { return 125; }
    @Override public int getAdultAgeDays() { return 8; }
    @Override public int getTeenAgeDays() { return 3; }
    @Override public float getScaleBase() { return 0.25f; }
    @Override public float getScaleMax() { return 1.2f; }
    @Override public double getHealthBase() { return 10.0; }
    @Override public double getHealthMax() { return 30.0; }
    @Override public double getDamageBase() { return 1.0; }
    @Override public double getDamageMax() { return 6.0; }
    @Override public double getSpeedBase() { return 0.2; }
    @Override public double getSpeedMax() { return 0.35; }
    @Override public boolean canBeRidden() { return false; }
    @Override public boolean breaksBlocks() { return false; }
    @Override public String getDiet() { return "PISCIVORE"; }
    @Override public String getResponseType() { return "WATER_CALM"; }
}
