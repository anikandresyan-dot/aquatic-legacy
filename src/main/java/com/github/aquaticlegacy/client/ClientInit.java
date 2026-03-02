package com.github.aquaticlegacy.client;

import com.github.aquaticlegacy.AquaticLegacyMod;
import com.github.aquaticlegacy.client.renderer.AquaticGeoRenderer;
import com.github.aquaticlegacy.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side initialization — registers entity renderers.
 */
@Mod.EventBusSubscriber(modid = AquaticLegacyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ELASMOSAURUS.get(), AquaticGeoRenderer::new);
        event.registerEntityRenderer(ModEntities.KRONOSAURUS.get(), AquaticGeoRenderer::new);
        event.registerEntityRenderer(ModEntities.DUNKLEOSTEUS.get(), AquaticGeoRenderer::new);
        event.registerEntityRenderer(ModEntities.SHONISAURUS.get(), AquaticGeoRenderer::new);
        event.registerEntityRenderer(ModEntities.TYLOSAURUS.get(), AquaticGeoRenderer::new);
        event.registerEntityRenderer(ModEntities.ARCHELON.get(), AquaticGeoRenderer::new);
    }
}
