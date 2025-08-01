package net.oreo.oreos_spells_addon.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.example.registry.SoundRegistry;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class OreoSoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, oreos_spells_addon.MODID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    public static RegistryObject<SoundEvent> BOOGIE_WOOGIE_CLAP = registerSoundEvent("boogie_woogie_clap");
    public static RegistryObject<SoundEvent> THUNDEROUS_BEAT_SOUND = registerSoundEvent("thunderous_beat_sound");





    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(oreos_spells_addon.MODID, name)));
    }
}
