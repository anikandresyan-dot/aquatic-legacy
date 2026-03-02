package com.github.aquaticlegacy.entity.prehistoric;

import com.github.aquaticlegacy.AquaticLegacyMod;
import com.github.aquaticlegacy.entity.data.EntityDataLoader;
import com.github.aquaticlegacy.entity.data.EntityInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Base class for all aquatic prehistoric creatures.
 * Mirrors the architecture of Fossils & Archeology Revival's PrehistoricSwimming class.
 * 
 * Features:
 * - Hunger system with starvation damage
 * - Mood system (happy/content/angry)
 * - Growth system (baby → teen → adult with visual scale)
 * - Taming system (imprinting babies, feeding adults)  
 * - Gender system (male/female textures)
 * - Sleep cycle (diurnal/nocturnal/both)
 * - Order system (stay/follow/wander)
 * - Swimming mechanics with depth preferences
 */
public abstract class AquaticPrehistoric extends WaterAnimal implements GeoEntity, OwnableEntity {

    // ========== Synched Data ==========
    private static final EntityDataAccessor<Integer> DATA_AGE_TICKS = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HUNGER = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MOOD = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_GENDER = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SLEEPING = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_ORDER = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<java.util.Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_TAMED = SynchedEntityData.defineId(AquaticPrehistoric.class, EntityDataSerializers.BOOLEAN);

    // ========== Constants ==========
    public static final int TICKS_PER_MC_DAY = 24000;
    public static final int HUNGER_TICK_INTERVAL = 200; // lose hunger every 10 seconds
    public static final int MOOD_TICK_INTERVAL = 100;   // mood update every 5 seconds
    
    // Order types
    public static final byte ORDER_WANDER = 0;
    public static final byte ORDER_FOLLOW = 1;
    public static final byte ORDER_STAY = 2;

    // ========== Instance Fields ==========
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected int ticksSinceLastMeal = 0;
    protected int starvationTimer = 0;
    protected float currentScale = 1.0f;
    
    // ========== Constructor ==========
    protected AquaticPrehistoric(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.5F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    // ========== Abstract Methods ==========
    
    /** Returns the unique entity name used for data lookups (e.g., "elasmosaurus") */
    public abstract String getEntityName();
    
    /** Returns the maximum hunger this entity can have */
    public abstract int getMaxHunger();
    
    /** Returns how many Minecraft days until this entity is an adult */
    public abstract int getAdultAgeDays();
    
    /** Returns how many Minecraft days until this entity is a teen */
    public abstract int getTeenAgeDays();
    
    /** Returns the minimum scale (baby) */
    public abstract float getScaleBase();
    
    /** Returns the maximum scale (adult) */
    public abstract float getScaleMax();
    
    /** Returns the base max health */
    public abstract double getHealthBase();
    
    /** Returns the adult max health */
    public abstract double getHealthMax();
    
    /** Returns the base damage */
    public abstract double getDamageBase();
    
    /** Returns the adult damage */
    public abstract double getDamageMax();
    
    /** Returns the base speed */
    public abstract double getSpeedBase();
    
    /** Returns the adult speed */
    public abstract double getSpeedMax();
    
    /** Whether this creature can be ridden */
    public abstract boolean canBeRidden();
    
    /** Whether this creature breaks blocks when large */
    public abstract boolean breaksBlocks();
    
    /** Diet type string for food lookups */
    public abstract String getDiet();
    
    /** AI response type: AGGRESSIVE, WATER_CALM, TERRITORIAL, PASSIVE */
    public abstract String getResponseType();

    // ========== Attribute Factory ==========
    public static AttributeSupplier.Builder createAttributes() {
        return WaterAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // ========== Data ==========
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_AGE_TICKS, 0);
        this.entityData.define(DATA_HUNGER, 100);
        this.entityData.define(DATA_MOOD, 50);
        this.entityData.define(DATA_GENDER, this.random.nextBoolean()); // true = male
        this.entityData.define(DATA_SLEEPING, false);
        this.entityData.define(DATA_ORDER, ORDER_WANDER);
        this.entityData.define(DATA_OWNER, java.util.Optional.empty());
        this.entityData.define(DATA_TAMED, false);
    }

