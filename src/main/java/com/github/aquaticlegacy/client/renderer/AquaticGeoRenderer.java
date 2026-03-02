package com.github.aquaticlegacy.client.renderer;

import com.github.aquaticlegacy.AquaticLegacyMod;
import com.github.aquaticlegacy.client.model.AquaticGeoModel;
import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * GeckoLib entity renderer for aquatic prehistoric creatures.
 * Handles dynamic scaling based on growth and sleeping overlay.
 */
public class AquaticGeoRenderer extends GeoEntityRenderer<AquaticPrehistoric> {

    public AquaticGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new AquaticGeoModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, 
                                      AquaticPrehistoric entity, float partialTick) {
        float scale = entity.getCurrentScale();
        poseStack.scale(scale, scale, scale);
        this.shadowRadius = 0.5f * scale;
    }
}
