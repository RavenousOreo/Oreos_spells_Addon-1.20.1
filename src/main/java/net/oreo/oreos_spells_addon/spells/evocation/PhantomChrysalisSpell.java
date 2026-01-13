package net.oreo.oreos_spells_addon.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.effect.ButterflyDamageBuff.ButterflyBuffEffect;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;

import java.util.List;


public class PhantomChrysalisSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "phantom_chrysalis");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 20, 1)),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentAttackDamage(spellLevel, caster), 0), Component.translatable("attribute.name.generic.attack_damage")),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentSpellPower(spellLevel, caster), 0), Component.translatable("attribute.irons_spellbooks.spell_power"))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(25)
            .build();

    public PhantomChrysalisSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 15;
        this.spellPowerPerLevel = 5;
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
        entity.addEffect(new MobEffectInstance(OreoMobEffectRegistry.BUTTERFLYDEBUFF.get(), (int) (getSpellPower(spellLevel, entity) * 20), spellLevel - 1, false, false, true));
    }

    private float getPercentAttackDamage(int spellLevel, LivingEntity entity) {
        return spellLevel * ButterflyBuffEffect.ATTACK_DAMAGE_PER_LEVEL * 100;
    }

    private float getPercentSpellPower(int spellLevel, LivingEntity entity) {
        return spellLevel * ButterflyBuffEffect.SPELL_POWER_PER_LEVEL * 100;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
