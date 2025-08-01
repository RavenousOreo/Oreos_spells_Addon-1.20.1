package net.oreo.oreos_spells_addon.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.oreo.oreos_spells_addon.entity.spells.PiercingBlood.PiercingBloodEntity;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class PiercingBloodSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "piercing_blood");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(20)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 2)),
                Component.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getRange(spellLevel, caster), 1))
        );
    }

    public PiercingBloodSpell() {
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 15;
        this.spellPowerPerLevel = 1;
        this.castTime = 15;
        this.baseManaCost = 40;
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
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.BLOOD_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BLOOD_EXPLOSION.get());
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        var hitResult = Utils.raycastForEntity(level, entity, getRange(spellLevel, entity), true, .15f);
        level.addFreshEntity(new PiercingBloodEntity(level, entity.getEyePosition(), hitResult.getLocation(), entity, spellLevel));
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hitResult).getEntity();
            DamageSources.applyDamage(target, getDamage(spellLevel, entity), getDamageSource(entity));
            MagicManager.spawnParticles(level, ParticleHelper.BLOOD_GROUND, hitResult.getLocation().x, target.getY(), hitResult.getLocation().z, 4, 0, 0, 0, .3, true);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            MagicManager.spawnParticles(level, ParticleHelper.BLOOD_GROUND, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 4, 0, 0, 0, .3, true);
        }
        MagicManager.spawnParticles(level, ParticleHelper.BLOOD, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 50, 0, 0, 0, .3, false);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public float getRange(int level, LivingEntity caster) {
        return 30;
    }

    public float getDamage(int spellLevel, LivingEntity caster) {
        return 3 + getSpellPower(spellLevel, caster) * 1.5f;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_CONTINUOUS_CAST;
    }

}
