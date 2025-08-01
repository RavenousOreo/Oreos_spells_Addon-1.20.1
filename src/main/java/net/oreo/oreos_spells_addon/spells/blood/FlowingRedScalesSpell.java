package net.oreo.oreos_spells_addon.spells.blood;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.capabilities.magic.OreoSyncedSpellData;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.effect.*;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AutoSpellConfig
public class FlowingRedScalesSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "flowingredscales");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 5, 1)),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentSpeed(spellLevel, caster), 0), Component.translatable("attribute.name.generic.movement_speed")),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentAttackDamage(spellLevel, caster), 0), Component.translatable("attribute.name.generic.attack_damage")),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentBloodSPower(spellLevel, caster), 0), Component.translatable("attribute.irons_spellbooks.blood_spell_power")),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getLifeStealPower(spellLevel, caster), 0), Component.translatable("attributeslib:life_steal"))

        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(40)
            .build();

    public FlowingRedScalesSpell() {
        this.manaCostPerLevel = 30;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 8;
        this.castTime = 0;
        this.baseManaCost = 50;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.removeEffect(MobEffectRegistry.CHARGED.get());
        if (entity.hasEffect(MobEffectRegistry.CHARGED.get())) {
            return; // Cancel the cast entirely
        }

        entity.addEffect(new MobEffectInstance(OreoMobEffectRegistry.FLOWINGREDSCALES.get(), (int) (getSpellPower(spellLevel, entity) * 20), spellLevel - 1, false, false, true));


        super.onCast(level, spellLevel, entity, castSource, playerMagicData);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            int loops = 3;
            int particlesPerLoop = 30;
            double radius = 1.5;
            double height = 2.0;
            double yOffset = entity.getBbHeight() * 0.5;

            for (int i = 0; i < loops * particlesPerLoop; i++) {
                double angle = 2 * Math.PI * i / particlesPerLoop;
                double spiralHeight = (height / (loops * particlesPerLoop)) * i;

                double x = entity.getX() + Math.cos(angle) * radius;
                double y = entity.getY() + yOffset + spiralHeight;
                double z = entity.getZ() + Math.sin(angle) * radius;

                serverLevel.sendParticles(
                        ParticleHelper.BLOOD,
                        x, y, z,
                        1, // count
                        0, 0, 0, // xOffset, yOffset, zOffset
                        0        // speed
                );
            }
        }

        if (!level.isClientSide) {
            var attribute = ALObjects.Attributes.LIFE_STEAL.get();
            var instance = entity.getAttributes().getInstance(attribute);
            UUID uuid = UUID.nameUUIDFromBytes("flowing_red_scales_lifesteal".getBytes());

            if (instance != null && instance.getModifier(uuid) == null) {
                instance.addPermanentModifier(new AttributeModifier(uuid, "flowing_red_scales_lifesteal", 0.05, AttributeModifier.Operation.ADDITION));
            }
        }

    }

    private float getPercentAttackDamage(int spellLevel, LivingEntity entity) {
        return spellLevel * FlowingRedScalesEffect.ATTACK_DAMAGE_PER_LEVEL * 100;
    }

    private float getLifeStealPower(int spellLevel, LivingEntity entity) {
        return 5.0f;
    }

    private float getPercentSpeed(int spellLevel, LivingEntity entity) {
        return spellLevel * FlowingRedScalesEffect.SPEED_PER_LEVEL * 100;
    }

    private float getPercentBloodSPower(int spellLevel, LivingEntity entity) {
        return spellLevel * FlowingRedScalesEffect.BLOOD_POWER_PER_LEVEL * 100;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }

}
