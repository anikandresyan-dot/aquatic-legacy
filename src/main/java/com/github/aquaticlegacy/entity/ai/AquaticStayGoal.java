package com.github.aquaticlegacy.entity.ai;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Stay in place goal when order is STAY.
 * Mirrors Fossils' DinoStayGoal.
 */
public class AquaticStayGoal extends Goal {
    private final AquaticPrehistoric entity;

    public AquaticStayGoal(AquaticPrehistoric entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return entity.isTamed() && entity.getOrder() == AquaticPrehistoric.ORDER_STAY;
    }

    @Override
    public void start() {
        entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        entity.getNavigation().stop();
    }
}
