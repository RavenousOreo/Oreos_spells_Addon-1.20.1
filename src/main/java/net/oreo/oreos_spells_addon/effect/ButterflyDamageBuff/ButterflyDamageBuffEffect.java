package net.oreo.oreos_spells_addon.effect.ButterflyDamageBuff;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.oreo.oreos_spells_addon.capabilities.magic.OreoSyncedSpellData;
import net.oreo.oreos_spells_addon.registries.OreoParticleRegistry;

import java.util.UUID;

public class ButterflyDamageBuffEffect extends MagicMobEffect {
    public static final float ATTACK_DAMAGE_PER_LEVEL = 0.15f;
    public static final float SPELL_POWER_PER_LEVEL = 0.05f;
    public static final UUID ATTACK_MODIFIER_ID = UUID.fromString("95075f7e-9236-4a88-88cd-3988157ff1cd");
    public static final UUID SPELL_POWER_ID  = UUID.fromString("1c254d29-cecc-4611-bc11-228ecb4c24f0");

    public ButterflyDamageBuffEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ATTACK_MODIFIER_ID.toString(),
                ButterflyDamageBuffEffect.ATTACK_DAMAGE_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                AttributeRegistry.SPELL_POWER.get(),
                SPELL_POWER_ID.toString(),
                ButterflyDamageBuffEffect.SPELL_POWER_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public void removeAttributeModifiers(LivingEntity plivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(plivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(plivingEntity).getSyncedData().removeEffects(OreoSyncedSpellData.BUTTERFLYDAMAGE);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(pLivingEntity).getSyncedData().addEffects(OreoSyncedSpellData.BUTTERFLYDAMAGE);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

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
                    5,          // count
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
