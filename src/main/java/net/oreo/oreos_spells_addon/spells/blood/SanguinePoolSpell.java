package net.oreo.oreos_spells_addon.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.entity.spells.SanguinePool.SanguinePoolEntity;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoSpellConfig
public class SanguinePoolSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "sanguine_pool");

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
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(60)
            .build();

    public SanguinePoolSpell() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 3;
        this.spellPowerPerLevel = 2;
        this.castTime = 0;
        this.baseManaCost = 100;
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
    public boolean canBeInterrupted(@Nullable Player player) {
        return false;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            int duration = getDurationTicks(spellLevel, entity);
            float damage = damagePerTick(spellLevel, entity);

            // Apply effects to caster
            entity.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), duration, 0, false, false));
            entity.addEffect(new MobEffectInstance(OreoMobEffectRegistry.SANGUINEPOOLEFFECT.get(), duration, 0, false, false));

            // Spawn pool entity using full constructor
            SanguinePoolEntity pool = new SanguinePoolEntity(level, entity, 1.0f, damage, duration);
            pool.setOwner(entity);
            level.addFreshEntity(pool);
        }

        entity.invulnerableTime = getDurationTicks(spellLevel, entity);

    }


    @Override
    public SpellDamageSource getDamageSource(Entity pool, Entity attacker) {
        return super.getDamageSource(pool, attacker).setLifestealPercent(.05f);
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.pass();
    }

    private float damagePerTick(int spellLevel, LivingEntity caster) {
        return 2.0f + getSpellPower(spellLevel, caster) * 1.0f;
    }

    private int getDurationTicks(int spellLevel, LivingEntity caster) {
        return Math.round(40 + getSpellPower(spellLevel, caster) * 10);
    }
}
