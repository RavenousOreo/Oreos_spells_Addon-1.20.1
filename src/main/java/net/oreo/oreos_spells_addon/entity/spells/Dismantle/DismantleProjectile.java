package net.oreo.oreos_spells_addon.entity.spells.Dismantle;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import net.oreo.oreos_spells_addon.registries.OreoParticleRegistry;
import net.oreo.oreos_spells_addon.registries.OreoSpellRegistry;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class DismantleProjectile extends AbstractMagicProjectile {

    private final float randomYaw;
    private final float randomScale;

    public DismantleProjectile(EntityType<? extends DismantleProjectile> type, Level level) {
        super(type, level);
        Random trueRandom = new Random(); // No seed
        this.randomYaw = trueRandom.nextFloat() * 360f;
        this.randomScale = 0.15f + trueRandom.nextFloat() * 1.5f;
        this.setNoGravity(true);
    }

    public float getRandomYaw() {
        return randomYaw;
    }

    public float getRandomScale() {
        return randomScale;
    }


    public DismantleProjectile(Level level, LivingEntity shooter) {
        this(OreoEntityRegistry.DISMANTLE.get(), level);
        setOwner(shooter);
    }

    @Override
    public float getSpeed() {
        return 2.8f;
    }

    @Override
    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            Vec3 start = this.position();
            Vec3 end = start.add(this.getDeltaMovement());
            AABB sweepBox = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0); // wider area

            List<LivingEntity> targets = level().getEntitiesOfClass(
                    LivingEntity.class,
                    sweepBox,
                    (entity) -> entity != this.getOwner() && entity.isAlive() && entity != this.getOwner()

            );

            for (LivingEntity target : targets) {
                this.onHitEntity(new EntityHitResult(target));
                break; // hit only one
            }
        }

        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    OreoParticleRegistry.CLEAVE_PARTICLE_2.get(),
                    this.getX(),
                    this.getY() + 0.15, // Slight vertical lift for visual clarity
                    this.getZ(),
                    1,                 // Number of particles per tick
                    0.02, 0.02, 0.02,  // Random offset in each axis
                    0                  // No extra speed
            );

            serverLevel.sendParticles(
                    OreoParticleRegistry.CLEAVE_PARTICLE_4.get(),
                    this.getX(),
                    this.getY() + 0.15, // Slight vertical lift for visual clarity
                    this.getZ(),
                    1,                 // Number of particles per tick
                    0.02, 0.02, 0.02,  // Random offset in each axis
                    0
            );
        }
    }


    @Override
    public void trailParticles() {

    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }


    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        if (!this.level().isClientSide) {
            DamageSources.applyDamage(hitResult.getEntity(), this.damage,
                    OreoSpellRegistry.DismantleSpell.get().getDamageSource(this, this.getOwner()));

            Vec3 hitPos = hitResult.getEntity().position().add(0, hitResult.getEntity().getBbHeight() / 2, 0);

            MagicManager.spawnParticles(this.level(), ParticleHelper.BLOOD, hitPos.x, hitPos.y, hitPos.z, 40, 0.05, 0.05, 0.05, 0.15, true);
            MagicManager.spawnParticles(this.level(), ParticleHelper.BLOOD, hitPos.x, hitPos.y, hitPos.z, 40, 0.05, 0.05, 0.05, 0.3, false);
        }

        // this runs on both sides
        impactParticles(this.getX(), this.getY(), this.getZ());
    }



    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            this.playSound(SoundRegistry.BLOOD_NEEDLE_IMPACT.get(), 1.0f, 1.5f);
        }

        super.onHit(result);
        impactParticles(this.getX(), this.getY(), this.getZ());
        this.discard();
    }

    public int getAge(){
        return tickCount;
    }


    @Override
    public net.minecraft.world.entity.Pose getPose() {
        return net.minecraft.world.entity.Pose.STANDING; // No custom pose needed
    }

    @Override
    protected AABB makeBoundingBox() {
        Vec3 pos = this.position();
        Vec3 motion = this.getDeltaMovement().normalize();
        Vec3 right = new Vec3(-motion.z, 0, motion.x).normalize();

        double halfWidth = 1.5; // 3 blocks wide
        double height = 1.0;

        Vec3 offset = right.scale(halfWidth);
        Vec3 min = pos.subtract(offset).subtract(0, 0, 0).add(0, -height / 2, 0);
        Vec3 max = pos.add(offset).add(0, height / 2, 0);

        return new AABB(min, max);
    }



}