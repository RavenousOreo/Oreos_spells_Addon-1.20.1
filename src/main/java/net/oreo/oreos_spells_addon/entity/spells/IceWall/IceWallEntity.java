package net.oreo.oreos_spells_addon.entity.spells.IceWall;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import org.checkerframework.checker.nullness.qual.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceWallEntity extends AbstractShieldEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int lifetime = 60;
    private ShieldPart[] subEntities;
    protected  Vec3[] subPositions;
    protected int width;
    protected int height;


    public IceWallEntity(EntityType<? extends AbstractShieldEntity> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.width = 9;
        this.height = 3;
        this.setHealth(10);
        createShield();
    }

    public void setLifetimeTicks(int ticks) {
        this.lifetime = ticks;
    }

    public IceWallEntity(Level level, float health) {
        this(OreoEntityRegistry.ICEWALLENTITY.get(), level);
        this.setHealth(health);
    }

    @Override
    public void tick() {
        hurtThisTick = false;
        super.tick();

        if (getHealth() <= 0) {
            destroy();
        }
        if (++tickCount >= lifetime) {
            if (!this.level().isClientSide) {
                discard();
            }
        }

        // Update sub-entity positions based on wall rotation
        for (int i = 0; i < subEntities.length; i++) {
            var subEntity = subEntities[i];
            Vec3 localOffset = subPositions[i];

            Vec3 rotatedOffset = localOffset
                    .xRot(Mth.DEG_TO_RAD * -this.getXRot())
                    .yRot(Mth.DEG_TO_RAD * -this.getYRot());

            Vec3 worldPos = this.position().add(rotatedOffset);

            subEntity.setPos(worldPos);
            subEntity.xo = worldPos.x;
            subEntity.yo = worldPos.y;
            subEntity.zo = worldPos.z;
            subEntity.xOld = worldPos.x;
            subEntity.yOld = worldPos.y;
            subEntity.zOld = worldPos.z;
        }

        // Particle FX within AABB
        if (!level().isClientSide && level() instanceof ServerLevel) {
            AABB box = getBoundingBox();
            double px = box.minX + random.nextDouble() * (box.maxX - box.minX);
            double py = box.minY + random.nextDouble() * (box.maxY - box.minY);
            double pz = box.minZ + random.nextDouble() * (box.maxZ - box.minZ);

            MagicManager.spawnParticles(
                    this.level(), ParticleHelper.SNOW_DUST,
                    px, py, pz,
                    1, 0, 0, 0, 0.01, true
            );
        }
    }


    @Override
    protected void createShield() {
        this.width = 18;   // 9 blocks wide
        this.height = 8;  // 2 blocks high

        subEntities = new ShieldPart[width * height];
        subPositions = new Vec3[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = x * height + y;

                // Create the part
                subEntities[i] = new ShieldPart(this, "part" + (i + 1), 0.5f, 0.5f, true);

                // Centered horizontally and vertically
                float xOffset = (x - width / 2f + 0.5f) * 0.5f;
                float yOffset = (y - height / 2f + 0.5f) * 0.5f;

                // Offset on local Z = 0 (wall plane)
                subPositions[i] = new Vec3(xOffset, yOffset, 0);

                // Add the part entity to the world
                if (!this.level().isClientSide) {
                    this.level().addFreshEntity(subEntities[i]);
                }
            }
        }
    }


    public void setRotation(float x, float y) {
        this.setXRot(x);
        this.xRotO = x;
        this.setYRot(y);
        this.yRotO = y;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public void takeDamage(DamageSource source, float amount, @Nullable Vec3 location) {
        if (!this.isInvulnerableTo(source)) {
            this.setHealth(this.getHealth() - amount);
            if (!level().isClientSide && location != null) {
                MagicManager.spawnParticles(level(), ParticleTypes.SNOWFLAKE, location.x, location.y, location.z, 30, .1, .1, .1, .5, false);
                level().playSound(null, location.x, location.y, location.z, SoundRegistry.FORCE_IMPACT.get(), SoundSource.NEUTRAL, .8f, 1f);
            }
        }
    }

    @Override
    protected void destroy() {
        if (!this.level().isClientSide) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 2, .65f);
        }
        super.destroy();
    }

    // GeckoLib (visual-only)
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Lifetime", this.lifetime);
        tag.putInt("TickCount", this.tickCount);
        tag.putFloat("Health", this.getHealth());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.lifetime = tag.getInt("Lifetime");
        this.tickCount = tag.getInt("TickCount");
        this.setHealth(tag.getFloat("Health"));
    }

}
