package com.github.aquaticlegacy.entity.prehistoric;

import com.github.aquaticlegacy.entity.ai.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Tylosaurus — Fast marine mosasaur (related to Mosasaurus but different genus).
 * Very fast swimmer, aggressive predator. Attacks most things in the water.
 * Rideable when tamed. High damage output.
 */
public class Tylosaurus extends AquaticPrehistoric {

    public Tylosaurus(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticBreathGoal(this));
        this.goalSelector.addGoal(1, new AquaticGrabAttackGoal(this, 2.0));
        this.goalSelector.addGoal(2, new AquaticFollowOwnerGoal(this, 1.4, 10.0f, 3.0f));
        this.goalSelector.addGoal(3, new AquaticStayGoal(this));
        this.goalSelector.addGoal(4, new AquaticEatGoal(this));
        this.goalSelector.addGoal(5, new AquaticMatingGoal(this, 1.0));
        this.goalSelector.addGoal(6, new AquaticSwimGoal(this));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new AquaticHuntGoal(this, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                player -> !this.isTamed()));
    }

    @Override public String getEntityName() { return "tylosaurus"; }
    @Override public int getMaxHunger() { return 140; }
    @Override public int getAdultAgeDays() { return 10; }
    @Override public int getTeenAgeDays() { return 4; }
    @Override public float getScaleBase() { return 0.35f; }
    @Override public float getScaleMax() { return 1.8f; }
    @Override public double getHealthBase() { return 12.0; }
    @Override public double getHealthMax() { return 50.0; }
    @Override public double getDamageBase() { return 2.0; }
    @Override public double getDamageMax() { return 24.0; }
    @Override public double getSpeedBase() { return 0.25; }
    @Override public double getSpeedMax() { return 0.5; }
    @Override public boolean canBeRidden() { return true; }
    @Override public boolean breaksBlocks() { return true; }
    @Override public String getDiet() { return "PISCI_CARNIVORE"; }
    @Override public String getResponseType() { return "AGGRESSIVE"; }
}
