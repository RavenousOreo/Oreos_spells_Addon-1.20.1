package net.oreo.oreos_spells_addon.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.entity.spells.Dismantle.DismantleProjectile;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

import java.util.List;
import java.util.Optional;


public class DismantleSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "dismantle");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(35)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getScaledDamage(spellLevel, caster), 1)),
                Component.translatable("ui.oreos_spells_addon.recast", Utils.stringTruncation(getRecastCount(spellLevel, caster), 1))
        );
    }

    public DismantleSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 5;
        this.baseManaCost = 50;
    }

    float getScaledDamage(int spellLevel, LivingEntity caster) {
        float spellPower = getSpellPower(spellLevel, caster);
        // Scale proportionally from level 1 to 10: base damage scales with level
        float base = 1.0f + (spellLevel - 1) * (4.0f / 9f); // level 1: 1.0, level 10: 5.0
        float normalizedPower = spellPower / (this.baseSpellPower + (spellLevel - 1) * this.spellPowerPerLevel); // 14 at level 10
        return base * 4.0f * normalizedPower; // multiplier chosen to hit 20 at level 10
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
        return Optional.of(SoundRegistry.EVOCATION_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.LIGHTNING_WOOSH_01.get());
    }

    @Override
    public int getRecastCount(int spellLevel, LivingEntity caster) {
        return Math.max(0, 3 + spellLevel - 1); // Level 1: 2, Level 10: 11
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        // Add recast instance if it's the initial cast
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(
                    new RecastInstance(
                            getSpellId().toString(),
                            spellLevel,
                            getRecastCount(spellLevel, caster),
                            500, // ticks to live – adjust for recast window duration
                            castSource,
                            null
                    ),
                    playerMagicData
            );
        }

        // Fire the projectile
        DismantleProjectile projectile = new DismantleProjectile(level, caster);
        projectile.setPos(caster.position().add(0, caster.getEyeHeight(), 0).add(caster.getForward()));
        projectile.shoot(caster.getLookAngle());
        projectile.setDamage(getScaledDamage(spellLevel, caster));
        level.addFreshEntity(projectile);

        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
    }

}
