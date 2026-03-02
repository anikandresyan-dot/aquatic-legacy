package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

/**
 * Mating goal — adults of the same species find each other and breed.
 * Mirrors Fossils' DinoMatingGoal — requires both adults, well-fed, good mood.
 * Creates baby entity on successful mating.
 */
public class AquaticMatingGoal extends Goal {
    private final AquaticPrehistoric entity;
    private final double speedModifier;
    private AquaticPrehistoric partner;
    private int matingTimer;
    private static final int MATE_COOLDOWN = 6000; // 5 minutes
    private int cooldownTimer = 0;

    public AquaticMatingGoal(AquaticPrehistoric entity, double speed) {
        this.entity = entity;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!entity.isAdult()) return false;
        if (entity.isHungry()) return false;
        if (entity.getMoodType() == AquaticPrehistoric.MoodType.ANGRY) return false;
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return false;
        }

        // Find a partner of same species, opposite gender, nearby
        List<? extends AquaticPrehistoric> nearbyMates = entity.level().getEntitiesOfClass(
                entity.getClass(),
                entity.getBoundingBox().inflate(16.0, 8.0, 16.0),
                e -> e != entity && e.isAdult() && e.isMale() != entity.isMale()
                        && !e.isHungry() && e.getMoodType() != AquaticPrehistoric.MoodType.ANGRY
        );

        if (!nearbyMates.isEmpty()) {
            partner = nearbyMates.get(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return partner != null && partner.isAlive() && matingTimer < 200;
    }

    @Override
    public void start() {
        matingTimer = 0;
    }

    @Override
    public void tick() {
        if (partner == null) return;
        
        entity.getLookControl().setLookAt(partner, 10.0f, 30.0f);
        
        if (entity.distanceToSqr(partner) > 4.0) {
            // Direct deltaMovement toward partner (F&A Revival style)
            double dx = partner.getX() - entity.getX();
            double dy = partner.getY() - entity.getY();
            double dz = partner.getZ() - entity.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 0.5) {
                double accel = 0.1 * speedModifier;
                net.minecraft.world.phys.Vec3 motion = entity.getDeltaMovement();
                entity.setDeltaMovement(
                    motion.x + (dx / dist) * accel,
                    motion.y + (dy / dist) * accel,
                    motion.z + (dz / dist) * accel
                );
            }
        } else {
            // Close enough, stop moving
            matingTimer++;
            
            // Spawn baby after mating
            if (matingTimer >= 100) {
                if (!entity.level().isClientSide()) {
                    try {
                        AquaticPrehistoric baby = (AquaticPrehistoric) entity.getType().create(entity.level());
                        if (baby != null) {
                            baby.moveTo(entity.getX(), entity.getY(), entity.getZ(), 0, 0);
                            baby.setAgeTicks(0);
                            baby.setHunger(baby.getMaxHunger() / 2);
                            entity.level().addFreshEntity(baby);
                        }
                    } catch (Exception e) {
                        // Fallback — entity creation failed
                    }
                }
                
                // Set cooldown
                cooldownTimer = MATE_COOLDOWN;
                matingTimer = 200; // End mating
                
                // Hearts
                entity.level().broadcastEntityEvent(entity, (byte) 18);
            }
        }
    }

    @Override
    public void stop() {
        partner = null;
    }
}
