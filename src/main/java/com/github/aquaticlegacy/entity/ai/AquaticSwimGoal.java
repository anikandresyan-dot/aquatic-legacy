package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 1:1 Replica of F&A Revival 1.20's `DinoRandomSwimGoal`.
 * Extends standard `RandomSwimmingGoal` but overrides water validation constraints.
 */
public class AquaticSwimGoal extends RandomSwimmingGoal {
    private final AquaticPrehistoric dino;

    public AquaticSwimGoal(AquaticPrehistoric dino, double speedModifier) {
        super(dino, speedModifier, 10);
        this.dino = dino;
    }

    @Override
    public boolean canUse() {
        if (!this.dino.isInWater() || this.dino.getTarget() != null || this.dino.getOrder() != AquaticPrehistoric.ORDER_WANDER) {
            return false;
        }
        return super.canUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 targetPos = BehaviorUtils.getRandomSwimmablePos(this.dino, 10, 7);
        if (targetPos != null && this.dino.level().getFluidState(BlockPos.containing(targetPos)).is(FluidTags.WATER)) {
            return targetPos;
        }
        
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        RandomSource random = this.dino.getRandom();
        
        if (this.dino.getTarget() == null || this.dino.getTarget().isDeadOrDying()) {
            for (int i = 0; i < 20; ++i) {
                mutableBlockPos.set(
                    this.dino.blockPosition().getX() + random.nextInt(16) - 7,
                    this.dino.blockPosition().getY() + random.nextInt(8) - 4,
                    this.dino.blockPosition().getZ() + random.nextInt(16) - 7
                );
                if (this.dino.level().getFluidState(mutableBlockPos).is(FluidTags.WATER)) {
                    return Vec3.atBottomCenterOf(mutableBlockPos); // F&A logic wrapper
                }
            }
        }
        return null;
    }
}
