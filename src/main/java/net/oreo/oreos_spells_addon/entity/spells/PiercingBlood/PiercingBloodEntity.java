package net.oreo.oreos_spells_addon.entity.spells.PiercingBlood;

import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import net.oreo.oreos_spells_addon.registries.OreoSpellRegistry;
import net.oreo.oreos_spells_addon.spells.blood.PiercingBloodSpell;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PiercingBloodEntity extends Entity implements IEntityAdditionalSpawnData {
    public static final int lifetime = 15;

    public float distance;
    private float damage;
    private int spellLevel;
    private LivingEntity caster;
    private final List<Entity> victims = new ArrayList<>();

    public PiercingBloodEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public PiercingBloodEntity(Level level, Vec3 start, Vec3 end, LivingEntity caster, int spellLevel) {
        super(OreoEntityRegistry.PIERCING_BLOOD_ENTITY.get(), level);
        this.setPos(start.subtract(0, 0.75f, 0));
        this.distance = (float) start.distanceTo(end);
        this.setRot(caster.getYRot(), caster.getXRot());
        this.caster = caster;
        this.spellLevel = spellLevel;
        this.damage = new PiercingBloodSpell().getDamage(spellLevel, caster);

        Vec3 direction = new Vec3(end.x - start.x, end.y - start.y, end.z - start.z).normalize();
        this.setDeltaMovement(direction.scale(1.25));


    }

    @Override
    public void tick() {
        super.tick();

        if (++tickCount > lifetime) {
            discard();
            return;
        }

        if (!level().isClientSide) {
            AABB hitbox = getBoundingBox().inflate(0.5);
            List<Entity> targets = level().getEntities(this, hitbox, this::canHitEntity);

            for (Entity target : targets) {
                if (!victims.contains(target)) {
                    DamageSources.applyDamage(
                            target,
                            damage,
                            OreoSpellRegistry.PiercingBloodSpell.get().getDamageSource(this, caster)
                    );
                    victims.add(target);
                }
            }
        }

        this.setPos(position().add(getDeltaMovement()));

    }

    private boolean canHitEntity(Entity entity) {
        return entity instanceof LivingEntity && entity != caster && entity.isAlive();
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.damage = tag.getFloat("Damage");
        this.spellLevel = tag.getInt("SpellLevel");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Damage", this.damage);
        tag.putInt("SpellLevel", this.spellLevel);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt((int) (distance * 10));
        buffer.writeFloat(damage);
        buffer.writeInt(spellLevel);
        buffer.writeUUID(caster != null ? caster.getUUID() : new UUID(0, 0));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.distance = buffer.readInt() / 10f;
        this.damage = buffer.readFloat();
        this.spellLevel = buffer.readInt();
        // Optional: caster is typically resolved server-side
    }

    public void setCaster(LivingEntity caster) {
        this.caster = caster;
    }

    public LivingEntity getCaster() {
        return this.caster;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setSpellLevel(int spellLevel) {
        this.spellLevel = spellLevel;
    }

    public int getSpellLevel() {
        return this.spellLevel;
    }
}
