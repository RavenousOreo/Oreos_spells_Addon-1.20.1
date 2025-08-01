package net.oreo.oreos_spells_addon.entity.spells.FlameAuraEntity;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class FlameAuraEntity extends AoeEntity {
    private LivingEntity caster;
    private int amplifier;
    private TargetedAreaEntity visualRing;

    public FlameAuraEntity(EntityType<? extends AoeEntity> type, Level level) {
        super(type, level);
        this.caster = null;
        this.setCircular();
    }

    private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(FlameAuraEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DURATION, 600); // default duration
    }

    @Override
    public void setDuration(int duration) {
        super.setDuration(duration);
        if (!level().isClientSide) {
            this.entityData.set(DATA_DURATION, duration);
        }
    }

    public int getDurationSynced() {
        return this.entityData.get(DATA_DURATION);
    }



    public FlameAuraEntity(Level level, LivingEntity caster, float radius, float damage, int duration, int amplifier) {
        this(OreoEntityRegistry.FLAMEAURAENTITY.get(), level);
        this.caster = caster;
        this.setRadius(radius);
        this.setDamage(damage);
        this.setDuration(duration);
        this.setPos(caster.getX(), caster.getY() - 0.1, caster.getZ());
        this.setOwner(caster);
        this.setAmplifier(amplifier);
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public FlameAuraEntity(Level level, LivingEntity caster) {
        super(OreoEntityRegistry.FLAMEAURAENTITY.get(), level);
        this.caster = caster;
    }

    private UUID casterUUID;

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AuraDuration", getDurationSynced());
        if (caster != null) {
            tag.putUUID("CasterUUID", caster.getUUID());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AuraDuration")) {
            this.entityData.set(DATA_DURATION, tag.getInt("AuraDuration"));
        }
        if (tag.hasUUID("CasterUUID")) {
            this.casterUUID = tag.getUUID("CasterUUID");
        }
    }


    @Override
    public void tick() {
        this.baseTick();

        if (level().isClientSide) {
            this.ambientParticles();
            return;
        }

        if (level() instanceof ServerLevel serverLevel && !level().isClientSide && visualRing == null) {
            TargetedAreaEntity ring = TargetedAreaEntity.createTargetAreaEntity(
                    serverLevel,
                    this.position(),
                    getRadius(),
                    0xFFFF0000,
                    this
            );
            ring.setDuration(this.getDurationSynced());
            this.visualRing = ring;
            level().addFreshEntity(ring);
        }

        LivingEntity caster = (LivingEntity) getOwner();
        if (caster == null || !caster.isAlive()) {
            discard();
            return;
        }

        // Move with caster
        this.setPos(caster.getX(), caster.getY() - 0.1, caster.getZ());
        if (visualRing != null && !visualRing.isRemoved()) {
            visualRing.setPos(this.getX(), this.getY(), this.getZ());
        }

        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox())) {
            if (!target.isAlive()) continue;

            if (target == caster || target.isAlliedTo(caster)) {
                target.addEffect(new MobEffectInstance(
                        OreoMobEffectRegistry.FLAMEAURABUFF.get(),
                        20,
                        amplifier,
                        false, false, true
                ));
            } else {
                target.hurt(caster.damageSources().magic(), getDamage());
                target.addEffect(new MobEffectInstance(
                        OreoMobEffectRegistry.FLAMEAURADEBUFF.get(),
                        20,
                        amplifier,
                        false, false, true
                ));
            }

        }

        if (this.tickCount > getDurationSynced()) {
            this.discard();
        }
    }

    public void applyEffect(LivingEntity target) {
        LivingEntity caster = (LivingEntity) getOwner();
        if (caster == null || !caster.isAlive()) return;

        if (target.isAlliedTo(caster)) {
            target.addEffect(new MobEffectInstance(
                    OreoMobEffectRegistry.FLAMEAURABUFF.get(),
                    getEffectDuration(),
                    amplifier,
                    false, false, true
            ));
        } else {
            target.hurt(caster.damageSources().magic(), getDamage());
            target.addEffect(new MobEffectInstance(
                    OreoMobEffectRegistry.FLAMEAURADEBUFF.get(),
                    getEffectDuration(),
                    amplifier,
                    false, false, true
            ));
        }
    }

    public void ambientParticles() {
        if (!level().isClientSide)
            return;

        getParticle().ifPresent((particle) -> {
            float f = getParticleCount();
            f = Mth.clamp(f * getRadius(), f / 4, f * 10);
            for (int i = 0; i < f; i++) {
                if (f - i < 1 && random.nextFloat() > f - i)
                    return;
                var r = getRadius();
                Vec3 pos;
                if (isCircular()) {
                    var distance = r * (1 - this.random.nextFloat() * this.random.nextFloat());
                    var theta = this.random.nextFloat() * 6.282f; // two pi :nerd:
                    pos = new Vec3(
                            distance * Mth.cos(theta),
                            .2f,
                            distance * Mth.sin(theta)
                    );
                } else {
                    pos = new Vec3(
                            Utils.getRandomScaled(r * .85f),
                            .2f,
                            Utils.getRandomScaled(r * .85f)
                    );
                }
                Vec3 motion = new Vec3(
                        Utils.getRandomScaled(.03f),
                        this.random.nextDouble() * .01f,
                        Utils.getRandomScaled(.03f)
                ).scale(this.getParticleSpeedModifier());

                level().addParticle(particle, getX() + pos.x, getY() + pos.y + particleYOffset(), getZ() + pos.z, motion.x, motion.y, motion.z);
            }
        });

    }


    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.of(ParticleHelper.FIRE);
    }

    @Override
    public float getParticleCount() {
        return 2f;
    }

}
