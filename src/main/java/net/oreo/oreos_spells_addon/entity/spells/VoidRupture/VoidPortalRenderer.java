package net.oreo.oreos_spells_addon.entity.spells.VoidRupture;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidPortalEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class VoidPortalRenderer extends EntityRenderer<VoidPortalEntity> {

    public static final ResourceLocation[] TEXTURES = {
            oreos_spells_addon.id("textures/entity/void_portal/void_portal1.png"),
            oreos_spells_addon.id("textures/entity/void_portal/void_portal2.png"),
            oreos_spells_addon.id("textures/entity/void_portal/void_portal3.png"),
            oreos_spells_addon.id("textures/entity/void_portal/void_portal4.png"),
            oreos_spells_addon.id("textures/entity/void_portal/void_portal5.png"),
            oreos_spells_addon.id("textures/entity/void_portal/void_portal6.png")
    };

    public VoidPortalRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(VoidPortalEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        poseStack.translate(0, 0.1, 0);  // Adjust vertical position slightly
        renderModel(poseStack, bufferSource, entity.tickCount);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90f));


        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int animOffset) {
        Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(animOffset)));

// Flat on the ground: facing up from XZ, Y is constant height
        float y = 0.0f; // height off ground
        float halfSize = 1.0f;
        float thickness = 1.0f;

        consumer.vertex(poseMatrix, -thickness, y, -halfSize).color(255, 255, 255, 255)
                .uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();

        consumer.vertex(poseMatrix, thickness, y, -halfSize).color(255, 255, 255, 255)
                .uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();

        consumer.vertex(poseMatrix, thickness, y, halfSize).color(255, 255, 255, 255)
                .uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();

        consumer.vertex(poseMatrix, -thickness, y, halfSize).color(255, 255, 255, 255)
                .uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();


    }

    @Override
    public ResourceLocation getTextureLocation(VoidPortalEntity entity) {
        return getTextureLocation(entity.tickCount);
    }

    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 1f;
        int frameIndex = (int) (offset / ticksPerFrame);

        // Clamp to last frame (index 5)
        if (frameIndex >= TEXTURES.length) {
            frameIndex = TEXTURES.length - 1;
        }

        return TEXTURES[frameIndex];
    }

}
