package com.github.aquaticlegacy.item;

import com.github.aquaticlegacy.AquaticLegacyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Creative mode tab for Aquatic Legacy items.
 */
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AquaticLegacyMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> AQUATIC_LEGACY_TAB = TABS.register("aquatic_legacy_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + AquaticLegacyMod.MOD_ID))
                    .icon(() -> new ItemStack(ModItems.ELASMOSAURUS_SPAWN_EGG.get()))
                    .displayItems((params, output) -> {
                        // Spawn Eggs
                        output.accept(ModItems.ELASMOSAURUS_SPAWN_EGG.get());
                        output.accept(ModItems.KRONOSAURUS_SPAWN_EGG.get());
                        output.accept(ModItems.DUNKLEOSTEUS_SPAWN_EGG.get());
                        output.accept(ModItems.SHONISAURUS_SPAWN_EGG.get());
                        output.accept(ModItems.TYLOSAURUS_SPAWN_EGG.get());
                        output.accept(ModItems.ARCHELON_SPAWN_EGG.get());
                        // DNA
                        output.accept(ModItems.ELASMOSAURUS_DNA.get());
                        output.accept(ModItems.KRONOSAURUS_DNA.get());
                        output.accept(ModItems.DUNKLEOSTEUS_DNA.get());
                        output.accept(ModItems.SHONISAURUS_DNA.get());
                        output.accept(ModItems.TYLOSAURUS_DNA.get());
                        output.accept(ModItems.ARCHELON_DNA.get());
                        // Misc
                        output.accept(ModItems.AQUATIC_FOSSIL.get());
                        output.accept(ModItems.ANCIENT_SCALE.get());
                    })
                    .build());
}
