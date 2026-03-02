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
 * Kronosaurus — Giant pliosaur, apex predator of the Cretaceous seas.
 * Extremely aggressive. Grab attack. Attacks players on sight.
 * Tameable only with nautilus shell when adult (difficult). 
 */
public class Kronosaurus extends AquaticPrehistoric {

    public Kronosaurus(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticBreathGoal(this));
        this.goalSelector.addGoal(1, new AquaticGrabAttackGoal(this, 1.8));
        this.goalSelector.addGoal(2, new AquaticFollowOwnerGoal(this, 1.2, 10.0f, 3.0f));
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

    @Override public String getEntityName() { return "kronosaurus"; }
    @Override public int getMaxHunger() { return 150; }
    @Override public int getAdultAgeDays() { return 12; }
    @Override public int getTeenAgeDays() { return 5; }
    @Override public float getScaleBase() { return 0.4f; }
    @Override public float getScaleMax() { return 2.0f; }
    @Override public double getHealthBase() { return 15.0; }
    @Override public double getHealthMax() { return 60.0; }
    @Override public double getDamageBase() { return 3.0; }
    @Override public double getDamageMax() { return 28.0; }
    @Override public double getSpeedBase() { return 0.2; }
    @Override public double getSpeedMax() { return 0.4; }
    @Override public boolean canBeRidden() { return true; }
    @Override public boolean breaksBlocks() { return true; }
    @Override public String getDiet() { return "PISCI_CARNIVORE"; }
    @Override public String getResponseType() { return "AGGRESSIVE"; }
}
