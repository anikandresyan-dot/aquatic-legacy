package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Grab attack for large predators (Kronosaurus, Tylosaurus).
 * Uses Pure Vanilla PathNavigation for chasing.
 */
public class AquaticGrabAttackGoal extends Goal {
    private final AquaticPrehistoric entity;
    private final double speedModifier;
    private LivingEntity target;
    private int attackTimer;
    private int grabTimer;
    private boolean isGrabbing;

    public AquaticGrabAttackGoal(AquaticPrehistoric entity, double speedModifier) {
        this.entity = entity;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingTarget = entity.getTarget();
        if (livingTarget == null || !livingTarget.isAlive()) return false;
        this.target = livingTarget;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && entity.getTarget() != null;
    }

    @Override
    public void start() {
        attackTimer = 0;
        grabTimer = 0;
        isGrabbing = false;
        entity.getNavigation().moveTo(target, speedModifier * 1.2);
    }

    @Override
    public void tick() {
        if (target == null) return;

        entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distSq = entity.distanceToSqr(target);

        attackTimer--;

        if (isGrabbing) {
            // Hold the target — drag it along
            target.startRiding(entity, true);
            grabTimer++;
            
            // Deal periodic damage while grabbing
            if (grabTimer % 20 == 0) {
                float damage = (float) entity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                target.hurt(entity.damageSources().mobAttack(entity), damage * 0.5f);
            }
            
            // Release after 3 seconds or if target dies
            if (grabTimer >= 60 || !target.isAlive()) {
                target.stopRiding();
                isGrabbing = false;
                grabTimer = 0;
            }
        } else {
            // Chase using vanilla navigation
            if (distSq > 4.0) {
                if (entity.tickCount % 10 == 0) {
                    entity.getNavigation().moveTo(target, speedModifier * 1.2);
                }
            } else if (attackTimer <= 0) {
                // Attempt grab on large enough adults
                if (entity.isAdult() && entity.getRandom().nextFloat() < 0.3f) {
                    isGrabbing = true;
                    grabTimer = 0;
                }
                
                // Regular attack
                float damage = (float) entity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                target.hurt(entity.damageSources().mobAttack(entity), damage);
                attackTimer = 20;
                
                // Feed on kill
                if (!target.isAlive()) {
                    entity.feed((int) (target.getBbWidth() * target.getBbHeight() * 15));
                }
            }
        }
    }

    @Override
    public void stop() {
        if (isGrabbing && target != null) {
            target.stopRiding();
        }
        target = null;
        isGrabbing = false;
        entity.getNavigation().stop();
    }
}
