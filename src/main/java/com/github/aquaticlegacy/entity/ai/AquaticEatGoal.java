package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

/**
 * Eat dropped food items from the water.
 * Uses Pure Vanilla PathNavigation.
 */
public class AquaticEatGoal extends Goal {
    private final AquaticPrehistoric entity;
    private ItemEntity targetFood;
    private int searchCooldown;

    public AquaticEatGoal(AquaticPrehistoric entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!entity.isHungry()) return false;
        if (entity.isSleeping()) return false;
        if (searchCooldown > 0) {
            searchCooldown--;
            return false;
        }

        // Search for food items nearby
        List<ItemEntity> items = entity.level().getEntitiesOfClass(ItemEntity.class,
                entity.getBoundingBox().inflate(16.0, 8.0, 16.0),
                item -> entity.isFoodItem(item.getItem()));

        if (!items.isEmpty()) {
            targetFood = items.get(0);
            double closestDist = entity.distanceToSqr(targetFood);
            for (ItemEntity item : items) {
                double dist = entity.distanceToSqr(item);
                if (dist < closestDist) {
                    targetFood = item;
                    closestDist = dist;
                }
            }
            return true;
        }
        searchCooldown = 40;
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return targetFood != null && targetFood.isAlive() && entity.isHungry();
    }

    @Override
    public void start() {
        entity.getNavigation().moveTo(targetFood, 1.2D);
    }

    @Override
    public void tick() {
        if (targetFood == null || !targetFood.isAlive()) return;

        double dist = entity.distanceToSqr(targetFood);

        if (dist < 4.0) { // Sqr distance < 4 means raw distance < 2
            // Eat the item
            ItemStack stack = targetFood.getItem();
            int foodValue = entity.getFoodValue(stack);
            entity.feed(foodValue);
            stack.shrink(1);
            if (stack.isEmpty()) {
                targetFood.discard();
            }
            entity.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT, 1.0f, 1.0f);
            targetFood = null;
        } else {
            // Keep navigating to moving items
            if (entity.tickCount % 10 == 0) {
                entity.getNavigation().moveTo(targetFood, 1.2D);
            }
        }
    }

    @Override
    public void stop() {
        targetFood = null;
        entity.getNavigation().stop();
    }
}
