package net.oreo.oreos_spells_addon.spells.ender;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidRuptureEntity;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class VoidRuptureSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "void_rupture");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        float roundedDamage = Math.round(damagePerTick(spellLevel, caster) * 10) / 10.0f;
        float roundedDuration = Math.round((getDurationTicks(spellLevel, caster) / 20f) * 10) / 10.0f;
        float roundedRadius = Math.round(getRadius(spellLevel, caster) * 10f) / 10f;


        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", roundedDamage),
                Component.translatable("ui.irons_spellbooks.duration", roundedDuration),
                Component.translatable("ui.irons_spellbooks.radius", roundedRadius)
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(15)
            .build();

    public VoidRuptureSpell() {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 2;
        this.spellPowerPerLevel = 1;
        this.castTime = 1;
        this.baseManaCost = 85;
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
        return Optional.of(SoundRegistry.ENDER_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public boolean canBeInterrupted(@Nullable Player player) {
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            float spellPower = getSpellPower(spellLevel, caster);
            int duration = getDurationTicks(spellLevel, caster);
            int spawnRate = summonFrequencyTicks(spellLevel, caster);
            float radius = getRadius(spellLevel, caster);

            // Get spawn point in front of the caster, clamped to ground
            Vec3 look = caster.getLookAngle().normalize();
            Vec3 offset = caster.position().add(look.scale(2.0));
            Vec3 groundPos = Utils.moveToRelativeGroundLevel(level, offset, 12);

            // Create and configure entity
            VoidRuptureEntity rupture = new VoidRuptureEntity(level, caster, spellPower, duration, radius);
            rupture.setSpawnFrequency(spawnRate);
            rupture.setRadius(radius); // ensure internal + visual radius match
            rupture.setPos(groundPos);
            level.addFreshEntity(rupture);

            // Visual ring
            TargetedAreaEntity.createTargetAreaEntity(level, groundPos, radius, 0xAA00AA, rupture);
        }
    }


    private float getRadius(int spellLevel, LivingEntity caster) {
        return 5.5f + 0.5f * spellLevel;
    }

    private float damagePerTick(int spellLevel, LivingEntity caster) {
        return 2.0f + getSpellPower(spellLevel, caster) * 1.1f;
    }

    private int getDurationTicks(int spellLevel, LivingEntity caster) {
        return Math.round(40 + getSpellPower(spellLevel, caster) * 12);
    }

    private int summonFrequencyTicks(int spellLevel, LivingEntity caster) {
        return Math.max(4, 14 - spellLevel * 3 - Math.round(getSpellPower(spellLevel, caster) * 0.5f));
    }

}
