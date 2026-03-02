package com.github.aquaticlegacy;

import com.github.aquaticlegacy.entity.ModEntities;
import com.github.aquaticlegacy.entity.data.EntityDataLoader;
import com.github.aquaticlegacy.item.ModItems;
import com.github.aquaticlegacy.item.ModTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AquaticLegacyMod.MOD_ID)
public class AquaticLegacyMod {
    public static final String MOD_ID = "aquatic_legacy";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public AquaticLegacyMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Aquatic Legacy: Prehistoric Seas initialized!");
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new EntityDataLoader());
    }
}