    // ========== Getters/Setters ==========
    
    public int getAgeTicks() { return this.entityData.get(DATA_AGE_TICKS); }
    public void setAgeTicks(int ticks) { this.entityData.set(DATA_AGE_TICKS, ticks); }
    
    public int getHunger() { return this.entityData.get(DATA_HUNGER); }
    public void setHunger(int hunger) { this.entityData.set(DATA_HUNGER, Mth.clamp(hunger, 0, getMaxHunger())); }
    
    public int getMood() { return this.entityData.get(DATA_MOOD); }
    public void setMood(int mood) { this.entityData.set(DATA_MOOD, Mth.clamp(mood, -100, 100)); }
    
    public boolean isMale() { return this.entityData.get(DATA_GENDER); }
    public void setMale(boolean male) { this.entityData.set(DATA_GENDER, male); }
    
    public boolean isSleeping() { return this.entityData.get(DATA_SLEEPING); }
    public void setSleeping(boolean sleeping) { this.entityData.set(DATA_SLEEPING, sleeping); }
    
    public byte getOrder() { return this.entityData.get(DATA_ORDER); }
    public void setOrder(byte order) { this.entityData.set(DATA_ORDER, order); }
    
    public boolean isTamed() { return this.entityData.get(DATA_TAMED); }
    public void setTamed(boolean tamed) { this.entityData.set(DATA_TAMED, tamed); }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNER, java.util.Optional.ofNullable(uuid));
    }

    @Nullable
    @Override
    public Entity getOwner() {
        UUID uuid = this.getOwnerUUID();
        if (uuid != null && this.level() instanceof ServerLevel serverLevel) {
            return serverLevel.getEntity(uuid);
        }
        return null;
    }

    // ========== Life Stage System ==========
    
    public enum LifeStage {
        BABY, TEEN, ADULT
    }

    public LifeStage getLifeStage() {
        int ageDays = getAgeTicks() / TICKS_PER_MC_DAY;
        if (ageDays >= getAdultAgeDays()) return LifeStage.ADULT;
        if (ageDays >= getTeenAgeDays()) return LifeStage.TEEN;
        return LifeStage.BABY;
    }
    
    public boolean isBaby() { return getLifeStage() == LifeStage.BABY; }
    public boolean isTeen() { return getLifeStage() == LifeStage.TEEN; }
    public boolean isAdult() { return getLifeStage() == LifeStage.ADULT; }

    /** Returns the growth progress from 0.0 (baby) to 1.0 (adult) */
    public float getGrowthProgress() {
        return Mth.clamp((float) getAgeTicks() / (float) (getAdultAgeDays() * TICKS_PER_MC_DAY), 0.0f, 1.0f);
    }

    /** Current scale based on age */
    public float getCurrentScale() {
        float progress = getGrowthProgress();
        return Mth.lerp(progress, getScaleBase(), getScaleMax());
    }

    // ========== Mood System ==========
    
    public enum MoodType {
        HAPPY(25),       // Mood > 25
        CONTENT(-25),    // Mood between -25 and 25
        ANGRY(-100);     // Mood < -25

        public final int threshold;
        MoodType(int threshold) { this.threshold = threshold; }
    }

    public MoodType getMoodType() {
        int mood = getMood();
        if (mood > MoodType.HAPPY.threshold) return MoodType.HAPPY;
        if (mood > MoodType.ANGRY.threshold) return MoodType.CONTENT;
        return MoodType.ANGRY;
    }

    private void tickMood() {
        if (this.tickCount % MOOD_TICK_INTERVAL != 0) return;
        
        int moodChange = 0;
        
        // Hunger affects mood
        float hungerPercent = (float) getHunger() / getMaxHunger();
        if (hungerPercent > 0.8f) moodChange += 2;
        else if (hungerPercent > 0.5f) moodChange += 1;
        else if (hungerPercent < 0.2f) moodChange -= 3;
        else if (hungerPercent < 0.4f) moodChange -= 1;
        
        // Being tamed improves mood
        if (isTamed()) moodChange += 1;
        
        // Owner nearby improves mood
        if (isTamed() && getOwner() != null && distanceTo(getOwner()) < 16) {
            moodChange += 1;
        }
        
        // Being hurt decreases mood
        if (getHealth() < getMaxHealth() * 0.5f) moodChange -= 2;
        
        setMood(getMood() + moodChange);
    }

    // ========== Hunger System ==========
    
    private void tickHunger() {
        if (this.tickCount % HUNGER_TICK_INTERVAL != 0) return;
        
        // Decrease hunger over time
        int currentHunger = getHunger();
        if (currentHunger > 0) {
            setHunger(currentHunger - 1);
        }
        
        // Track starvation
        if (currentHunger <= 0) {
            starvationTimer++;
            // Damage from starvation every 5 seconds of being at 0 hunger
            if (starvationTimer % 5 == 0) {
                this.hurt(this.damageSources().starve(), 1.0f);
            }
        } else {
            starvationTimer = 0;
        }

        // Natural healing when well-fed
        if (currentHunger > getMaxHunger() * 0.8f && getHealth() < getMaxHealth()) {
            this.heal(1.0f);
        }
    }

    public boolean isHungry() {
        return getHunger() < getMaxHunger() * 0.5f;
    }

    public boolean isStarving() {
        return getHunger() <= 0;
    }

    public void feed(int amount) {
        setHunger(getHunger() + amount);
        ticksSinceLastMeal = 0;
    }

    // ========== Sleep System ==========
    
    private void tickSleep() {
        if (this.level().isClientSide()) return;
        
        long dayTime = this.level().getDayTime() % TICKS_PER_MC_DAY;
        boolean isNight = dayTime > 13000 && dayTime < 23000;
        
        // Diurnal creatures sleep at night
        if (!isNight && !isSleeping()) return;
        if (isNight && !isSleeping() && !isInWaterOrBubble()) return;
        
        // Simple sleep logic - diurnal creatures sleep at night in safe positions
        if (isNight && getHunger() > getMaxHunger() * 0.3f && !this.isInCombat()) {
            setSleeping(true);
        } else if (!isNight || this.isInCombat()) {
            setSleeping(false);
        }
    }

    private boolean isInCombat() {
        return this.getLastHurtByMob() != null && 
               this.tickCount - this.getLastHurtByMobTimestamp() < 200;
    }

    // ========== Growth System ==========
    
    private void tickGrowth() {
        if (this.level().isClientSide()) return;
        
        setAgeTicks(getAgeTicks() + 1);
        
        // Update attributes based on growth
        if (this.tickCount % 200 == 0) {
            updateStatsForAge();
        }
    }

    protected void updateStatsForAge() {
        float progress = getGrowthProgress();
        
        double health = Mth.lerp(progress, getHealthBase(), getHealthMax());
        double damage = Mth.lerp(progress, getDamageBase(), getDamageMax());
        double speed = Mth.lerp(progress, getSpeedBase(), getSpeedMax());
        
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        
        // Heal to full when growing up
        if (getHealth() > getMaxHealth()) {
            setHealth((float) getMaxHealth());
        }
    }

    // ========== Interaction ==========
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Taming with fish (babies) or nautilus shell (adults)
        if (!isTamed()) {
            if (isBaby() && isFoodItem(stack)) {
                return tryTame(player, stack);
            }
            if (isAdult() && stack.is(Items.NAUTILUS_SHELL)) {
                return tryTame(player, stack);
            }
        }

        // Feeding when tamed
        if (isTamed() && isFoodItem(stack) && isHungry()) {
            feed(getFoodValue(stack));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }

        // Order cycling when tamed (empty hand + sneak)
        if (isTamed() && isOwnedBy(player) && player.isShiftKeyDown() && stack.isEmpty()) {
            cycleOrder();
            String orderName = switch (getOrder()) {
                case ORDER_WANDER -> "Wander";
                case ORDER_FOLLOW -> "Follow";
                case ORDER_STAY -> "Stay";
                default -> "Unknown";
            };
            player.displayClientMessage(
                    Component.literal(getEntityName() + " is set to: " + orderName), true);
            return InteractionResult.SUCCESS;
        }

        // Riding (for rideable species when adult + tamed)
        if (isTamed() && isOwnedBy(player) && canBeRidden() && isAdult() && !player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    private InteractionResult tryTame(Player player, ItemStack stack) {
        if (!this.level().isClientSide()) {
            if (this.random.nextInt(3) == 0) {
                setTamed(true);
                setOwnerUUID(player.getUUID());
                setMood(50);
                this.level().broadcastEntityEvent(this, (byte) 7); // heart particles
                player.displayClientMessage(
                        Component.literal(getEntityName() + " has been tamed!"), true);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6); // smoke particles
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void cycleOrder() {
        byte current = getOrder();
        setOrder((byte) ((current + 1) % 3));
    }

    public boolean isOwnedBy(Entity entity) {
        return entity.getUUID().equals(getOwnerUUID());
    }

    public boolean isFoodItem(ItemStack stack) {
        return stack.is(Items.COD) || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH) ||
               stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON) ||
               stack.is(Items.PUFFERFISH) || stack.is(Items.KELP) || stack.is(Items.DRIED_KELP);
    }

    public int getFoodValue(ItemStack stack) {
        if (stack.is(Items.COD) || stack.is(Items.SALMON)) return 10;
        if (stack.is(Items.TROPICAL_FISH) || stack.is(Items.PUFFERFISH)) return 8;
        if (stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON)) return 15;
        if (stack.is(Items.KELP) || stack.is(Items.DRIED_KELP)) return 5;
        return 0;
    }

    // ========== Navigation ==========
    
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            // F&A Revival style: small moveRelative multiplier, motion driven by AI goals
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            // Uniform drag on all axes (same as F&A Revival's 0.9)
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            // No gravity pull here — vertical movement is controlled by AI goals directly
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // ========== Main Tick ==========
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide()) {
            tickGrowth();
            tickHunger();
            tickMood();
            tickSleep();
        }
        
        // Update dynamic scale
        currentScale = getCurrentScale();
    }

    @Override
    public float getScale() {
        return getCurrentScale();
    }

    // ========== Save/Load ==========
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AgeTicks", getAgeTicks());
        tag.putInt("Hunger", getHunger());
        tag.putInt("Mood", getMood());
        tag.putBoolean("Male", isMale());
        tag.putBoolean("Sleeping", isSleeping());
        tag.putByte("Order", getOrder());
        tag.putBoolean("Tamed", isTamed());
        if (getOwnerUUID() != null) {
            tag.putUUID("OwnerUUID", getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAgeTicks(tag.getInt("AgeTicks"));
        setHunger(tag.getInt("Hunger"));
        setMood(tag.getInt("Mood"));
        setMale(tag.getBoolean("Male"));
        setSleeping(tag.getBoolean("Sleeping"));
        setOrder(tag.getByte("Order"));
        setTamed(tag.getBoolean("Tamed"));
        if (tag.hasUUID("OwnerUUID")) {
            setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        updateStatsForAge();
    }

    // ========== Spawn Data ==========
    
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        groupData = super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
        // Random starting age between baby and adult
        if (spawnType != MobSpawnType.BREEDING) {
            int randomAge = this.random.nextInt(getAdultAgeDays() * TICKS_PER_MC_DAY);
            setAgeTicks(randomAge);
        }
        setHunger(getMaxHunger() / 2 + this.random.nextInt(getMaxHunger() / 2));
        updateStatsForAge();
        return groupData;
    }

    // ========== Sounds ==========
    
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.DOLPHIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    // ========== GeckoLib Animation ==========
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, state -> {
            if (isSleeping()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("animation." + getEntityName() + ".sleep"));
            }
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("animation." + getEntityName() + ".swim"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation." + getEntityName() + ".idle"));
        }));

        controllers.add(new AnimationController<>(this, "attack", 5, state -> {
            if (this.swinging) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("animation." + getEntityName() + ".attack"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ========== Texture Helper ==========
    
    public ResourceLocation getTextureLocation() {
        String stage;
        if (isBaby()) stage = "baby";
        else if (isTeen()) stage = "teen";
        else stage = isMale() ? "male" : "female";
        
        String suffix = isSleeping() ? "_sleeping" : "";
        
        return new ResourceLocation(AquaticLegacyMod.MOD_ID,
                "textures/entity/" + getEntityName() + "/" + getEntityName() + "_" + stage + suffix + ".png");
    }
}
