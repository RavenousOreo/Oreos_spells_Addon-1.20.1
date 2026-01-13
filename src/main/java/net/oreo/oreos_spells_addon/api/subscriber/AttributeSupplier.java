package net.oreo.oreos_spells_addon.api.subscriber;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;

import static net.oreo.oreos_spells_addon.oreos_spells_addon.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class AttributeSupplier {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(OreoEntityRegistry.FROZEN_BLOOD_HUMANOID.get(), FrozenBloodHumanoid.prepareAttributes().build());
    }
}
