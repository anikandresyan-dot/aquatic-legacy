package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Turtle;

import java.util.function.Predicate;

/**
 * Hunting AI for aquatic predators.
 * Targets fish, squids, dolphins, turtles, and other smaller prehistoric creatures.
 * Mirrors Fossils' HuntingTargetGoal — considers hunger, diet, and size.
 * Only hunts when hungry.
 */
public class AquaticHuntGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final AquaticPrehistoric hunter;
    private final boolean piscivoreOnly;

    public AquaticHuntGoal(AquaticPrehistoric hunter, boolean piscivoreOnly) {
        super(hunter, LivingEntity.class, 10, true, false, createPredicate(hunter, piscivoreOnly));
        this.hunter = hunter;
        this.piscivoreOnly = piscivoreOnly;
    }

    private static Predicate<LivingEntity> createPredicate(AquaticPrehistoric hunter, boolean piscivoreOnly) {
        return target -> {
            if (target == null) return false;
            // Don't hunt owner
            if (hunter.isTamed() && target.getUUID().equals(hunter.getOwnerUUID())) return false;
            // Don't hunt same species
            if (target.getType() == hunter.getType()) return false;
            // Only hunt smaller things
            if (target.getBbWidth() * target.getBbHeight() > hunter.getBbWidth() * hunter.getBbHeight() * 0.8f) return false;
            
            if (piscivoreOnly) {
                // Only fish, squid, small aquatic creatures
                return target instanceof AbstractFish || target instanceof Squid ||
                       target instanceof Dolphin || target instanceof Turtle;
            } else {
                // Carnivore — anything alive in water including players (handled by separate goal)
                return target.isInWater() || target instanceof AbstractFish || 
                       target instanceof Squid || target instanceof Dolphin;
            }
        };
    }

    @Override
    public boolean canUse() {
        // Only hunt when hungry
        if (!hunter.isHungry()) return false;
        if (hunter.isSleeping()) return false;
        if (!hunter.isAdult() && !hunter.isTeen()) return false;
        return super.canUse();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        // Feed when target killed
        LivingEntity target = hunter.getTarget();
        if (target != null && !target.isAlive()) {
            int foodValue = getFoodValueForEntity(target);
            hunter.feed(foodValue);
        }
    }

    private int getFoodValueForEntity(LivingEntity entity) {
        if (entity instanceof AbstractFish) return 10;
        if (entity instanceof Squid) return 20;
        if (entity instanceof Dolphin) return 25;
        if (entity instanceof Turtle) return 15;
        // Scale by entity size
        return (int) (entity.getBbWidth() * entity.getBbHeight() * 10);
    }
}
