package net.oreo.oreos_spells_addon.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class CleaveParticleRendererProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public CleaveParticleRendererProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z,
                                   double dx, double dy, double dz) {
        return new CleaveParticle3CustomRenderer(level, x, y, z, spriteSet);
    }
}

