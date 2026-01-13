package net.oreo.oreos_spells_addon.entity.spells.BloodyLance;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import net.oreo.oreos_spells_addon.registries.OreoSpellRegistry;

import java.util.Optional;
import java.util.function.Supplier;

public class BloodyLanceProjectile extends AbstractMagicProjectile {

    @Override
    public void trailParticles() {
        Vec3 vec3 = this.position().subtract(getDeltaMovement());
        this.level().addParticle(ParticleHelper.BLOOD, vec3.x, vec3.y, vec3.z, 0, 0, 0);
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(this.level(), ParticleHelper.BLOOD, x, y, z, 75, .1, .1, .1, 2, true);
        MagicManager.spawnParticles(this.level(), ParticleHelper.BLOOD, x, y, z, 75, .1, .1, .1, .5, false);
    }

    @Override
    public float getSpeed() {
        return 3f;
    }

    @Override
    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }


    public BloodyLanceProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(false);
    }

    public BloodyLanceProjectile(Level levelIn, LivingEntity shooter) {
        this(OreoEntityRegistry.BLOODY_LANCE_PROJECTILE.get(), levelIn);
        setOwner(shooter);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {

    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        DamageSources.applyDamage(entityHitResult.getEntity(), damage, OreoSpellRegistry.BloodyLanceSpell.get().getDamageSource(this, getOwner()));

    }

    @Override
    protected void onHit(HitResult pResult) {

        if (!this.level().isClientSide) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 4, .65f);

        }


        super.onHit(pResult);
        this.discard();
    }

    public int getAge(){
        return tickCount;
    }

}
