package net.oreo.oreos_spells_addon.entity.spells.Dismantle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

import java.util.Random;

public class DismantleRenderer extends EntityRenderer<DismantleProjectile> {

    public static final ResourceLocation[] TEXTURES = {
            oreos_spells_addon.id("textures/entity/dismantle/dismantle.png"),
    };

    private static final Random random = new Random();

    public DismantleRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(DismantleProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        // Get cached values from entity
        float scale = entity.getRandomScale();
        float randomYaw = entity.getRandomYaw();

        poseStack.scale(scale, scale, scale);

        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (180F / Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (180F / Math.PI)) + 90.0F);

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(randomYaw)); // now stable per entity

        renderModel(poseStack, bufferSource, entity.getAge());

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }


    public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int animOffset) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(animOffset)));

        float halfWidth = 2;
        float halfHeight = 1;
        float thickness = 0.25f; // Thickness in pixels

            // Use the X axis to give the plane body thickness
        consumer.vertex(poseMatrix, -thickness, -halfWidth, -halfHeight).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 1f, 0f, 0f).endVertex();
        consumer.vertex(poseMatrix, -thickness, halfWidth, -halfHeight).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 1f, 0f, 0f).endVertex();
        consumer.vertex(poseMatrix, -thickness, halfWidth, halfHeight).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 1f, 0f, 0f).endVertex();
        consumer.vertex(poseMatrix, -thickness, -halfWidth, halfHeight).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 1f, 0f, 0f).endVertex();

    }

    @Override
    public ResourceLocation getTextureLocation(DismantleProjectile entity) {
        return getTextureLocation(entity.getAge());
    }

    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 1f;
        return TEXTURES[(int) (offset / ticksPerFrame) % TEXTURES.length];
    }
}
