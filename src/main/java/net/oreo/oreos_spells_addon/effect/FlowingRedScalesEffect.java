package net.oreo.oreos_spells_addon.effect;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.oreo.oreos_spells_addon.capabilities.magic.OreoSyncedSpellData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.registries.ForgeRegistries;


import java.util.List;
import java.util.UUID;

public class FlowingRedScalesEffect extends MagicMobEffect {
    public static final float ATTACK_DAMAGE_PER_LEVEL = .05f;
    public static final float SPEED_PER_LEVEL = .0335f;
    public static final float BLOOD_POWER_PER_LEVEL = .10f;
    public static final float LIFESTEAL_POWER = 0.0f;
    public static final UUID SPEED_MODIFIER_ID = UUID.fromString("ead57bdf-a4ad-4cd6-bed4-566ef16b0377");
    public static final UUID ATTACK_MODIFIER_ID = UUID.fromString("b8d74e9a-b8a9-4f11-a08c-1be045d22ef9");
    public static final UUID BLOOD_POWER_ID = UUID.fromString("a64e4099-278f-4dbb-8be0-7fccbd284f17");
    public static final UUID LIFESTEAL_POWER_ID = UUID.fromString("5f23aebe-40d3-4175-82b8-094a7efdd7e1");
    ResourceLocation LIFESTEAL_ID = new ResourceLocation("apothicattributes", "lifesteal");
    public FlowingRedScalesEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                SPEED_MODIFIER_ID.toString(),
                FlowingRedScalesEffect.SPEED_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_BASE
        );

        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ATTACK_MODIFIER_ID.toString(),
                FlowingRedScalesEffect.ATTACK_DAMAGE_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                AttributeRegistry.BLOOD_SPELL_POWER.get(),
                BLOOD_POWER_ID.toString(),
                FlowingRedScalesEffect.BLOOD_POWER_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );



    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(livingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(livingEntity).getSyncedData().removeEffects(OreoSyncedSpellData.FLOWINGREDSCALES);

        Attribute lifesteal = ALObjects.Attributes.LIFE_STEAL.get();
        if (lifesteal != null && pAttributeMap.hasAttribute(lifesteal)) {
            UUID uuid = UUID.nameUUIDFromBytes("flowing_red_scales_lifesteal".getBytes());
            pAttributeMap.getInstance(lifesteal).removeModifier(uuid);
        }
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        MagicData.getPlayerMagicData(pLivingEntity).getSyncedData().addEffects(OreoSyncedSpellData.FLOWINGREDSCALES);

    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.hasEffect(MobEffectRegistry.CHARGED.get())) {
            entity.removeEffect(MobEffectRegistry.CHARGED.get());
        }
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            double radius = 1.0;
            double minY = entity.getY();
            double maxY = entity.getY() + entity.getBbHeight() * 0.75;

            int particleCount = 3 + amplifier; // More with higher spell level

            for (int i = 0; i < particleCount; i++) {
                double angle = Math.random() * 2 * Math.PI;
                double r = Math.random() * radius;
                double x = entity.getX() + Math.cos(angle) * r;
                double y = minY + Math.random() * (maxY - minY);
                double z = entity.getZ() + Math.sin(angle) * r;

                double ySpeed = 0.02 + Math.random() * 0.015;

                serverLevel.sendParticles(
                        ParticleHelper.BLOOD,
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
