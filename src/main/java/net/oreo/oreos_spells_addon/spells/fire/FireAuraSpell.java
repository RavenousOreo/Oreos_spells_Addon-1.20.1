package net.oreo.oreos_spells_addon.spells.fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.entity.spells.FlameAuraEntity.FlameAuraEntity;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;

import java.util.List;

@AutoSpellConfig
public class FireAuraSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "flame_aura");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        float roundedDamage = Math.round(damagePerTick(spellLevel, caster) * 10) / 10.0f;
        float roundedDuration = Math.round((getDurationTicks(spellLevel, caster) / 20f) * 10) / 10.0f;

        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", roundedDamage),
                Component.translatable("ui.irons_spellbooks.duration", roundedDuration)
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(60)
            .build();

    public FireAuraSpell() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
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
        if (!level.isClientSide) {
            int duration = getDurationTicks(spellLevel, entity);
            float damage = damagePerTick(spellLevel, entity);
            int amplifier = getAmplifier(spellLevel, entity);
            float radius = getRadius(spellLevel, entity);

            entity.addEffect(new MobEffectInstance(OreoMobEffectRegistry.FLAMEAURABUFF.get(), (int) (getSpellPower(spellLevel, entity) * 20), amplifier, false, false, true));

            super.onCast(level, spellLevel, entity, castSource, playerMagicData);

            // summon flame entity
            FlameAuraEntity aura = new FlameAuraEntity(level, entity, radius, damage, duration, amplifier);
            aura.setOwner(entity);
            level.addFreshEntity(aura);



        }
    }

    private float damagePerTick(int spellLevel, LivingEntity entity) {
        return 2.0f + getSpellPower(spellLevel, entity) * 1.0f;
    }

    private int getDurationTicks(int spellLevel, LivingEntity entity) {
        float spellPower = getSpellPower(spellLevel, entity);
        return Mth.clamp(Math.round(600 + spellPower * 40 + spellLevel * 60), 400, 1800);
    }

    private int getAmplifier(int spellLevel, LivingEntity entity) {
        return Math.max(0, spellLevel - 1);
    }

    private float getRadius(int spellLevel, LivingEntity entity) {
        return 2.5f + 0.5f * spellLevel;
    }
}
