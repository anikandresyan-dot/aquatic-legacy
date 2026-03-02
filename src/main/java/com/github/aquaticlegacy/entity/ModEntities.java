package com.github.aquaticlegacy.entity;

import com.github.aquaticlegacy.AquaticLegacyMod;
import com.github.aquaticlegacy.entity.prehistoric.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AquaticLegacyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AquaticLegacyMod.MOD_ID);

    public static final RegistryObject<EntityType<Elasmosaurus>> ELASMOSAURUS = ENTITIES.register("elasmosaurus",
            () -> EntityType.Builder.<Elasmosaurus>of(Elasmosaurus::new, MobCategory.WATER_CREATURE)
                    .sized(2.5f, 1.8f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(AquaticLegacyMod.MOD_ID, "elasmosaurus").toString()));

    public static final RegistryObject<EntityType<Kronosaurus>> KRONOSAURUS = ENTITIES.register("kronosaurus",
            () -> EntityType.Builder.<Kronosaurus>of(Kronosaurus::new, MobCategory.WATER_CREATURE)
                    .sized(3.0f, 2.0f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(AquaticLegacyMod.MOD_ID, "kronosaurus").toString()));

    public static final RegistryObject<EntityType<Dunkleosteus>> DUNKLEOSTEUS = ENTITIES.register("dunkleosteus",
            () -> EntityType.Builder.<Dunkleosteus>of(Dunkleosteus::new, MobCategory.WATER_CREATURE)
                    .sized(2.0f, 1.5f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(AquaticLegacyMod.MOD_ID, "dunkleosteus").toString()));

    public static final RegistryObject<EntityType<Shonisaurus>> SHONISAURUS = ENTITIES.register("shonisaurus",
            () -> EntityType.Builder.<Shonisaurus>of(Shonisaurus::new, MobCategory.WATER_CREATURE)
                    .sized(4.0f, 2.5f)
                    .clientTrackingRange(12)
                    .build(new ResourceLocation(AquaticLegacyMod.MOD_ID, "shonisaurus").toString()));

    public static final RegistryObject<EntityType<Tylosaurus>> TYLOSAURUS = ENTITIES.register("tylosaurus",
            () -> EntityType.Builder.<Tylosaurus>of(Tylosaurus::new, MobCategory.WATER_CREATURE)
                    .sized(2.8f, 1.6f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(AquaticLegacyMod.MOD_ID, "tylosaurus").toString()));

    public static final RegistryObject<EntityType<Archelon>> ARCHELON = ENTITIES.register("archelon",
            () -> EntityType.Builder.<Archelon>of(Archelon::new, MobCategory.WATER_CREATURE)
                    .sized(2.2f, 1.2f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(AquaticLegacyMod.MOD_ID, "archelon").toString()));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ELASMOSAURUS.get(), AquaticPrehistoric.createAttributes().build());
        event.put(KRONOSAURUS.get(), AquaticPrehistoric.createAttributes().build());
        event.put(DUNKLEOSTEUS.get(), AquaticPrehistoric.createAttributes().build());
        event.put(SHONISAURUS.get(), AquaticPrehistoric.createAttributes().build());
        event.put(TYLOSAURUS.get(), AquaticPrehistoric.createAttributes().build());
        event.put(ARCHELON.get(), AquaticPrehistoric.createAttributes().build());
    }
}
