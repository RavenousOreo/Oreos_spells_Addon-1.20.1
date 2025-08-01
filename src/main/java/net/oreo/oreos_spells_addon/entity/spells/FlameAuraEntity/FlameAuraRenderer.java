package net.oreo.oreos_spells_addon.entity.spells.FlameAuraEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class FlameAuraRenderer extends EntityRenderer<FlameAuraEntity> {
    private static final ResourceLocation TEXTURES =
            new ResourceLocation("oreos_spells_addon", "textures/entity/flame_sigil.png");

    public FlameAuraRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FlameAuraEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float radius = entity.getRadius();
        float scale = 2.0f;

        poseStack.pushPose();;
        float spin = (entity.tickCount + partialTicks) * 2f;
        float hover = (float) Math.sin((entity.tickCount + partialTicks) * 0.1f) * 0.1f;

        poseStack.translate(0.0D, 0.25D + hover, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.scale(scale, 1.0f, scale);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURES));

        PoseStack.Pose pose = poseStack.last();

        float halfSize = 1.0f;
        float minX = -halfSize;
        float maxX = halfSize;
        float minZ = -halfSize;
        float maxZ = halfSize;

        vertexConsumer.vertex(pose.pose(), minX, 0, minZ)
                .color(255, 255, 255, 255)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        vertexConsumer.vertex(pose.pose(), minX, 0, maxZ)
                .color(255, 255, 255, 255)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        vertexConsumer.vertex(pose.pose(), maxX, 0, maxZ)
                .color(255, 255, 255, 255)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        vertexConsumer.vertex(pose.pose(), maxX, 0, minZ)
                .color(255, 255, 255, 255)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FlameAuraEntity entity) {
        return TEXTURES;
    }
}
