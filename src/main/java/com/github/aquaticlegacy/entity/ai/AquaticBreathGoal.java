package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Surface breathing goal — some creatures periodically surface for realism.
 * Uses direct deltaMovement for upward motion (F&A Revival style).
 * Not critical for survival (they breathe underwater) but adds naturalistic behavior.
 */
public class AquaticBreathGoal extends Goal {
    private final AquaticPrehistoric entity;
    private int breathTimer;
    private int surfaceCooldown;

    public AquaticBreathGoal(AquaticPrehistoric entity) {
        this.entity = entity;
        this.breathTimer = 0;
        this.surfaceCooldown = 600 + entity.getRandom().nextInt(400); // 30-50 seconds
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isSleeping()) return false;

        surfaceCooldown--;
        if (surfaceCooldown <= 0) {
            surfaceCooldown = 600 + entity.getRandom().nextInt(400);
            return entity.isInWater();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return breathTimer < 100 && entity.isInWater();
    }

    @Override
    public void start() {
        breathTimer = 0;
    }

    @Override
    public void tick() {
        breathTimer++;

        // Direct motionY to surface (F&A Revival style — WaterDinoAISwimming)
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x, 0.1, motion.z);

        // Check if we reached the surface
        if (entity.blockPosition().getY() >= entity.level().getSeaLevel() - 2) {
            breathTimer = 100; // Done — go back to normal
        }
    }

    @Override
    public void stop() {
        breathTimer = 0;
    }
}
