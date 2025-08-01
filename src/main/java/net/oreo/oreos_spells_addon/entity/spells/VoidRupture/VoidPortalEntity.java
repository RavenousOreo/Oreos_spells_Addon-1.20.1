package net.oreo.oreos_spells_addon.entity.spells.VoidRupture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;

public class VoidPortalEntity extends Entity {

    private LivingEntity owner;
    private int lifetime = 20; // lasts 0.5 seconds


    public VoidPortalEntity(EntityType<? extends VoidPortalEntity> type, Level level) {
        super(type, level);
    }

    public VoidPortalEntity(Level level, LivingEntity owner) {
        this(OreoEntityRegistry.VOIDPORTALENTITY.get(), level);
        this.setOwner(owner); // properly handled by superclass
        this.setNoGravity(true);
    }


    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;



        if (tickCount == 10) {
            Entity owner = getOwner();
            if (owner instanceof LivingEntity livingOwner) {
                VoidSwordProjectile sword = new VoidSwordProjectile(level(), livingOwner);
                sword.setPos(this.getX(), this.getY(), this.getZ());
                level().addFreshEntity(sword);
            }
        }

        if (this.tickCount >= lifetime) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }


}

