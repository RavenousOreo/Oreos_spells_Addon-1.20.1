package net.oreo.oreos_spells_addon.effect.FrozenBloodEffect;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FrozenBloodEffect extends MobEffect implements ISyncedMobEffect {

    public FrozenBloodEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B0000); // Dark red color
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 4; ++i) {
                double offsetX = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
                double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
                double offsetZ = (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth();
                double velocityX = (entity.getRandom().nextDouble() - 0.5) * 0.2;
                double velocityY = (entity.getRandom().nextDouble() - 0.5) * 0.2;
                double velocityZ = (entity.getRandom().nextDouble() - 0.5) * 0.2;

            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Called every tick
    }
}