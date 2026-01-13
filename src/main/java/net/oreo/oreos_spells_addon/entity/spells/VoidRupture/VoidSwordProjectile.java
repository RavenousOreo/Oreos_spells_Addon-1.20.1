package net.oreo.oreos_spells_addon.entity.spells.VoidRupture;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;

import java.util.Optional;
import java.util.function.Supplier;

public class VoidSwordProjectile extends AbstractMagicProjectile {

    private boolean stuckInGround = false;
    private int stuckTicks = 0;
    private float stuckYaw;
    private float stuckPitch;


    public VoidSwordProjectile(EntityType<? extends AbstractMagicProjectile> type, Level level) {
        super(type, level);
    }

    public VoidSwordProjectile(Level level, LivingEntity owner) {
        super(OreoEntityRegistry.VOIDSWORDPROJECTILE.get(), level);
        this.setOwner(owner);
        this.setNoGravity(true);
        this.setDeltaMovement(0, -getSpeed(), 0);
        this.hasImpulse = true;
        this.setRandomRotations(level.random.nextFloat() * 360f, level.random.nextFloat() * 360f);

    }

    public boolean isStuckInGround() {
        return stuckInGround;
    }

    public float getStuckYaw() {
        return stuckYaw;
    }

    public float getStuckPitch() {
        return stuckPitch;
    }

    private static final EntityDataAccessor<Float> ROT_X = SynchedEntityData.defineId(VoidSwordProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROT_Z = SynchedEntityData.defineId(VoidSwordProjectile.class, EntityDataSerializers.FLOAT);

    public float getRandomZRotation() {
        return this.entityData.get(ROT_Z);
    }

    public float getRandomXRotation() {
        return this.entityData.get(ROT_X);
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide && result instanceof BlockHitResult) {
            Vec3 hitPos = result.getLocation();
            this.setPos(hitPos.x, hitPos.y + 0.01, hitPos.z); // prevent z-fighting
            this.setDeltaMovement(Vec3.ZERO);
            this.setNoGravity(true);
            this.stuckInGround = true;
            this.stuckTicks = 0;
            this.setRandomRotations(this.level().random.nextFloat() * 360f, this.level().random.nextFloat() * 360f);

            level().playSound(
                    null,
                    getX(), getY(), getZ(),
                    SoundRegistry.ENDER_CAST.get(),
                    SoundSource.PLAYERS,
                    1.0f,  // volume
                    1.0f   // pitch
            );


        }
    }






    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        if (stuckInGround) {
            stuckTicks++;

            if (stuckTicks >= 10) {
                Vec3 pos = this.position();
                if (level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 60; i++) {
                        double theta = 2 * Math.PI * level().random.nextDouble();
                        double phi = Math.acos(2 * level().random.nextDouble() - 1);
                        double r = 0.8 + level().random.nextDouble() * 0.6;

                        double x = r * Math.sin(phi) * Math.cos(theta);
                        double y = r * Math.sin(phi) * Math.sin(theta);
                        double z = r * Math.cos(phi);

                    }

                    level().playSound(null, pos.x, pos.y, pos.z,
                            SoundEvents.ENDERMAN_TELEPORT,
                            SoundSource.PLAYERS, 0.1f, 1.0f);
                }

                this.discard();
            }
        }
    }

    public void setRandomRotations(float xRot, float zRot) {
        this.entityData.set(ROT_X, xRot);
        this.entityData.set(ROT_Z, zRot);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ROT_X, 0f);
        this.entityData.define(ROT_Z, 0f);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public void trailParticles() {
        Vec3 vec3 = this.position().subtract(getDeltaMovement());
        this.level().addParticle(ParticleHelper.UNSTABLE_ENDER, vec3.x, vec3.y, vec3.z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.UNSTABLE_ENDER, x, y, z, 75, 1.0, 1.0, 1.0, 2, true);
        MagicManager.spawnParticles(this.level(), ParticleHelper.VOID_TENTACLE_FOG, x, y, z, 75, 1.0, 1.0, 1.0, 1, true);
    }



    @Override
    public float getSpeed() {
        return 4f;
    }

    @Override
    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

}

