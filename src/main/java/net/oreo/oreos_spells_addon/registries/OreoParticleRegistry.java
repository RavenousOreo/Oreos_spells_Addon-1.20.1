package net.oreo.oreos_spells_addon.registries;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class OreoParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, oreos_spells_addon.MODID);

    public static final RegistryObject<SimpleParticleType> CLEAVE_PARTICLE =
            PARTICLE_TYPES.register("cleave_slash", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> CLEAVE_PARTICLE_2 =
            PARTICLE_TYPES.register("cleave_particle_2", () -> new SimpleParticleType(true));
    //public static final RegistryObject<SimpleParticleType> CLEAVE_PARTICLE_3 =
            //PARTICLE_TYPES.register("cleave_particle_3", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> CLEAVE_PARTICLE_4 =
            PARTICLE_TYPES.register("cleave_particle_3", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLUE_BUTTERFLY_PARTICLE =
            PARTICLE_TYPES.register("blue_butterfly_particle", () -> new SimpleParticleType(true));





    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}

