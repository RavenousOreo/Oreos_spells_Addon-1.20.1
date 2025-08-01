package net.oreo.oreos_spells_addon.entity;

import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;

public class HelixEffectEntity extends Entity {
    private final int duration = 10;              // lasts 0.5 seconds
    private final double radius = 1.2;            // keep close to body
    private final double verticalStep = 0.8;      // 10 ticks * 0.8 = ~8 blocks
    private final int particlesPerTick = 24;      // very dense spiral


    public HelixEffectEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public HelixEffectEntity(Level level, Vec3 origin) {
        this(OreoEntityRegistry.HELIX_EFFECT_ENTITY.get(), level);
        this.setPos(origin);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || tickCount > duration) {
            discard();
            return;
        }

        final double spiralSpeed = 15.0; // degrees per particle
        final double radius = 1.2;
        final double verticalStep = 0.8; // 10 ticks * 0.8 = 8 blocks high

        for (int i = 0; i < particlesPerTick; i++) {
            double angle = (tickCount * particlesPerTick + i) * spiralSpeed;
            double radians = Math.toRadians(angle);

            double offsetX = radius * Math.cos(radians);
            double offsetZ = radius * Math.sin(radians);
            double offsetY = tickCount * verticalStep;

            ((ServerLevel) this.level()).sendParticles(
                    ParticleHelper.BLOOD,
                    getX() + offsetX,
                    getY() + offsetY,
                    getZ() + offsetZ,
                    1, 0.05, 0.0, 0.05, 0.0005
            );
        }

        tickCount++;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}

