package com.github.aquaticlegacy.entity.ai.control;

import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;

/**
 * 1:1 Replica of F&A Revival 1.20's `CustomSwimMoveControl`.
 * Fixes the 1.20 "swimming backward/sideways" issue by manually calculating Geckolib-compatible pitch/yaw.
 */
public class AquaticSwimMoveControl extends SmoothSwimmingMoveControl {
    private final AquaticPrehistoric mob;

    public AquaticSwimMoveControl(AquaticPrehistoric mob) {
        super(mob, 85, 10, 0.1f, 0.1f, true);
        this.mob = mob;
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            double x = this.wantedX - this.mob.getX();
            double y = this.wantedY - this.mob.getY();
            double z = this.wantedZ - this.mob.getZ();
            double horizontalDist = x * x + z * z;
            double dist = horizontalDist + y * y;
            
            if (dist < 2.5E-7) {
                this.mob.setZza(0.0f);
            } else {
                if (horizontalDist > 0.3) {
                    float h = clampTo360(yawToYRot(Mth.atan2(z, x) * 57.2957763671875));
                    float g = this.rotlerp(clampTo360(this.mob.getYRot()), h, 5.0f);
                    this.mob.setYRot(g);
                    this.mob.yBodyRot = this.mob.getYRot();
                    this.mob.yHeadRot = this.mob.getYRot();
                }
                if (this.mob.isInWater()) {
                    float k;
                    float i = (float) this.mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
                    
                    if (horizontalDist < 4.0 && i > 0.12) {
                        i *= 0.5f;
                    }
                    this.mob.setSpeed(i);
                    double horDist = Math.sqrt(x * x + z * z);
                    if (Math.abs(y) > 1.0E-5 || Math.abs(horDist) > 1.0E-5) {
                        k = (float)(-Mth.atan2(y, horDist) * 57.2957763671875) + 90.0f;
                        k = Mth.clamp(k, 30.0f, 150.0f);
                        float g = this.rotlerp(this.mob.getXRot() + 90.0f, k, 5.0f);
                        this.mob.setXRot(g - 90.0f);
                    }
                    k = Mth.cos((float)(this.mob.getXRot() * (Math.PI / 180)));
                    float l = Mth.sin((float)(this.mob.getXRot() * (Math.PI / 180)));
                    this.mob.zza = k * i;
                    this.mob.yya = -l * i;
                    
                    // Optional jumping out logic (Amphibious support)
                    if (this.mob.level().getBlockState(BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ)).isAir()) {
                        this.mob.getJumpControl().jump();
                        this.operation = MoveControl.Operation.JUMPING;
                    }
                }
            }
        } else if (this.operation == MoveControl.Operation.JUMPING) {
            this.mob.setSpeed((float) this.mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED));
            if (!this.mob.isInWater() || this.mob.getDeltaMovement().y <= 0.0) {
                this.operation = MoveControl.Operation.WAIT;
            }
        } else {
            this.mob.setSpeed(0.0f);
            this.mob.setXxa(0.0f);
            this.mob.setYya(0.0f);
            this.mob.setZza(0.0f);
        }
    }

    // Official F&A Math utilities
    private static float yawToYRot(double yaw) {
        return (float)Mth.wrapDegrees(yaw - 90.0);
    }
    private static float clampTo360(double x) {
        return (float)(x - Math.floor(x / 360.0) * 360.0);
    }
}
