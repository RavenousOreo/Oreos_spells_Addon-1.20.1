package net.oreo.oreos_spells_addon.entity.spells.SanguinePool;

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
import net.minecraft.util.Mth;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class SanguinePoolRenderer extends EntityRenderer<SanguinePoolEntity> {
    private static final ResourceLocation[] TEXTURES = {
            oreos_spells_addon.id("textures/entity/sanguine_pool/sanguinepool1.png"),
            oreos_spells_addon.id("textures/entity/sanguine_pool/sanguinepool2.png"),
            oreos_spells_addon.id("textures/entity/sanguine_pool/sanguinepool3.png"),
            oreos_spells_addon.id("textures/entity/sanguine_pool/sanguinepool4.png")
    };

    private static final int TICKS_PER_FRAME = 4; // Adjust for animation speed

    public SanguinePoolRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SanguinePoolEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float radius = entity.getRadius();
        float scale = radius / 1.0f;

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.01D, 0.0D);
        poseStack.scale(scale, 1.0f, scale);

        // Get current animation frame
        int frame = (entity.tickCount / TICKS_PER_FRAME) % TEXTURES.length;
        ResourceLocation currentTexture = TEXTURES[frame];

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(currentTexture));
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
    public ResourceLocation getTextureLocation(SanguinePoolEntity entity) {
        // Fallback, unused in render pipeline
        return TEXTURES[0];
    }
}
