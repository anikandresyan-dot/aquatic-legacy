package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Random swimming behavior for aquatic prehistoric creatures.
 * Uses Pure Vanilla 1.20 PathNavigation for buttery smooth movement.
 */
public class AquaticSwimGoal extends Goal {
    private final AquaticPrehistoric entity;

    public AquaticSwimGoal(AquaticPrehistoric entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (entity.isSleeping()) return false;
        if (entity.getOrder() == AquaticPrehistoric.ORDER_STAY) return false;
        if (!entity.isInWater()) return false;
        if (entity.getNavigation().isDone() && entity.getRandom().nextInt(10) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !entity.getNavigation().isDone() && entity.isInWater() && !entity.isSleeping();
    }

    @Override
    public void start() {
        Vec3 pos = findTargetPos();
        if (pos != null) {
            entity.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0D);
        }
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
    }

    private Vec3 findTargetPos() {
        BlockPos pos = entity.blockPosition();

        for (int attempts = 0; attempts < 10; attempts++) {
            double dx = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dz = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * 16.0F;
            double dy = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * 8.0F;

            double tx = pos.getX() + dx;
            double ty = Math.max(entity.level().getMinBuildHeight() + 3, pos.getY() + dy);
            double tz = pos.getZ() + dz;

            BlockPos targetPos = BlockPos.containing(tx, ty, tz);
            if (!entity.level().getFluidState(targetPos).isEmpty()) {
                return new Vec3(tx, ty, tz);
            }
        }
        return null;
    }
}
