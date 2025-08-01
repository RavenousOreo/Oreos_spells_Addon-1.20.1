package net.oreo.oreos_spells_addon.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.render.ModParticleRenderTypes;
import org.joml.Matrix4f;

public class CleaveParticle3CustomRenderer extends Particle {
    private final SpriteSet spriteSet;
    private final float baseQuadSize;
    private final float angleToCenter;

    public CleaveParticle3CustomRenderer(ClientLevel level, double centerX, double y, double centerZ,
                                         SpriteSet spriteSet) {
        super(level, centerX, y, centerZ, 0, 0, 0);
        this.spriteSet = spriteSet;
        this.lifetime = 8;
        this.baseQuadSize = (0.5f + level.random.nextFloat() * 0.3f) * 0.75f;
        this.alpha = 0.9f;

        // Choose a direction
        int[] angleSteps = {0, 45, 90, 135, 180, 225, 270, 315};
        double angleRad = Math.toRadians(angleSteps[level.random.nextInt(angleSteps.length)]);

        // Move particle out from center
        double radius = 1.0;
        double offsetX = Math.cos(angleRad) * radius;
        double offsetZ = Math.sin(angleRad) * radius;
        this.setPos(centerX + offsetX, y + level.random.nextDouble() * 0.8, centerZ + offsetZ);

        // Motion away from center
        double dx = offsetX * 0.08;
        double dz = offsetZ * 0.08;
        this.xd = dx;
        this.zd = dz;

        // Yaw angle in radians
        this.angleToCenter = (float) Math.atan2(this.z - centerZ, this.x - centerX);



    }

    @Override
    public void tick() {
        super.tick();
        float lifeRatio = (float) age / lifetime;
        this.alpha = 0.9f * (1.0f - lifeRatio);
        this.currentSize = baseQuadSize * (1.0f + Mth.sin(lifeRatio * (float) Math.PI) * 0.3f);
        if (++this.age >= this.lifetime) this.remove();
    }

    private float currentSize;

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float px = (float)(this.x - camPos.x);
        float py = (float)(this.y - camPos.y);
        float pz = (float)(this.z - camPos.z);

        float lifeRatio = (float) age / lifetime;
        float alpha = 0.9f * (1.0f - lifeRatio);
        float size = currentSize;



        // Camera basis vectors from rotation quaternion
        Vec3 viewLeft = new Vec3(1, 0, 0).xRot(camera.getXRot() * ((float) Math.PI / 180F))
                .yRot(-camera.getYRot() * ((float) Math.PI / 180F));
        Vec3 viewUp = new Vec3(0, 1, 0).xRot(camera.getXRot() * ((float) Math.PI / 180F))
                .yRot(-camera.getYRot() * ((float) Math.PI / 180F));

        float flicker = 0.85f + 0.15f * Mth.sin(age * 2f); // fast sine wave
        this.alpha = flicker * (1.0f - lifeRatio);

        // Add minor scale flicker
        this.currentSize = baseQuadSize * (1.0f + Mth.sin(lifeRatio * (float) Math.PI) * 0.3f);
        this.currentSize *= 0.95f + 0.05f * Mth.cos(age * 4f); // extra distortion

        // Optional flicker scale effect
        float flick = 1.0f + (random.nextFloat() - 0.5f) * 0.15f; // ±7.5%
        this.currentSize = baseQuadSize * flick * (1.0f + Mth.sin(lifeRatio * (float) Math.PI) * 0.3f);

        // Calculate rotated left/up vectors using angleToCenter
        float sin = Mth.sin(angleToCenter);
        float cos = Mth.cos(angleToCenter);

        float ux = (float)(viewUp.x * cos + viewLeft.x * sin);
        float uy = (float)(viewUp.y * cos + viewLeft.y * sin);
        float uz = (float)(viewUp.z * cos + viewLeft.z * sin);

        float lx = (float)(viewLeft.x * cos - viewUp.x * sin);
        float ly = (float)(viewLeft.y * cos - viewUp.y * sin);
        float lz = (float)(viewLeft.z * cos - viewUp.z * sin);

        TextureAtlasSprite sprite = spriteSet.get(age, lifetime);
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        int light = LightTexture.FULL_BRIGHT;

        float uvJitter = 0.003f * (random.nextFloat() - 0.5f);
        u0 += uvJitter;
        u1 += uvJitter;
        v0 += uvJitter;
        v1 += uvJitter;

        // Compute each vertex (billboard + rotated UVs)
        buffer.vertex(px - lx - ux, py - ly - uy, pz - lz - uz).uv(u0, v1).color(1f, 1f, 1f, alpha).uv2(light).normal(0f, 1f, 0f).endVertex();
        buffer.vertex(px - lx + ux, py - ly + uy, pz - lz + uz).uv(u0, v0).color(1f, 1f, 1f, alpha).uv2(light).normal(0f, 1f, 0f).endVertex();
        buffer.vertex(px + lx + ux, py + ly + uy, pz + lz + uz).uv(u1, v0).color(1f, 1f, 1f, alpha).uv2(light).normal(0f, 1f, 0f).endVertex();
        buffer.vertex(px + lx - ux, py + ly - uy, pz + lz - uz).uv(u1, v1).color(1f, 1f, 1f, alpha).uv2(light).normal(0f, 1f, 0f).endVertex();
    }





    @Override
    public ParticleRenderType getRenderType() {
        return ModParticleRenderTypes.CLEAVE_CUSTOM_RENDER_TYPE;
    }

}

