package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Panic/flee goal for passive aquatic creatures when hurt.
 * Uses direct deltaMovement manipulation for fast fleeing (F&A Revival style).
 */
public class AquaticPanicGoal extends Goal {
    private final AquaticPrehistoric entity;
    private final double speedMultiplier;
    private double targetX, targetY, targetZ;
    private int panicTimer;
    private int courseChangeCooldown;

    private static final double PANIC_ACCELERATION = 0.15; // Faster than normal swim

    public AquaticPanicGoal(AquaticPrehistoric entity, double speedMultiplier) {
        this.entity = entity;
        this.speedMultiplier = speedMultiplier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.getLastHurtByMob() == null) return false;
        return entity.tickCount - entity.getLastHurtByMobTimestamp() < 100;
    }

    @Override
    public boolean canContinueToUse() {
        return panicTimer > 0;
    }

    @Override
    public void start() {
        panicTimer = 100;
        courseChangeCooldown = 0;
        findRandomFleePosition();
    }

    @Override
    public void tick() {
        panicTimer--;

        if (--courseChangeCooldown <= 0) {
            courseChangeCooldown = 10; // Re-evaluate more frequently when panicking

            double dx = targetX - entity.getX();
            double dy = targetY - entity.getY();
            double dz = targetZ - entity.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist > 1.0) {
                double accel = PANIC_ACCELERATION * speedMultiplier;
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(
                    motion.x + (dx / dist) * accel,
                    motion.y + (dy / dist) * accel,
                    motion.z + (dz / dist) * accel
                );
            } else {
                // Reached flee point, pick another
                findRandomFleePosition();
            }
        }

        // Yaw from motion
        Vec3 motion = entity.getDeltaMovement();
        if (motion.x * motion.x + motion.z * motion.z > 0.0001) {
            float yaw = -((float) Math.atan2(motion.x, motion.z)) * (180F / (float) Math.PI);
            entity.setYRot(yaw);
            entity.yBodyRot = yaw;
        }
    }

    private void findRandomFleePosition() {
        BlockPos pos = entity.blockPosition();
        targetX = pos.getX() + (entity.getRandom().nextDouble() - 0.5) * 24.0;
        targetY = Math.max(entity.level().getMinBuildHeight() + 5, pos.getY() + (entity.getRandom().nextDouble() - 0.3) * 10.0);
        targetZ = pos.getZ() + (entity.getRandom().nextDouble() - 0.5) * 24.0;
    }

    @Override
    public void stop() {
        panicTimer = 0;
    }
}
