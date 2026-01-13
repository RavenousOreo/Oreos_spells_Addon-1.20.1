package net.oreo.oreos_spells_addon.effect;

import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ThunderClapDashEffect extends MagicMobEffect implements ISyncedMobEffect {
    public ThunderClapDashEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // no periodic effect
    }
}

