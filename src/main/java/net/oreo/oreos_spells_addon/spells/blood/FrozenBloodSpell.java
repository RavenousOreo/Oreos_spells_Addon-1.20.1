package net.oreo.oreos_spells_addon.spells.blood;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class FrozenBloodSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "frozen_blood");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        float roundDuration = Math.round((getDurationTicks(spellLevel, caster) / 20f) * 10) / 10.0f;

        return List.of(
                Component.translatable("ui.irons_spellbooks.duration", roundDuration)
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(120)
            .build();

    public FrozenBloodSpell() {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 5;
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
        int duration = getDurationTicks(spellLevel, entity);

        // Apply effects to caster
        entity.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), duration, 0, false, false));
        entity.addEffect(new MobEffectInstance(OreoMobEffectRegistry.FROZENBLOODEFFECT.get(), duration, 0, false, false));

        // spawn frozenbloodentity
        FrozenBloodHumanoid FrozenBloodEntity = new FrozenBloodHumanoid(level, entity);
        FrozenBloodEntity.setDeathTimer(duration);
        level.addFreshEntity(FrozenBloodEntity);
    }

    private int getDurationTicks(int spellLevel, LivingEntity caster) {
        return Math.round(20 + getSpellPower(spellLevel, caster) * 5);
    }
}
