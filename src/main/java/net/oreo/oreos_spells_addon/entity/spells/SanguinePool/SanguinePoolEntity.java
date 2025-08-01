package net.oreo.oreos_spells_addon.entity.spells.SanguinePool;

import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;

import java.util.Optional;
import java.util.UUID;

public class SanguinePoolEntity extends AoeEntity {
    private LivingEntity caster;

    public SanguinePoolEntity(EntityType<? extends AoeEntity> type, Level level) {
        super(type, level);
        this.caster = null;
        this.setCircular(); // Use circular AOE checks
    }

    public SanguinePoolEntity(Level level, LivingEntity caster, float radius, float damage, int duration) {
        this(OreoEntityRegistry.SANGUINE_POOL_ENTITY.get(), level);
        this.caster = caster;
        this.setRadius(radius);
        this.setDamage(damage);
        this.setDuration(duration);
        this.setPos(caster.getX(), caster.getY() - 0.1, caster.getZ());
        this.setOwner(caster);
    }

    public SanguinePoolEntity(Level level, LivingEntity caster) {
        super(OreoEntityRegistry.SANGUINE_POOL_ENTITY.get(), level);
        this.caster = caster;
    }

    private UUID casterUUID;

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (caster != null) {
            tag.putUUID("CasterUUID", caster.getUUID());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("CasterUUID")) {
            this.casterUUID = tag.getUUID("CasterUUID");
        }
    }


    @Override
    public void tick() {
        if (caster == null && casterUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity e = serverLevel.getEntity(casterUUID);
            if (e instanceof LivingEntity living) {
                this.caster = living;
            }
        }

        if (!level().isClientSide && caster != null && caster.isAlive()) {
            this.setPos(caster.getX(), caster.getY() + 0.1, caster.getZ());

            float growShrinkRatio = 0.25f;
            int growTicks = (int)(duration * growShrinkRatio);
            int shrinkStart = duration - growTicks;

            float targetRadius = 5.0f;
            float currentRadius;

            if (tickCount < growTicks) {
                currentRadius = Mth.lerp(tickCount / (float) growTicks, 1.0f, targetRadius);
            } else if (tickCount >= shrinkStart) {
                currentRadius = Mth.lerp((tickCount - shrinkStart) / (float) growTicks, targetRadius, 1.0f);
            } else {
                currentRadius = targetRadius;
            }

            this.setRadius(currentRadius);
        }

        super.tick();
    }


    @Override
    public void applyEffect(LivingEntity target) {
        float damage = this.getDamage();
        if (caster != null && caster.isAlive()) {
            boolean damaged = target.hurt(caster.damageSources().magic(), damage);
            if (damaged) {
                float healAmount = damage * 0.05f;
                caster.heal(healAmount);
            }
        }
    }


    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.of(ParticleHelper.BLOOD);
    }

    @Override
    public float getParticleCount() {
        return 5f; // Very dense blood effect
    }
}
