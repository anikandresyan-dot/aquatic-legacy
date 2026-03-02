package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Random swimming behavior for aquatic prehistoric creatures.
 * Uses DIRECT deltaMovement manipulation (like F&A Revival's WaterDinoAIWander)
 * instead of MoveControl/Navigation, which have issues with underwater pathing.
 *
 * Movement is achieved by adding a normalized direction vector * acceleration
 * to the entity's deltaMovement each tick. The travel() method's drag (0.9)
 * naturally limits max speed.
 */
public class AquaticSwimGoal extends Goal {
    private final AquaticPrehistoric entity;
    private double waypointX, waypointY, waypointZ;
    private int courseChangeCooldown;
    private int swimTimer;
    private double lastX, lastY, lastZ;

    private static final int MAX_SWIM_TICKS = 200;
    private static final int STUCK_CHECK_INTERVAL = 40;
    private static final double STUCK_THRESHOLD_SQ = 1.0;
    private static final double ACCELERATION = 0.1; // F&A Revival's value

    public AquaticSwimGoal(AquaticPrehistoric entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isSleeping()) return false;
        if (entity.getOrder() == AquaticPrehistoric.ORDER_STAY) return false;
        if (!entity.isInWater()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.isSleeping()) return false;
        if (!entity.isInWater()) return false;
        if (swimTimer >= MAX_SWIM_TICKS) return false;
        double distSq = distSqToWaypoint();
        return distSq > 2.0;
    }

    @Override
    public void start() {
        pickRandomSwimTarget();
        swimTimer = 0;
        courseChangeCooldown = 0;
        lastX = entity.getX();
        lastY = entity.getY();
        lastZ = entity.getZ();
    }

    @Override
    public void tick() {
        swimTimer++;

        double dx = waypointX - entity.getX();
        double dy = waypointY - entity.getY();
        double dz = waypointZ - entity.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Course change on cooldown (like F&A Revival)
        if (--courseChangeCooldown <= 0) {
            courseChangeCooldown = entity.getRandom().nextInt(10) + 5;

            if (dist > 0.5 && isTargetInWater()) {
                // Direct deltaMovement manipulation — the F&A Revival way
                Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(
                    motion.x + (dx / dist) * ACCELERATION,
                    motion.y + (dy / dist) * ACCELERATION,
                    motion.z + (dz / dist) * ACCELERATION
                );
            } else {
                // Target not reachable, pick new one
                pickRandomSwimTarget();
            }
        }

        // Update yaw based on motion direction (F&A Revival style)
        Vec3 motion = entity.getDeltaMovement();
        if (motion.x * motion.x + motion.z * motion.z > 0.0001) {
            float yaw = -((float) Math.atan2(motion.x, motion.z)) * (180F / (float) Math.PI);
            entity.setYRot(yaw);
            entity.yBodyRot = yaw;
        }

        // Stuck detection
        if (swimTimer % STUCK_CHECK_INTERVAL == 0) {
            double movedSq = (entity.getX() - lastX) * (entity.getX() - lastX) +
                             (entity.getY() - lastY) * (entity.getY() - lastY) +
                             (entity.getZ() - lastZ) * (entity.getZ() - lastZ);
            if (movedSq < STUCK_THRESHOLD_SQ) {
                pickRandomSwimTarget();
            }
            lastX = entity.getX();
            lastY = entity.getY();
            lastZ = entity.getZ();
        }
    }

    @Override
    public void stop() {
        swimTimer = 0;
        courseChangeCooldown = 0;
    }

    private double distSqToWaypoint() {
        double dx = waypointX - entity.getX();
        double dy = waypointY - entity.getY();
        double dz = waypointZ - entity.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private boolean isTargetInWater() {
        BlockPos targetPos = BlockPos.containing(waypointX, waypointY, waypointZ);
        return !entity.level().getFluidState(targetPos).isEmpty();
    }

    private void pickRandomSwimTarget() {
        BlockPos pos = entity.blockPosition();

        for (int attempts = 0; attempts < 6; attempts++) {
            // Random direction, 8-16 blocks away (like F&A's ±16 range)
            double dx = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dz = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * 16.0F;

            // Vertical: ±8 blocks (like F&A's ±8 range)
            double dy = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * 8.0F;

            double tx = pos.getX() + dx;
            double ty = Math.max(entity.level().getMinBuildHeight() + 3, pos.getY() + dy);
            double tz = pos.getZ() + dz;

            // Verify target is in water
            BlockPos targetPos = BlockPos.containing(tx, ty, tz);
            if (!entity.level().getFluidState(targetPos).isEmpty()) {
                waypointX = tx;
                waypointY = ty;
                waypointZ = tz;
                return;
            }
        }

        // Fallback: stay near current position
        waypointX = pos.getX() + (entity.getRandom().nextDouble() - 0.5) * 6.0;
        waypointY = Math.max(entity.level().getMinBuildHeight() + 3, pos.getY() + (entity.getRandom().nextDouble() - 0.5) * 4.0);
        waypointZ = pos.getZ() + (entity.getRandom().nextDouble() - 0.5) * 6.0;
    }
}
