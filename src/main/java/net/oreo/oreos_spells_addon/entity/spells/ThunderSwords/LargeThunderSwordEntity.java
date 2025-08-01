package net.oreo.oreos_spells_addon.entity.spells.ThunderSwords;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static java.nio.file.Files.getOwner;

public class LargeThunderSwordEntity extends Entity {


    public LargeThunderSwordEntity(EntityType<? extends LargeThunderSwordEntity> type, Level level) {
        super(type, level);
    }

    private float damage;
    public void setDamage(float dmg) { this.damage = dmg; }
    public float getDamage() { return this.damage; }
    private LivingEntity owner;

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public LivingEntity getOwner() {
        return this.owner;
    }


    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            if (tickCount % 2 == 0) { // only emit every other tick
                Vec3 forward = getLookAngle().normalize();
                Vec3 origin = position().add(0, 0.6, 0); // mid-sword height

                double along = random.nextDouble() * 3.0;
                Vec3 base = origin.add(forward.scale(along));

                double xOffset = (random.nextDouble() - 0.5) * 0.2;
                double yOffset = (random.nextDouble() - 0.5) * 0.2;
                double zOffset = (random.nextDouble() - 0.5) * 0.2;

                serverLevel.sendParticles(
                        ParticleHelper.ELECTRICITY,
                        base.x + xOffset, base.y + yOffset, base.z + zOffset,
                        2, 0, 0, 0, 0.25
                );
            }
        }

        if (this.tickCount > 5) {
            this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {}
}
