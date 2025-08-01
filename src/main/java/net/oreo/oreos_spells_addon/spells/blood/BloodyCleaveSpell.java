package net.oreo.oreos_spells_addon.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.entity.spells.BloodyCleave.BloodyCleave;
import io.redspace.ironsspellbooks.api.spells.SchoolType;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class BloodyCleaveSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "bloody_cleave");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(18)
            .build();

    public BloodyCleaveSpell() {
        this.manaCostPerLevel = 18;
        this.baseSpellPower = 3;
        this.spellPowerPerLevel = 2;
        this.castTime = 10;
        this.baseManaCost = 45;
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
        //due to animation timing, we do not want cast time attribute to affect this spell
        return getCastTime(spellLevel);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float radius = 3.25f;
        float distance = 1.9f;
        Vec3 forward = entity.getForward();
        Vec3 hitLocation = entity.position().add(0, entity.getBbHeight() * .3f, 0).add(forward.scale(distance));
        var entities = level.getEntities(entity, AABB.ofSize(hitLocation, radius * 2, radius, radius * 2));
        var damageSource = this.getDamageSource(entity);
        for (Entity targetEntity : entities) {
            if (targetEntity instanceof LivingEntity && targetEntity.isAlive() && entity.isPickable() && targetEntity.position().subtract(entity.getEyePosition()).dot(forward) >= 0 && entity.distanceToSqr(targetEntity) < radius * radius && Utils.hasLineOfSight(level, entity.getEyePosition(), targetEntity.getBoundingBox().getCenter(), true)) {
                Vec3 offsetVector = targetEntity.getBoundingBox().getCenter().subtract(entity.getEyePosition());
                if (offsetVector.dot(forward) >= 0) {
                    if (DamageSources.applyDamage(targetEntity, getDamage(spellLevel, entity), damageSource)) {
                        MagicManager.spawnParticles(level, ParticleHelper.BLOOD, targetEntity.getX(), targetEntity.getY() + targetEntity.getBbHeight() * .5f, targetEntity.getZ(), 50, targetEntity.getBbWidth() * .5f, targetEntity.getBbHeight() * .5f, targetEntity.getBbWidth() * .5f, .03, false);
                        EnchantmentHelper.doPostDamageEffects(entity, targetEntity);
                    }
                }
            }
        }
        boolean mirrored = playerMagicData.getPlayerCastingItem().equals(SpellSelectionManager.OFFHAND);
        BloodyCleave BloodyCleave = new BloodyCleave(level, mirrored);
        BloodyCleave.moveTo(hitLocation);
        BloodyCleave.setYRot(entity.getYRot());
        BloodyCleave.setXRot(entity.getXRot());
        level.addFreshEntity(BloodyCleave);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setLifestealPercent(.15f);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity, MobType.UNDEFINED) + EnchantmentHelper.getSweepingDamageRatio(entity);
    }


    private String getDamageText(int spellLevel, LivingEntity entity) {
        if (entity != null) {
            float weaponDamage = Utils.getWeaponDamage(entity, MobType.UNDEFINED);
            String plus = "";
            if (weaponDamage > 0) {
                plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
            }
            String damage = Utils.stringTruncation(getDamage(spellLevel, entity), 1);
            return damage + plus;
        }
        return "" + getSpellPower(spellLevel, entity);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.pass();
    }



}
