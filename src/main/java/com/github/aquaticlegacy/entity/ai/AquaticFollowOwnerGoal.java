package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Follow owner goal for tamed aquatic creatures.
 * Uses direct deltaMovement manipulation (F&A Revival style).
 * Teleports if too far.
 */
public class AquaticFollowOwnerGoal extends Goal {
    private final AquaticPrehistoric entity;
    private final double speedModifier;
    private final float startDist;
    private final float stopDist;
    private LivingEntity owner;
    private int teleportCooldown;

    private static final double ACCELERATION = 0.12; // Slightly faster than wander

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

        double dx = owner.getX() - entity.getX();
        double dy = owner.getY() - entity.getY();
        double dz = owner.getZ() - entity.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Teleport if very far
        if (dist > 20) {
            teleportCooldown++;
            if (teleportCooldown > 60) {
                entity.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                teleportCooldown = 0;
                return;
            }
        }

        // Direct deltaMovement (F&A Revival style)
        if (dist > 1.0) {
            double accel = ACCELERATION * speedModifier;
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(
                motion.x + (dx / dist) * accel,
                motion.y + (dy / dist) * accel,
                motion.z + (dz / dist) * accel
            );
        }

        // Look at owner + yaw from motion
        entity.getLookControl().setLookAt(owner, 10.0f, 40.0f);
        Vec3 motion = entity.getDeltaMovement();
        if (motion.x * motion.x + motion.z * motion.z > 0.0001) {
            float yaw = -((float) Math.atan2(motion.x, motion.z)) * (180F / (float) Math.PI);
            entity.setYRot(yaw);
            entity.yBodyRot = yaw;
        }
    }

    @Override
    public void stop() {
        owner = null;
    }
}
