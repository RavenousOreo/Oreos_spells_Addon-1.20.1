package net.oreo.oreos_spells_addon.effect.FlameAura;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.oreo.oreos_spells_addon.capabilities.magic.OreoSyncedSpellData;
import net.oreo.oreos_spells_addon.registries.OreoParticleRegistry;

import java.util.UUID;

public class FlameAuraEffect extends MagicMobEffect {
    public static final float FIRE_SPELL_POWER_PER_LEVEL = 0.25f;
    public static final float FIRE_SPELL_RES_PER_LEVEL = 0.15f;
    public static final float MANA_REGEN_PER_LEVEL = 0.10f;
    public static final float MANA_DEBUFF_PER_LEVEL = -0.17f;
    public static final UUID FIRE_POWER_ID = UUID.fromString("70ef574e-9935-4d46-a795-882df2496380");
    public static final UUID FIRE_RES_ID = UUID.fromString("494075bf-ecc2-415e-acb0-6ea806545987");
    public static final UUID MANA_REGEN_ID = UUID.fromString("c3a4882e-4496-413b-ab63-143dbbf4e12f");
    public static final UUID MANA_DEBUFF_ID = UUID.fromString("494299dc-fc3a-44a9-b2a8-d92e908ad99e");

    public FlameAuraEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                AttributeRegistry.FIRE_SPELL_POWER.get(),
                FIRE_POWER_ID.toString(),
                FlameAuraEffect.FIRE_SPELL_POWER_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                AttributeRegistry.FIRE_MAGIC_RESIST.get(),
                FIRE_RES_ID.toString(),
                FlameAuraEffect.FIRE_SPELL_RES_PER_LEVEL,
                AttributeModifier.Operation.ADDITION
        );

        this.addAttributeModifier(
                AttributeRegistry.MANA_REGEN.get(),
                MANA_REGEN_ID.toString(),
                FlameAuraEffect.MANA_REGEN_PER_LEVEL,
                AttributeModifier.Operation.ADDITION
        );

        this.addAttributeModifier(
                AttributeRegistry.MAX_MANA.get(),
                MANA_DEBUFF_ID.toString(),
                FlameAuraEffect.MANA_DEBUFF_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(livingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(livingEntity).getSyncedData().removeEffects(OreoSyncedSpellData.FLAMEAURAEFFECT);
    }

    @Override
    public void addAttributeModifiers(LivingEntity plivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(plivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(plivingEntity).getSyncedData().addEffects(OreoSyncedSpellData.FLAMEAURAEFFECT);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.hasEffect(MobEffectRegistry.CHARGED.get())) {
            entity.removeEffect(MobEffectRegistry.CHARGED.get());
        }

        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            double baseY = entity.getY();
            double height = entity.getBbHeight();
            int particleCount = 4 + amplifier * 2;

            for (int i = 0; i < particleCount; i++) {
                double angle = Math.random() * 2 * Math.PI;

                // Radius of the swirl
                double radius = 0.5 + Math.random() * 0.1;

                // Spiral offsets around entity
                double xOffset = Math.cos(angle) * radius;
                double zOffset = Math.sin(angle) * radius;

                // Vertical range
                double yOffset = baseY + 0.2 + Math.random() * (height * 0.6);

                // Slight upward flicker
                double dx = xOffset * 0.05;
                double dy = 0.15 + Math.random() * 0.1;
                double dz = zOffset * 0.05;

                serverLevel.sendParticles(
                        ParticleHelper.EMBERS,
                        entity.getX() + xOffset,
                        yOffset,
                        entity.getZ() + zOffset,
                        1,
                        dx, dy, dz,
                        0 // speed is ignored since velocity is explicit
                );
            }
        }
    }


    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
