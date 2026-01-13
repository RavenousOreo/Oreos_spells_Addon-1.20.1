package net.oreo.oreos_spells_addon.spells.lightning;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.entity.spells.ThunderSwords.LargeThunderSwordEntity;
import net.oreo.oreos_spells_addon.entity.spells.ThunderSwords.ThunderousBeatEntity;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ThunderousCrownSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "thunderous_crown");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(10)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster))
        );
    }

    public ThunderousCrownSpell() {
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 2;
        this.baseManaCost = 85;
        this.manaCostPerLevel = 15;
        this.castTime = 5;
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
        return Optional.of(SoundRegistry.LIGHTNING_WOOSH_01.get());
    }

    @Override
    public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {

    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        // Use horizontal look vector only
        Vec3 lookVec = caster.getLookAngle().normalize();
        Vec3 flatLookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize(); // Remove vertical component

        // Spawn just in front of torso, horizontally
        Vec3 bodyPos = caster.position().add(0, caster.getBbHeight() * 0.5, 0);
        Vec3 swordPos = bodyPos.add(flatLookVec.scale(0.75)); // Slightly in front

        LargeThunderSwordEntity sword = new LargeThunderSwordEntity(OreoEntityRegistry.LARGETHUNDERSWORDENTITY.get(), level);
        sword.setPos(swordPos.x, swordPos.y, swordPos.z);
        sword.setYRot((float) Math.toDegrees(Math.atan2(-flatLookVec.x, flatLookVec.z))); // Face toward look direction
        //sword.setXRot(0); // Keep sword flat regardless of pitch

        level.addFreshEntity(sword);

        // Hit detection
        double range = 6.0f;
        float width = 0.5f;
        Vec3 eyePos = caster.getEyePosition();

        AABB hitZone = new AABB(eyePos, eyePos.add(lookVec.scale(range))).inflate(width);
        LivingEntity target = null;
        double closest = Double.MAX_VALUE;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, hitZone)) {
            if (entity == caster || !entity.isAlive() || entity.isAlliedTo(caster)) continue;

            Vec3 toTarget = entity.getBoundingBox().getCenter().subtract(eyePos);
            if (toTarget.normalize().dot(lookVec) < 0.5) continue;

            double dist = eyePos.distanceTo(entity.getBoundingBox().getCenter());
            if (dist < closest) {
                closest = dist;
                target = entity;
            }
        }

        // Thunderous Beat
        if (target != null) {
            float damage = getSpellPower(spellLevel, caster);

            ThunderousBeatEntity beat = new ThunderousBeatEntity(OreoEntityRegistry.THUNDEROUSBEATENTITY.get(), level);
            beat.setCaster(caster);
            beat.setDamage(damage);
            beat.setAttachedTarget(target);
            beat.setPos(target.getX(), target.getY() + 0.7f, target.getZ());
            level.addFreshEntity(beat);
        }
    }





    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.ANIMATION_INSTANT_CAST;
    }

    private float getDamageText(int spellLevel, @Nullable LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }
}
