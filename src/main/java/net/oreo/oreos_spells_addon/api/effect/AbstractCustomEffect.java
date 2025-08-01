package net.oreo.oreos_spells_addon.api.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractCustomEffect extends MobEffect {
    private final Map<UUID, EffectData> effectTracker = new HashMap<>();

    public AbstractCustomEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    protected abstract int getCooldown(int amplifier);

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        UUID id = entity.getUUID();
        EffectData data = effectTracker.computeIfAbsent(id, k -> new EffectData());

        if (!data.initialized) {
            onFirstTick(entity, amplifier);
            data.initialized = true;
        }

        if (data.remainingActivations > 0) {
            if (--data.cooldownTicks <= 0) {
                onIntervalTick(entity, amplifier);
                data.remainingActivations--;
                data.cooldownTicks = getCooldown(amplifier);
            }
        }

        // Remove tracking if effect is gone
        if (!entity.hasEffect(this)) {
            effectTracker.remove(id);
            onRemove(entity);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // always tick for custom timing
    }

    public void initializeEffectTracking(LivingEntity entity, int activations, int cooldownTicks) {
        EffectData data = new EffectData();
        data.remainingActivations = activations;
        data.cooldownTicks = cooldownTicks;
        data.initialized = false;
        effectTracker.put(entity.getUUID(), data);
    }

    protected abstract void onFirstTick(LivingEntity entity, int amplifier);
    protected abstract void onIntervalTick(LivingEntity entity, int amplifier);
    protected abstract void onRemove(LivingEntity entity);

    private static class EffectData {
        int remainingActivations = 0;
        int cooldownTicks = 0;
        boolean initialized = false;
    }
}
