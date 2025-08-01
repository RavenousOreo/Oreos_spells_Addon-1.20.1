package net.oreo.oreos_spells_addon.entity.spells.VoidRupture;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.oreo.oreos_spells_addon.entity.spells.Cleave.CleaveEntity;

public class VoidRuptureRenderer extends EntityRenderer<VoidRuptureEntity> {

    public VoidRuptureRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(VoidRuptureEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Rendering intentionally skipped. Visuals handled via particles.
    }

    @Override
    public ResourceLocation getTextureLocation(VoidRuptureEntity entity) {
        // Required override, but unused. Returning dummy fallback.
        return new ResourceLocation("textures/misc/white.png");
    }
}
