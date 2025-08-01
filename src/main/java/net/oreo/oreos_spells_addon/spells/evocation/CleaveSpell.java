package net.oreo.oreos_spells_addon.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.entity.spells.Cleave.CleaveEntity;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class CleaveSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "cleave");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(18)
            .build();

    public CleaveSpell() {
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 5;
        this.baseManaCost = 20;
    }

    private int getSlashCount(int spellLevel) {
        return 2 + spellLevel;
    }

    private float getDamagePerSlash(int spellLevel, LivingEntity caster) {
        return 5f + (spellLevel - 1) * 2;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamagePerSlash(spellLevel, caster), 1)),
                Component.translatable("ui.oreos_spells_addon.slashcount", getSlashCount(spellLevel)),
                Component.translatable("ui.oreos_spells_addon.maxhpdamage", 2.5)
        );
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.EVOCATION_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.BLOOD_NEEDLE_IMPACT.get());
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
    public boolean canBeInterrupted(@Nullable Player player) {
        return false;
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
        return getCastTime(spellLevel);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        Vec3 origin = caster.getEyePosition();
        Vec3 lookVec = caster.getLookAngle();
        double range = 4.0;
        float width = 1.25f;

        AABB hitZone = new AABB(origin, origin.add(lookVec.scale(range))).inflate(width, width * 0.5f, width);
        LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, hitZone)) {
            if (entity == caster || !entity.isAlive() || entity.isAlliedTo(caster)) continue;

            Vec3 toTarget = entity.getBoundingBox().getCenter().subtract(origin);
            if (toTarget.normalize().dot(lookVec) < 0.5) continue;

            double distance = origin.distanceTo(entity.getBoundingBox().getCenter());
            if (distance < closestDistance) {
                closestDistance = distance;
                target = entity;
            }
        }

        if (target != null) {
            float damage = getDamagePerSlash(spellLevel, caster);
            int slashes = getSlashCount(spellLevel);

            for (int i = 0; i < slashes; i++) {
                if (target.isAlive()) {
                    DamageSources.applyDamage(target, damage, getDamageSource(caster, caster));
                }
            }

            CleaveEntity cleave = new CleaveEntity(OreoEntityRegistry.CLEAVE.get(), level);
            cleave.setCaster(caster);
            cleave.setSpellLevel(spellLevel);
            cleave.setDamagePerHit(damage / cleave.getSlashCount());
            cleave.attachTo(target);
            level.addFreshEntity(cleave);
        }
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_INSTANT_CAST;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.ANIMATION_INSTANT_CAST;
    }
}