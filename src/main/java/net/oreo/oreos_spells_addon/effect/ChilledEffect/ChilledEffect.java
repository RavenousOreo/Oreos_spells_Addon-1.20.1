package net.oreo.oreos_spells_addon.effect.ChilledEffect;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class ChilledEffect extends MagicMobEffect implements ISyncedMobEffect {
    public static final float SLOWNESS_PER_LEVEL = 0.10f;
    public static final float ICE_DAMAGE_TAKEN_PER_LEVEL = 0.15f;
    public static final UUID SLOWNESS_MODIFIER_ID = UUID.fromString("4473ec12-272b-4f11-8006-6d64766c578c");
    public static final UUID ICE_DAMAGE_MODIFIER_ID = UUID.fromString("43ee5659-ee04-42d4-90d3-c724b8353cbf");

    public ChilledEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                SLOWNESS_MODIFIER_ID.toString(),
                ChilledEffect.SLOWNESS_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                AttributeRegistry.ICE_MAGIC_RESIST.get(),
                ICE_DAMAGE_MODIFIER_ID.toString(),
                ChilledEffect.ICE_DAMAGE_TAKEN_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            double radius = 1.0;
            double minY = entity.getY();
            double maxY = entity.getY() + entity.getBbHeight() * 0.75;

            int particleCount = 5 + amplifier; // More with higher spell level

            for (int i = 0; i < particleCount; i++) {
                double angle = Math.random() * 2 * Math.PI;
                double r = Math.random() * radius;
                double x = entity.getX() + Math.cos(angle) * r;
                double y = minY + Math.random() * (maxY - minY);
                double z = entity.getZ() + Math.sin(angle) * r;

                double ySpeed = 0.02 + Math.random() * 0.015;

                serverLevel.sendParticles(
                        ParticleHelper.ICY_FOG,
                        x, y, z,
                        5,
                        0, 0, 0,
                        ySpeed
                );
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;

    }



}
