package com.github.aquaticlegacy.client.model;

import com.github.aquaticlegacy.AquaticLegacyMod;
import com.github.aquaticlegacy.entity.prehistoric.AquaticPrehistoric;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Generic GeckoLib model for all aquatic prehistoric creatures.
 * Dynamically resolves model, texture, and animation paths based on entity name.
 */
public class AquaticGeoModel extends GeoModel<AquaticPrehistoric> {

    @Override
    public ResourceLocation getModelResource(AquaticPrehistoric entity) {
        return new ResourceLocation(AquaticLegacyMod.MOD_ID,
                "geo/entity/" + entity.getEntityName() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AquaticPrehistoric entity) {
        return entity.getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(AquaticPrehistoric entity) {
        return new ResourceLocation(AquaticLegacyMod.MOD_ID,
                "animations/entity/" + entity.getEntityName() + ".animation.json");
    }
}
