package net.oreo.oreos_spells_addon.spells.lightning;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;
import net.oreo.oreos_spells_addon.registries.OreoSpellRegistry;
import org.jetbrains.annotations.Nullable;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

import java.util.List;
import java.util.Optional;


public class ThunderClapDashSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "thunder_clap_dash");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(30)
            .build();

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getDistance(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster))
        );
    }

    public ThunderClapDashSpell() {
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 5;
        this.baseManaCost = 50;
        this.manaCostPerLevel = 15;
        this.castTime = 0;
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
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.LIGHTNING_WOOSH_01.get());
    }

    @Override
    public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
        super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
        super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);

        Vec3 forward = entity.getForward().normalize();
        var random = entity.getRandom();

        for (int i = 0; i < 45; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2.0 * 1.0;  // -1.0 to 1.0
            double offsetY = (random.nextDouble() - 0.5) * 2.0 * 1.0;
            double offsetZ = (random.nextDouble() - 0.5) * 2.0 * 1.0;

            double motionX = forward.x + (random.nextDouble() - 0.5) * 1.2;
            double motionY = 0.1 + (random.nextDouble() - 0.5) * 0.6;
            double motionZ = forward.z + (random.nextDouble() - 0.5) * 1.2;

            level.addParticle(
                    ParticleHelper.ELECTRICITY,
                    entity.getX() + offsetX,
                    entity.getY() + offsetY + 0.5,
                    entity.getZ() + offsetZ,
                    motionX * 0.5,
                    motionY,
                    motionZ * 0.5
            );
        }
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 destination = null;
        HitResult hitResult = Utils.raycastForEntity(level, entity, getDistance(spellLevel, entity), true);

        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof LivingEntity target) {
            Vec3 toTarget = target.position().subtract(entity.position()).normalize();
            destination = target.position().subtract(toTarget.scale(2.5)).add(0, 0.05, 0);

            entity.teleportTo(destination.x, destination.y, destination.z);

            // Damage the target
            float damage = getSpellPower(spellLevel, entity);
            target.hurt(OreoSpellRegistry.ThunderClapDashSpell.get().getDamageSource(entity), damage);

            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 40; i++) {
                    double spread = 1.2;
                    serverLevel.sendParticles(
                            ParticleHelper.ELECTRICITY,
                            target.getX() + (entity.getRandom().nextDouble() - 0.5) * spread,
                            target.getY() + 0.5 + (entity.getRandom().nextDouble() - 0.5) * spread,
                            target.getZ() + (entity.getRandom().nextDouble() - 0.5) * spread,
                            1, 0, 0, 0, 0
                    );
                }
            }
        } else {
            // No target hit — fallback teleport
            destination = TeleportSpell.findTeleportLocation(level, entity, getDistance(spellLevel, entity));
            entity.teleportTo(destination.x, destination.y, destination.z);
        }

        // Apply post-cast effects
        entity.resetFallDistance();
        level.playSound(null, destination.x, destination.y, destination.z, getCastFinishSound().get(), SoundSource.NEUTRAL, 1f, 1f);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDistance(int spellLevel, LivingEntity sourceEntity) {
        return 5f * spellLevel + 5f;

    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }

    private float getDamageText(int spellLevel, @Nullable LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }

}
