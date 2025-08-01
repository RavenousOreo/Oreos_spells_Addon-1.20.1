package net.oreo.oreos_spells_addon.entity.spells.Cleave;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoParticleRegistry;
import net.oreo.oreos_spells_addon.registries.OreoSpellRegistry;

import java.util.Optional;
import java.util.UUID;

public class CleaveEntity extends AoeEntity {

    private LivingEntity attachedTarget;
    private UUID attachedTargetUUID;

    public CleaveEntity(EntityType<? extends AoeEntity> entityType, Level level) {
        super(entityType, level);
        this.setRadius(0.6f); // Tighten to single target
        this.setDuration(15); // Keep it short
        this.reapplicationDelay = 1000; // Skip repeated effects
        this.setCircular();
    }

    private int spellLevel;

    public void setSpellLevel(int level) {
        this.spellLevel = level;
    }

    public int getSpellLevel() {
        return this.spellLevel;
    }


    public int getSlashCount() {
        return Math.min(10, 3 + this.getSpellLevel()); // e.g., 3 at level 0, scales up
    }

    private int ticksBetweenSlashes = 2;
    private int slashCooldown = 0;
    private int slashesRemaining = 0;
    private LivingEntity caster = null;
    private float damagePerHit;

    public void setDamagePerHit(float damage) {
        this.damagePerHit = damage;
    }

    public float getDamagePerHit() {
        return this.damagePerHit;
    }

    public void setCaster(LivingEntity caster) {
        this.caster = caster;
    }


    public void attachTo(LivingEntity target) {
        this.attachedTarget = target;
        this.attachedTargetUUID = target.getUUID();
        this.setPos(target.position().add(0, target.getBbHeight() * 0.5, 0));
        this.slashesRemaining = getSlashCount(); // e.g., 3 + level

    }




    @Override
    public void tick() {
        super.tick();

        if (attachedTarget == null && attachedTargetUUID != null && level() instanceof ServerLevel serverLevel) {
            attachedTarget = (LivingEntity) serverLevel.getEntity(attachedTargetUUID);
        }

        if (attachedTarget != null && attachedTarget.isAlive()) {
            Vec3 followPos = attachedTarget.position().add(0, attachedTarget.getBbHeight() * 0.5, 0);
            this.setPos(followPos);

            // Slash handling
            if (!level().isClientSide && slashesRemaining > 0) {
                if (slashCooldown-- <= 0) {
                    DamageSources.applyDamage(attachedTarget, getDamagePerHit(),
                            OreoSpellRegistry.CleaveSpell.get().getDamageSource(this, caster));

                    MagicManager.spawnParticles(
                            this.level(), ParticleHelper.BLOOD,
                            this.getX(), this.getY(), this.getZ(),
                            15, 0.4, 0.5, 0.4, 0.1, true
                    );

                    if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                OreoParticleRegistry.CLEAVE_PARTICLE.get(),
                                this.getX(),
                                this.getY() + attachedTarget.getBbHeight() * 0.25,
                                this.getZ(),
                                8, // count
                                0.05, 0.4, 0.05, // x,y,z offset for spread
                                0 // speed
                        );

                        serverLevel.sendParticles(
                                OreoParticleRegistry.CLEAVE_PARTICLE_2.get(),
                                this.getX(),
                                this.getY() + attachedTarget.getBbHeight() * 0.25,
                                this.getZ(),
                                3,
                                0.05, 0.4, 0.05,
                                0
                        );

                        serverLevel.sendParticles(
                                OreoParticleRegistry.CLEAVE_PARTICLE_4.get(),
                                this.getX(),
                                this.getY() + attachedTarget.getBbHeight() * 0.25,
                                this.getZ(),
                                2,
                                0.05, 0.4, 0.05,
                                0
                        );
                    }


                    float percent = 0.025f; // 2.5% max HP per hit
                    float scaledDamage = attachedTarget.getMaxHealth() * percent;

                    DamageSources.applyDamage(attachedTarget, scaledDamage,
                            OreoSpellRegistry.CleaveSpell.get().getDamageSource(this, caster));

                    level().playSound(null, attachedTarget.getX(), attachedTarget.getY(), attachedTarget.getZ(),
                            SoundRegistry.BLOOD_NEEDLE_IMPACT.get(),
                            attachedTarget.getSoundSource(), 0.8f, 2.0f);


                    // Reset i-frames to allow another hit
                    attachedTarget.invulnerableTime = 0;

                    // Optionally trigger animation or sound
                    level().broadcastEntityEvent(this, (byte) 4); // swing animation

                    slashesRemaining--;
                    slashCooldown = ticksBetweenSlashes;

                    if (slashesRemaining <= 0) {
                        discard(); // Done with slashes
                    }
                }
            }

        } else if (!level().isClientSide) {
            this.discard(); // Target dead or missing
        }
    }



    @Override
    public void applyEffect(LivingEntity target) {
        if (this.attachedTarget == null) {
            this.attachedTarget = target;
            this.attachedTargetUUID = target.getUUID();
            this.setPos(target.position().add(0, target.getBbHeight() * 0.5, 0));
        }
    }

    @Override
    public float getParticleCount() {
        return 20f;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.of(OreoParticleRegistry.CLEAVE_PARTICLE.get());
    }

    @Override
    public void ambientParticles() {
        if (!this.level().isClientSide) {
            MagicManager.spawnParticles(
                    this.level(), ParticleHelper.BLOOD,
                    this.getX(), this.getY(), this.getZ(),
                    10, 0.1, 0.2, 0.1, 0.03, true
            );
        }
    }

}
