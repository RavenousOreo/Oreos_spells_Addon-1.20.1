package net.oreo.oreos_spells_addon.effect.ButterflyDamageBuff;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.capabilities.magic.OreoSyncedSpellData;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;
import net.oreo.oreos_spells_addon.registries.OreoParticleRegistry;

import java.util.UUID;

public class ButterflyBuffEffect extends MagicMobEffect {
    public static final float ATTACK_DAMAGE_PER_LEVEL = -0.15f;
    public static final float SPELL_POWER_PER_LEVEL = -0.05f;
    public static final UUID ATTACK_MODIFIER_ID = UUID.fromString("d8447629-b72c-40d0-92d4-56c75f785330");
    public static final UUID SPELL_POWER_ID = UUID.fromString("6cfbd4be-04f8-4cbc-ab76-f2d758da759e");

    public ButterflyBuffEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ATTACK_MODIFIER_ID.toString(),
                ButterflyBuffEffect.ATTACK_DAMAGE_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                AttributeRegistry.SPELL_POWER.get(),
                SPELL_POWER_ID.toString(),
                ButterflyBuffEffect.SPELL_POWER_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public void removeAttributeModifiers(LivingEntity plivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(plivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(plivingEntity).getSyncedData().removeEffects(OreoSyncedSpellData.BUTTERFLYDEBUFF);
    }

    @Override
    public void addAttributeModifiers(LivingEntity plivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(plivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(plivingEntity).getSyncedData().removeEffects(OreoSyncedSpellData.BUTTERFLYDEBUFF);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        LivingEntity attacker = ButterflyEffectEventHandler.consumeDodgeTarget(entity);
        if (attacker != null) {
            double angle = entity.getRandom().nextDouble() * 2 * Math.PI;
            double radius = 2.5 + amplifier;
            double x = attacker.getX() + Math.cos(angle) * radius;
            double z = attacker.getZ() + Math.sin(angle) * radius;
            double y = attacker.getY();
            MobEffectInstance current = entity.getEffect(this);
            int remainingDuration = current != null ? current.getDuration() : 100;

            entity.teleportTo(x, y ,z);

            entity.removeEffect(this);
            entity.addEffect(new MobEffectInstance(
                    OreoMobEffectRegistry.BUTTERFLYBUFF.get(),
                    remainingDuration,
                    amplifier,
                    false,
                    false,
                    true
            ));
            entity.level().playSound(null, entity.blockPosition(),
                    SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 0.7f, 1.5f);

            Vec3 explosionCenter = entity.position().add(0, 1.0, 0); // center on torso

            for (int i = 0; i < 40; i++) {
                double theta = entity.getRandom().nextDouble() * 2 * Math.PI;
                double phi = entity.getRandom().nextDouble() * Math.PI;

                double dx = Math.sin(phi) * Math.cos(theta);
                double dy = Math.cos(phi);
                double dz = Math.sin(phi) * Math.sin(theta);

                dx *= 2.5;
                dy *= 2.5;
                dz *= 2.5;

                serverLevel.sendParticles(
                        OreoParticleRegistry.BLUE_BUTTERFLY_PARTICLE.get(),
                        explosionCenter.x, explosionCenter.y, explosionCenter.z,
                        1,
                        dx, dy, dz,
                        5
                );
            }
        }

        double minY = entity.getY();
        double maxY = entity.getY() + entity.getBbHeight() * 0.75;

        int particleCount = 2 + amplifier;

        for (int i = 0; i < particleCount; i++) {
            // Random angle around the entity
            double angle = Math.random() * 2 * Math.PI;
            double speed = 0.35 + Math.random() * 0.1;

            // Motion away from center
            double dx = Math.cos(angle) * speed;
            double dz = Math.sin(angle) * speed;
            double dy = 0.5 + Math.random() * 0.5;

            // Spawn at random height along upper body
            double x = entity.getX();
            double y = minY + Math.random() * (maxY - minY);
            double z = entity.getZ();

            serverLevel.sendParticles(
                    OreoParticleRegistry.BLUE_BUTTERFLY_PARTICLE.get(),
                    x, y, z,    // spawn position
                    1,          // count
                    dx, dy, dz, // velocity (applied once per particle)
                    1           // speed scaling (0 means use exact dx/dy/dz)
            );
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
