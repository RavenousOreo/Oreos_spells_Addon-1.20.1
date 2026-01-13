package net.oreo.oreos_spells_addon.effect.LightningAura;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class LightningAuraDebuffEffect extends MagicMobEffect implements ISyncedMobEffect {
    public static final float LIGHTNING_RES_SHRED_PER_LEVEL = -0.05f;
    public static final UUID LIGHTNING_RES_ID = UUID.fromString("2553c978-03ee-462c-b514-a7b62ad5eb73");

    public LightningAuraDebuffEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                AttributeRegistry.LIGHTNING_MAGIC_RESIST.get(),
                LIGHTNING_RES_ID.toString(),
                LightningAuraDebuffEffect.LIGHTNING_RES_SHRED_PER_LEVEL,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            double minY = entity.getY() + entity.getBbHeight() * 0.25;
            double maxY = entity.getY() + entity.getBbHeight() * 0.75;

            int particleCount = 1 + amplifier;

            for (int i = 0; i < particleCount; i++) {
                double xOffset = (Math.random() - 0.5) * 0.3;
                double zOffset = (Math.random() - 0.5) * 0.3;

                double x = entity.getX() + xOffset;
                double y = minY + Math.random() * (maxY - minY);
                double z = entity.getZ() + zOffset;

                double dx = 0;
                double dy = 0.03 + Math.random() * 0.02;
                double dz = 0;

                serverLevel.sendParticles(
                        ParticleHelper.ELECTRICITY,
                        x, y, z,
                        1,
                        dx, dy, dz,
                        0
                );
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
