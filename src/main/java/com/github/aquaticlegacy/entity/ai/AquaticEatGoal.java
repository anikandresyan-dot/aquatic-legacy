package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Eat dropped food items from the water.
 * Uses direct deltaMovement manipulation (F&A Revival style) to swim toward food.
 */
public class AquaticEatGoal extends Goal {
    private final AquaticPrehistoric entity;
    private ItemEntity targetFood;
    private int searchCooldown;

    private static final double ACCELERATION = 0.1;

    public AquaticEatGoal(AquaticPrehistoric entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
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
        // No need for MoveControl — tick() drives motion directly
    }

    @Override
    public void tick() {
        if (targetFood == null || !targetFood.isAlive()) return;

        double dx = targetFood.getX() - entity.getX();
        double dy = targetFood.getY() - entity.getY();
        double dz = targetFood.getZ() - entity.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist < 2.0) {
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
        } else if (dist > 0.5) {
            // Direct deltaMovement toward food (F&A Revival style)
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(
                motion.x + (dx / dist) * ACCELERATION,
                motion.y + (dy / dist) * ACCELERATION,
                motion.z + (dz / dist) * ACCELERATION
            );

            // Yaw from motion
            Vec3 newMotion = entity.getDeltaMovement();
            if (newMotion.x * newMotion.x + newMotion.z * newMotion.z > 0.0001) {
                float yaw = -((float) Math.atan2(newMotion.x, newMotion.z)) * (180F / (float) Math.PI);
                entity.setYRot(yaw);
                entity.yBodyRot = yaw;
            }
        }

        entity.getLookControl().setLookAt(targetFood, 30, 30);
    }

    @Override
    public void stop() {
        targetFood = null;
    }
}
