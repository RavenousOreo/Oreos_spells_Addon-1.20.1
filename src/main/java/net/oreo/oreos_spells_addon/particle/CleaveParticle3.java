package net.oreo.oreos_spells_addon.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class CleaveParticle3 extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float baseQuadSize;

    protected CleaveParticle3(ClientLevel level, double x, double y, double z,
                              double dx, double dy, double dz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.sprites = spriteSet;

        // Set particle lifetime
        this.lifetime = 6 + level.random.nextInt(3); // 6–8 ticks

        // Random size between 0.25–0.85
        this.baseQuadSize = 0.25f + level.random.nextFloat() * (0.85f - 0.25f);
        this.quadSize = this.baseQuadSize;

        // Choose a random outward angle
        int[] angleSteps = {0, 45, 90, 135, 180, 225, 270, 315};
        int index = level.random.nextInt(angleSteps.length);
        double angle = Math.toRadians(angleSteps[index]);

        // Move spawn point outward from center
        float radius = 1.4f;
        double offsetX = Mth.cos((float) angle) * radius;
        double offsetZ = Mth.sin((float) angle) * radius;
        double offsetY = (level.random.nextDouble() * 0.8);
        this.move(offsetX, offsetY, offsetZ);


        // Now apply outward motion (moving away from center)
        float motionSpeed = 0.05f + level.random.nextFloat() * 0.05f;
        this.xd = offsetX * motionSpeed;
        this.zd = offsetZ * motionSpeed;
        this.yd = (level.random.nextDouble() * 0.02) - 0.01; // Tiny vertical drift

        // No roll
        this.roll = 0;
        this.oRoll = 0;

        // Visuals
        this.alpha = 0.9f;
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        float lifeRatio = (float) this.age / (float) this.lifetime;

        // Pulsing scale
        float pulseScale = 1.0f + Mth.sin(lifeRatio * (float) Math.PI) * 0.3f;
        this.quadSize = this.baseQuadSize * pulseScale;

        // Fade out
        this.alpha = 0.9f * (1.0f - lifeRatio);

        this.setSpriteFromAge(this.sprites);

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new CleaveParticle3(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
