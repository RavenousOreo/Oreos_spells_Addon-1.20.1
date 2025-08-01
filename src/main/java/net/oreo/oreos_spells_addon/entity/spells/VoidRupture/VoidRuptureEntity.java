package net.oreo.oreos_spells_addon.entity.spells.VoidRupture;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;

import javax.swing.text.html.Option;
import java.util.Optional;

public class VoidRuptureEntity extends AoeEntity {

    private float storedDamage = 0f;
    private int portalSpawnCooldown = 0;
    private int spawnFrequency = 10; // default fallback
    private int pulseCooldown = 10;

    public void setSpawnFrequency(int ticks) {
        this.spawnFrequency = ticks;
    }


    public int getDuration() {
        return this.duration;
    }


    public VoidRuptureEntity(EntityType<? extends AoeEntity> type, Level level) {
        super(type, level);
    }

    public VoidRuptureEntity(Level level, LivingEntity owner, float damage, int lifespanTicks, float radius) {
        super(OreoEntityRegistry.VOIDRUPTUREENTITY.get(), level);
        this.storedDamage = damage;
        this.duration = lifespanTicks;
        this.setRadius(radius);
        this.setNoGravity(true);
        this.setOwner(owner);
    }




    public static Vec3 getRandomPositionInCircle(Level level, Vec3 center, double radius) {
        double angle = level.getRandom().nextDouble() * 2 * Math.PI;
        double distance = Math.sqrt(level.getRandom().nextDouble()) * radius; // √ for uniform distribution
        double xOffset = Math.cos(angle) * distance;
        double zOffset = Math.sin(angle) * distance;
        return center.add(xOffset, 5.0, zOffset); // 5.0 Y-offset to spawn slightly above ground
    }


    @Override
    public float getParticleCount() {
        return 5f;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.of(ParticleHelper.UNSTABLE_ENDER);
    }

    @Override
    public void applyEffect(LivingEntity entity) {
        if (!entity.equals(getOwner())) {
            entity.hurt(level().damageSources().indirectMagic(this, getOwner()), storedDamage);
            entity.invulnerableTime = 0; // Optional: ignore vanilla i-frames
        }
    }




    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            MagicManager.spawnParticles(
                    this.level(),
                    ParticleHelper.COMET_FOG,
                    this.getX(), this.getY() + 5.0, this.getZ(),
                    1,         // count
                    2.5, -.25, 2.5, // spread
                    0.15,        // speed
                    false       // force
            );
        }





        if (this.level().isClientSide) return;

        // Handle pulse damage every pulseCooldown ticks
        if (--pulseCooldown <= 0) {
            pulseCooldown = 10; // Reset cooldown

            for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(getRadius()))) {
                if (!entity.equals(getOwner()) && entity.isAlive()) {
                    entity.hurt(level().damageSources().indirectMagic(this, getOwner()), storedDamage);
                    entity.invulnerableTime = 0;
                }
            }
        }

        // Handle portal visuals
        if (--portalSpawnCooldown <= 0) {
            portalSpawnCooldown = spawnFrequency;

            Vec3 spawnPos = getRandomPositionInCircle(level(), this.position(), this.getRadius());


            VoidPortalEntity portal = new VoidPortalEntity(level(), (LivingEntity) getOwner());
            portal.setPos(spawnPos);
            level().addFreshEntity(portal);
        }

        if (this.tickCount > this.getDuration()) {
            this.discard();
        }
    }



    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("StoredDamage", this.storedDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.storedDamage = tag.getFloat("StoredDamage");
    }

    public void setStoredDamage(float damage) {
        this.storedDamage = damage;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}
