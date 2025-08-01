package net.oreo.oreos_spells_addon.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class CleaveSlashParticle2 extends TextureSheetParticle {
    private final SpriteSet sprites;
    private int frameAge;

    protected CleaveSlashParticle2(ClientLevel level, double x, double y, double z,
                                  double dx, double dy, double dz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.sprites = spriteSet;

        // define our degreesssssssssss help
        int[] angleSteps = { 0, 45, 90, 135, 180, 225, 270, 315 };
        int index = level.random.nextInt(angleSteps.length);
        double angle = Math.toRadians(angleSteps[index]);

        float radius = 0.65f; // Fixed distance outward kill me
        this.x += Mth.cos((float) angle) * radius;
        this.z += Mth.sin((float) angle) * radius;
        this.y += (level.random.nextDouble() * 0.8); // Range: 0.0 to 0.8



        // Reduce raw speed to prevent overshoot
        //float baseSpeed = 0.1f + level.random.nextFloat() * 0.1f;

        //this.xd = -Mth.cos((float) angle) * baseSpeed;
        //this.zd = -Mth.sin((float) angle) * baseSpeed;
        //this.yd = -0.01 + level.random.nextDouble() * 0.15; // Very subtle Y movement

        // Make it thicc
        this.quadSize = 0.25f + level.random.nextFloat() * (1.25f - 0.25f);
        this.baseQuadSize = this.quadSize; // Save base size for pulse animation


        // Appearance
        this.lifetime = 4 + level.random.nextInt(2);
        this.setSpriteFromAge(spriteSet);
        this.alpha = 0.9f;
        this.roll = (float) (level.random.nextFloat() * 2 * Math.PI);
        this.oRoll = this.roll;


        this.hasPhysics = false;
    }

    private float baseQuadSize;

    @Override
    public void tick() {
        super.tick();

        // Animate roll for visual flair
        this.roll += 0.1f;

        // Life ratio: 0 at birth → 1 at death
        float lifeRatio = (float) this.age / (float) this.lifetime;

        // Scale pulse: 1.0 → ~1.3 → 1.0 using sine wave
        float pulseScale = 1.0f + Mth.sin(lifeRatio * (float) Math.PI) * 0.3f;
        this.quadSize = this.baseQuadSize * pulseScale;


        // Fade out over time
        this.alpha = 0.9f * (1.0f - lifeRatio);

        // Update texture if animated
        this.setSpriteFromAge(this.sprites);

        // Kill particle if expired
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
            return new CleaveSlashParticle(level, x, y, z, dx, dy, dz, sprites);
        }


    }
}
