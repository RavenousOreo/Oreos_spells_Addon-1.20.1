package net.oreo.oreos_spells_addon.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.particle.ParticleRenderType;

public class ModParticleRenderTypes {

    public static final ParticleRenderType CLEAVE_CUSTOM_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }


        @Override
        public String toString() {
            return "CLEAVE_CUSTOM_RENDER_TYPE";
        }
    };

}
