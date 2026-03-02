package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Follow owner goal for tamed aquatic creatures.
 * Uses Pure Vanilla PathNavigation.
 * Teleports if too far.
 */
public class AquaticFollowOwnerGoal extends Goal {
    private final AquaticPrehistoric entity;
    private final double speedModifier;
    private final float startDist;
    private final float stopDist;
    private LivingEntity owner;
    private int teleportCooldown;

    public AquaticFollowOwnerGoal(AquaticPrehistoric entity, double speed, float startDist, float stopDist) {
        this.entity = entity;
        this.speedModifier = speed;
        this.startDist = startDist;
        this.stopDist = stopDist;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!entity.isTamed()) return false;
        if (entity.getOrder() != AquaticPrehistoric.ORDER_FOLLOW) return false;
        Entity ownerEntity = entity.getOwner();
        if (!(ownerEntity instanceof LivingEntity livingOwner)) return false;
        if (entity.distanceToSqr(ownerEntity) < startDist * startDist) return false;
        this.owner = livingOwner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.getOrder() != AquaticPrehistoric.ORDER_FOLLOW) return false;
        return owner != null && entity.distanceToSqr(owner) > stopDist * stopDist;
    }

    @Override
    public void start() {
        teleportCooldown = 0;
    }

    @Override
    public void tick() {
        if (owner == null) return;

        double distSq = entity.distanceToSqr(owner);

        // Teleport if very far
        if (distSq > 400.0) { // 20 * 20
            teleportCooldown++;
            if (teleportCooldown > 60) {
                entity.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                entity.getNavigation().stop();
                teleportCooldown = 0;
                return;
            }
        } else {
            teleportCooldown = 0;
        }

        entity.getLookControl().setLookAt(owner, 10.0f, 40.0f);

        // Move towards owner
        if (distSq > stopDist * stopDist) {
            // Only recalculate path occasionally
            if (entity.tickCount % 10 == 0) {
                entity.getNavigation().moveTo(owner, speedModifier);
            }
        } else {
            entity.getNavigation().stop();
        }
    }

    @Override
    public void stop() {
        owner = null;
        entity.getNavigation().stop();
    }
}
