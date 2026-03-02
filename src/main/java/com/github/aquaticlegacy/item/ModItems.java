package com.github.aquaticlegacy.item;

import com.github.aquaticlegacy.AquaticLegacyMod;
import com.github.aquaticlegacy.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Item registry for Aquatic Legacy mod.
 * Categorized by spawn eggs, DNA items, and misc items.
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AquaticLegacyMod.MOD_ID);

    // ========== Spawn Eggs ==========
    public static final RegistryObject<Item> ELASMOSAURUS_SPAWN_EGG = ITEMS.register("elasmosaurus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ELASMOSAURUS, 0x2E5984, 0x7EC8E3,
                    new Item.Properties()));
    public static final RegistryObject<Item> KRONOSAURUS_SPAWN_EGG = ITEMS.register("kronosaurus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.KRONOSAURUS, 0x1B3A2D, 0x4A7C59,
                    new Item.Properties()));
    public static final RegistryObject<Item> DUNKLEOSTEUS_SPAWN_EGG = ITEMS.register("dunkleosteus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DUNKLEOSTEUS, 0x4A3728, 0x8B6914,
                    new Item.Properties()));
    public static final RegistryObject<Item> SHONISAURUS_SPAWN_EGG = ITEMS.register("shonisaurus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SHONISAURUS, 0x3E6B8A, 0xB0D4E8,
                    new Item.Properties()));
    public static final RegistryObject<Item> TYLOSAURUS_SPAWN_EGG = ITEMS.register("tylosaurus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.TYLOSAURUS, 0x2D4F3C, 0x6B9E7A,
                    new Item.Properties()));
    public static final RegistryObject<Item> ARCHELON_SPAWN_EGG = ITEMS.register("archelon_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ARCHELON, 0x5C4033, 0xA0C4A0,
                    new Item.Properties()));

    // ========== DNA Items ==========
    public static final RegistryObject<Item> ELASMOSAURUS_DNA = ITEMS.register("elasmosaurus_dna",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KRONOSAURUS_DNA = ITEMS.register("kronosaurus_dna",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DUNKLEOSTEUS_DNA = ITEMS.register("dunkleosteus_dna",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHONISAURUS_DNA = ITEMS.register("shonisaurus_dna",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TYLOSAURUS_DNA = ITEMS.register("tylosaurus_dna",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCHELON_DNA = ITEMS.register("archelon_dna",
            () -> new Item(new Item.Properties()));

    // ========== Misc Items ==========
    public static final RegistryObject<Item> AQUATIC_FOSSIL = ITEMS.register("aquatic_fossil",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_SCALE = ITEMS.register("ancient_scale",
            () -> new Item(new Item.Properties()));
}
