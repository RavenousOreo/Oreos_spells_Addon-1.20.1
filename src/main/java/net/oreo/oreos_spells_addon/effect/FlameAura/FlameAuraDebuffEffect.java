package net.oreo.oreos_spells_addon.effect.FlameAura;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.oreo.oreos_spells_addon.capabilities.magic.OreoSyncedSpellData;

import java.util.UUID;

public class FlameAuraDebuffEffect extends MagicMobEffect {
    public static final float FIRE_RES_SHRED_PER_LEVEL = -0.05f;
    public static final UUID FIRE_RES_ID = UUID.fromString("5666ab47-4b6a-4039-967b-e1fca3e61b2c");

    public FlameAuraDebuffEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                AttributeRegistry.FIRE_MAGIC_RESIST.get(),
                FIRE_RES_ID.toString(),
                FlameAuraDebuffEffect.FIRE_RES_SHRED_PER_LEVEL,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(livingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(livingEntity).getSyncedData().removeEffects(OreoSyncedSpellData.FLAMEAURADEBUFF);
    }

    @Override
    public void addAttributeModifiers(LivingEntity plivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(plivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(plivingEntity).getSyncedData().addEffects(OreoSyncedSpellData.FLAMEAURADEBUFF);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            double minY = entity.getY() + entity.getBbHeight() * 0.25; // start at chest height
            double maxY = entity.getY() + entity.getBbHeight() * 0.75;

            int particleCount = 1 + amplifier; // keep it minimal for "sizzle"

            for (int i = 0; i < particleCount; i++) {
                // Small horizontal jitter
                double xOffset = (Math.random() - 0.5) * 0.3; // ±0.15
                double zOffset = (Math.random() - 0.5) * 0.3;

                double x = entity.getX() + xOffset;
                double y = minY + Math.random() * (maxY - minY); // chest-to-neck height
                double z = entity.getZ() + zOffset;

                // Very gentle upward motion
                double dx = 0;
                double dy = 0.03 + Math.random() * 0.02;
                double dz = 0;

                serverLevel.sendParticles(
                        ParticleHelper.EMBERS,
                        x, y, z,
                        1,      // 1 particle at a time
                        dx, dy, dz,
                        0       // speed = 0 means use raw motion vector
                );
            }
        }
    }


    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
