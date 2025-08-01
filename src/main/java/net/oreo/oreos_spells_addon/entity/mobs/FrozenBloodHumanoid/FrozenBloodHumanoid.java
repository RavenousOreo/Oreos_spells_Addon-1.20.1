package net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

public class FrozenBloodHumanoid extends LivingEntity {
    private static final EntityDataAccessor<Boolean> DATA_IS_BABY = SynchedEntityData.defineId(net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_SITTING = SynchedEntityData.defineId(net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_LIMB_SWING = SynchedEntityData.defineId(net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_LIMB_SWING_AMOUNT = SynchedEntityData.defineId(net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ATTACK_TIME = SynchedEntityData.defineId(net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid.class, EntityDataSerializers.FLOAT);

    private int deathTimer = -1;
    private UUID summonerUUID;

    public FrozenBloodHumanoid(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FrozenBloodHumanoid(Level level, LivingEntity sourceEntity) {
        this(OreoEntityRegistry.FROZEN_BLOOD_HUMANOID.get(), level);
        this.moveTo(sourceEntity.getX(), sourceEntity.getY(), sourceEntity.getZ(), sourceEntity.getYRot(), sourceEntity.getXRot());

        this.entityData.set(DATA_IS_BABY, sourceEntity.isBaby());
        this.entityData.set(DATA_IS_SITTING, sourceEntity.isPassenger());
        this.entityData.set(DATA_LIMB_SWING, sourceEntity.walkAnimation.speed());
        this.entityData.set(DATA_LIMB_SWING_AMOUNT, sourceEntity.walkAnimation.position());
        this.entityData.set(DATA_ATTACK_TIME, sourceEntity.attackAnim);

        this.setPose(sourceEntity.getPose());
        this.setYBodyRot(sourceEntity.yBodyRot);
        this.setYHeadRot(sourceEntity.getYHeadRot());
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;

        if (sourceEntity instanceof Player player) {
            this.setCustomName(player.getDisplayName());
            this.setCustomNameVisible(true);
        }

        this.summonerUUID = sourceEntity.getUUID();
    }

    public void setDeathTimer(int ticks) {
        this.deathTimer = ticks;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_BABY, false);
        this.entityData.define(DATA_IS_SITTING, false);
        this.entityData.define(DATA_LIMB_SWING, 0f);
        this.entityData.define(DATA_LIMB_SWING_AMOUNT, 0f);
        this.entityData.define(DATA_ATTACK_TIME, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (deathTimer > 0) {
            deathTimer--;
        }
        if (deathTimer == 0) {
            this.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false; // Completely immune
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList(); // Safe no-op
    }


    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public boolean isSitting() {
        return this.entityData.get(DATA_IS_SITTING);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(DATA_IS_BABY);
    }

    public float getLimbSwing() {
        return this.entityData.get(DATA_LIMB_SWING);
    }

    public float getLimbSwingAmount() {
        return this.entityData.get(DATA_LIMB_SWING_AMOUNT);
    }

    public float getAttacktime() {
        return this.entityData.get(DATA_ATTACK_TIME);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100.0D);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        // No-op; statue can't equip items
    }


}
