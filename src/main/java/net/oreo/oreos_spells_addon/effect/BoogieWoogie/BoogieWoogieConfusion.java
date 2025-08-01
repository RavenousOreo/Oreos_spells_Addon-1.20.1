package net.oreo.oreos_spells_addon.effect.BoogieWoogie;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.UUID;

public class BoogieWoogieConfusion extends MobEffect {
    private static final UUID STUN_UUID = UUID.fromString("5cdd0937-1c3e-4856-9fa3-ec8d305b5dc0");

    public BoogieWoogieConfusion() {
        super(MobEffectCategory.NEUTRAL, 0x770077);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
            mob.setDeltaMovement(0, mob.getDeltaMovement().y, 0);
            mob.setTarget(null);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // So mobs keep having their AI interrupted passively
    }
}

