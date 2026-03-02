package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Panic/flee goal for passive aquatic creatures when hurt.
 * Uses Pure Vanilla 1.20 PathNavigation for fast fleeing.
 */
public class AquaticPanicGoal extends Goal {
    private final AquaticPrehistoric entity;
    private final double speedMultiplier;
    private double targetX, targetY, targetZ;

    public AquaticPanicGoal(AquaticPrehistoric entity, double speedMultiplier) {
        this.entity = entity;
        this.speedMultiplier = speedMultiplier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.getLastHurtByMob() == null) return false;
        if (entity.tickCount - entity.getLastHurtByMobTimestamp() > 100) return false;
        return findRandomFleePosition();
    }

    @Override
    public boolean canContinueToUse() {
        return !entity.getNavigation().isDone();
    }

    @Override
    public void start() {
        entity.getNavigation().moveTo(targetX, targetY, targetZ, speedMultiplier * 1.5);
    }

    private boolean findRandomFleePosition() {
        BlockPos pos = entity.blockPosition();
        for (int i = 0; i < 10; i++) {
            double tx = pos.getX() + (entity.getRandom().nextDouble() - 0.5) * 24.0;
            double ty = Math.max(entity.level().getMinBuildHeight() + 5, pos.getY() + (entity.getRandom().nextDouble() - 0.3) * 10.0);
            double tz = pos.getZ() + (entity.getRandom().nextDouble() - 0.5) * 24.0;
            
            BlockPos targetPos = BlockPos.containing(tx, ty, tz);
            if (!entity.level().getFluidState(targetPos).isEmpty()) {
                targetX = tx;
                targetY = ty;
                targetZ = tz;
                return true;
            }
        }
        return false;
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
    }
}
