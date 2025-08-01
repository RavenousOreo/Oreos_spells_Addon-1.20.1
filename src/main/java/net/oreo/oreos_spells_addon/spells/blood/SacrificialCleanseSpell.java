package net.oreo.oreos_spells_addon.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.minecraft.resources.ResourceLocation;


import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class SacrificialCleanseSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "sacrificial_cleanse");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_resistance_level", Utils.stringTruncation(spellLevel, 0))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(60)
            .build();

    public SacrificialCleanseSpell() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 60;
        this.baseManaCost = 50;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
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
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BLOOD_CAST.get());
    }

    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            // Deal 10% of max HP as self-damage
            float damage = entity.getMaxHealth() * 0.25f;
            entity.hurt(level.damageSources().magic(), damage);

            // Remove all harmful effects from the caster
            var harmfulEffects = entity.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL)
                    .toList();

            harmfulEffects.forEach(entity::removeEffect);

            // Apply Resistance effect
            int resistanceAmplifier = Mth.clamp(spellLevel - 1, 0, 2); // Resistance I–III
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, resistanceAmplifier));

            // Visual feedback
            MagicManager.spawnParticles(
                    level,
                    ParticleHelper.BLOOD,
                    entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    20,
                    entity.getBbWidth() * 0.6,
                    0.4,
                    entity.getBbWidth() * 0.6,
                    0.0,
                    false
            );
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_RAISED_HAND;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
