package net.oreo.oreos_spells_addon.entity.spells.ThunderSwords;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoSoundRegistry;
import net.oreo.oreos_spells_addon.registries.OreoSpellRegistry;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class ThunderousBeatEntity extends Entity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ThunderousBeatEntity(EntityType<? extends ThunderousBeatEntity> type, Level level) {
        super(type, level);
    }

    private float damage;

    public void setDamage(float dmg) { this.damage = dmg; }
    public float getDamage() { return this.damage; }
    private LivingEntity caster = null;
    private boolean soundPlayed = false;

    private LivingEntity attachedTarget;
    private UUID attachedTargetUUID;

    public void setAttachedTarget(LivingEntity entity) {
        this.attachedTarget = entity;
        this.attachedTargetUUID = entity.getUUID();

    }


    public LivingEntity getAttachedTarget() {
        return this.attachedTarget;
    }

    public void setCaster(LivingEntity caster) {
        this.caster = caster;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {

            double xOffset = (random.nextDouble() - 0.5) * 0.2;
            double yOffset = (random.nextDouble() - 0.5) * 0.2;
            double zOffset = (random.nextDouble() - 0.5) * 0.2;

            MagicManager.spawnParticles(
                    this.level(), ParticleHelper.ELECTRICITY,
                    this.getX() + xOffset, this.getY() + yOffset, this.getZ() + zOffset,
                    5, 0.4, 0.8, 0.4, 0.25, true
            );

            MagicManager.spawnParticles(
                    this.level(), ParticleHelper.ELECTRIC_SPARKS,
                    this.getX() + xOffset, this.getY() + yOffset, this.getZ() + zOffset,
                    5, 0.5, 0.5, 0.5, 0.5, true
            );

        }

        if (!level().isClientSide) {
            if (attachedTarget == null && attachedTargetUUID != null && level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(attachedTargetUUID);
                if (entity instanceof LivingEntity living) {
                    attachedTarget = living;
                }
            }

            if (attachedTarget != null && attachedTarget.isAlive()) {
                //setPos(attachedTarget.getX(), attachedTarget.getY() + attachedTarget.getBbHeight() / 2f, attachedTarget.getZ());

                DamageSources.applyDamage(
                        attachedTarget,
                        getDamage(),
                        OreoSpellRegistry.ThunderousCrownSpell.get().getDamageSource(this, caster)
                );

                AABB area = new AABB(
                        getX() - 2.5, getY() - 1.0, getZ() - 2.5,
                        getX() + 2.5, getY() + 1.0, getZ() + 2.5
                );

                for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area, e -> e.isAlive())) {
                    DamageSources.applyDamage(
                            target,
                            getDamage(),
                            OreoSpellRegistry.ThunderousCrownSpell.get().getDamageSource(this, caster)
                    );
                }

                // check if my stupid sound has been stupid played or not and only do it once idiot

                if (!soundPlayed && attachedTarget != null && attachedTarget.isAlive()) {
                    level().playSound(null, getX(), getY(), getZ(),
                            OreoSoundRegistry.THUNDEROUS_BEAT_SOUND.get(),
                            SoundSource.PLAYERS, 1.0f, 1.0f);
                    soundPlayed = true;
                }

            } else {
                discard();
            }

            if (tickCount > 60) {
                if (!level().isClientSide) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level());
                    if (lightning != null) {
                        lightning.moveTo(this.getX(), this.getY(), this.getZ());
                        lightning.setCause(caster instanceof ServerPlayer ? (ServerPlayer) caster : null);
                        level().addFreshEntity(lightning);
                    }
                }
                discard();
            }
        }
    }


    private static final RawAnimation thunderous_beat_animation = RawAnimation.begin().thenPlay("thunderous_beat_animation");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(
                this,
                "controller",
                0,
                state -> {
                    state.setAnimation(thunderous_beat_animation);
                    return PlayState.CONTINUE;
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("AttachedTarget")) {
            this.attachedTargetUUID = tag.getUUID("AttachedTarget");
        }
        this.damage = tag.getFloat("Damage");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        if (this.attachedTargetUUID != null) {
            tag.putUUID("AttachedTarget", this.attachedTargetUUID);
        }
        tag.putFloat("Damage", this.damage);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

}
