package net.oreo.oreos_spells_addon.entity.mobs.StoneHumanoid;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoid;

public class StoneHumanoid extends FrozenHumanoid {

    public StoneHumanoid(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public StoneHumanoid(Level level, LivingEntity entityToCopy) {
        super(level, entityToCopy);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.STONE_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STONE_BREAK;
    }
}
