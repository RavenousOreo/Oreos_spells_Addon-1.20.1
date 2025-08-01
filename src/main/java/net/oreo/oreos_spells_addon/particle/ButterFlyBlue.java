package net.oreo.oreos_spells_addon.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class ButterFlyBlue extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected ButterFlyBlue(ClientLevel level, double x, double y, double z,
                            double dx, double dy, double dz, SpriteSet spriteSet) {
        super(level, x, y, z);

        this.sprites = spriteSet;

        this.setSize(0.2f, 0.2f);
        this.hasPhysics = false;


        this.xd = dx * 0.01;
        this.yd = dy * 0.01;
        this.zd = dz * 0.01;

        this.lifetime = 80 + this.random.nextInt(20); // ~2.5–3 seconds
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        // Animate over 4 frames evenly across lifespan
        int frame = (int) ((age / (float) lifetime) * 4); // 0 to 3
        frame = Math.min(frame, 3); // Clamp
        this.setSprite(sprites.get(frame, 4));

        this.yd = Math.sin(age * 0.15f) * 0.003;

        // Apply motion
        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
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
            return new ButterFlyBlue(level, x, y, z, dx, dy, dz, sprites);
        }
    }


}
