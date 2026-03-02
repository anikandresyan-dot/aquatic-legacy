package com.github.aquaticlegacy.entity.data;

import com.github.aquaticlegacy.AquaticLegacyMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

/**
 * Data-driven entity info loader.
 * Loads entity_info/*.json files that define AI, attributes, diet, and other properties.
 * Mirrors Fossils' EntityDataLoader pattern.
 */
public class EntityDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, EntityInfo> ENTITY_DATA = new HashMap<>();

    public EntityDataLoader() {
        super(GSON, "entity_info");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager manager, ProfilerFiller profiler) {
        ENTITY_DATA.clear();
        
        data.forEach((location, json) -> {
            try {
                EntityInfo info = GSON.fromJson(json, EntityInfo.class);
                ENTITY_DATA.put(location.getPath(), info);
                AquaticLegacyMod.LOGGER.info("Loaded entity info: {}", location);
            } catch (Exception e) {
                AquaticLegacyMod.LOGGER.error("Failed to load entity info: {}", location, e);
            }
        });
        
        AquaticLegacyMod.LOGGER.info("Loaded {} entity info definitions", ENTITY_DATA.size());
    }

    public static EntityInfo getEntityInfo(String name) {
        return ENTITY_DATA.get(name);
    }
}
