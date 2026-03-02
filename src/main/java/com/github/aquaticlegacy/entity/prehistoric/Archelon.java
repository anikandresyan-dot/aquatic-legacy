package com.github.aquaticlegacy.entity.prehistoric;

import com.github.aquaticlegacy.entity.ai.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Archelon — Giant Cretaceous sea turtle.
 * Amphibious: swims in water, walks slowly on land to lay eggs.
 * Herbivore (eats kelp, seagrass). Calm and peaceful.
 * Tameable and can give turtle shell drops.
 */
public class Archelon extends AquaticPrehistoric {

    private WaterBoundPathNavigation waterNavigation;
    private GroundPathNavigation groundNavigation;

    public Archelon(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticBreathGoal(this));
        this.goalSelector.addGoal(1, new AquaticPanicGoal(this, 1.2));
        this.goalSelector.addGoal(2, new AquaticFollowOwnerGoal(this, 1.0, 10.0f, 3.0f));
        this.goalSelector.addGoal(3, new AquaticStayGoal(this));
        this.goalSelector.addGoal(4, new AquaticEatGoal(this));
        this.goalSelector.addGoal(5, new AquaticMatingGoal(this, 0.8));
        this.goalSelector.addGoal(6, new AquaticSwimGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8, 30));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isInWater()) {
            // F&A Revival style: same as base class
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            // Can walk on land (slowly)
            super.travel(travelVector);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        // Switch navigation based on water/land
        if (!this.level().isClientSide()) {
            if (this.isInWater()) {
                this.navigation = this.waterNavigation;
            } else {
                this.navigation = this.groundNavigation;
            }
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    // Archelon can survive on land
    @Override
    public void baseTick() {
        super.baseTick();
        // Don't suffocate on land
        this.setAirSupply(this.getMaxAirSupply());
    }

    // Override food check for herbivore diet
    @Override
    public boolean isFoodItem(ItemStack stack) {
        return stack.is(Items.KELP) || stack.is(Items.DRIED_KELP) || 
               stack.is(Items.SEAGRASS) || stack.is(Items.SEA_PICKLE);
    }

    @Override
    public int getFoodValue(ItemStack stack) {
        if (stack.is(Items.KELP) || stack.is(Items.SEAGRASS)) return 8;
        if (stack.is(Items.DRIED_KELP)) return 12;
        if (stack.is(Items.SEA_PICKLE)) return 5;
        return 0;
    }

    @Override public String getEntityName() { return "archelon"; }
    @Override public int getMaxHunger() { return 80; }
    @Override public int getAdultAgeDays() { return 7; }
    @Override public int getTeenAgeDays() { return 3; }
    @Override public float getScaleBase() { return 0.2f; }
    @Override public float getScaleMax() { return 1.0f; }
    @Override public double getHealthBase() { return 8.0; }
    @Override public double getHealthMax() { return 25.0; }
    @Override public double getDamageBase() { return 0.5; }
    @Override public double getDamageMax() { return 2.0; }
    @Override public double getSpeedBase() { return 0.15; }
    @Override public double getSpeedMax() { return 0.25; }
    @Override public boolean canBeRidden() { return true; }
    @Override public boolean breaksBlocks() { return false; }
    @Override public String getDiet() { return "HERBIVORE"; }
    @Override public String getResponseType() { return "PASSIVE"; }
}
