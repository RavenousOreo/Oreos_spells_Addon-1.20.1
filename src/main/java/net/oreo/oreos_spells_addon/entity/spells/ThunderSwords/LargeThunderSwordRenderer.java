package net.oreo.oreos_spells_addon.entity.spells.ThunderSwords;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class LargeThunderSwordRenderer extends EntityRenderer<LargeThunderSwordEntity> {
    private static final ResourceLocation SWORD_TEXTURE =
            new ResourceLocation("oreos_spells_addon", "textures/entity/thundersword.png");

    public LargeThunderSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LargeThunderSwordEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        poseStack.pushPose();

        // Raise slightly if needed
        poseStack.translate(0, 0.5, 0);

        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90)); // rotate locally to lay flat

        // Rotate based on entity yaw and pitch
        //poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot())); // rotate horizontally
        //poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));  // tilt vertically

        // Adjust orientation so sword faces forward like a horizontal thrust
        poseStack.mulPose(Axis.ZP.rotationDegrees(270)); // rotate so texture aligns along +X
        //poseStack.mulPose(Axis.YP.rotationDegrees(-90));

        float length = 4.5f; // Sword length along X
        float thickness = 2.0f; // sword width along Y

        VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SWORD_TEXTURE));
        Matrix4f matrix = poseStack.last().pose();

        // Draw flat horizontal sword from left (handle) to right (blade)
        builder.vertex(matrix, 0, -thickness, 0)
                .color(255, 255, 255, 255)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 0, 1)
                .endVertex();

        builder.vertex(matrix, length, -thickness, 0)
                .color(255, 255, 255, 255)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 0, 1)
                .endVertex();

        builder.vertex(matrix, length, thickness, 0)
                .color(255, 255, 255, 255)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 0, 1)
                .endVertex();

        builder.vertex(matrix, 0, thickness, 0)
                .color(255, 255, 255, 255)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 0, 1)
                .endVertex();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }


    @Override
    public ResourceLocation getTextureLocation(LargeThunderSwordEntity entity) {
        return SWORD_TEXTURE;
    }
}
