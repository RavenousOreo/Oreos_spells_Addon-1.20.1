package net.oreo.oreos_spells_addon.effect.ButterflyDamageBuff;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ButterflyEffectEventHandler {
    private static final Map<UUID, LivingEntity> DODGE_TRIGGER_MAP = new HashMap<>();

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(OreoMobEffectRegistry.BUTTERFLYDEBUFF.get())) {
            LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity e ? e : null;
            if (attacker != null) {
                DODGE_TRIGGER_MAP.put(entity.getUUID(), attacker);
                event.setCanceled(true);
            }
        }
    }

    public static LivingEntity consumeDodgeTarget(LivingEntity dodger) {
        return DODGE_TRIGGER_MAP.remove(dodger.getUUID());
    }
}

