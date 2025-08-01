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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class VoidSwordRenderer extends EntityRenderer<VoidSwordProjectile> {

    public static final ResourceLocation[] TEXTURES = {
            oreos_spells_addon.id("textures/entity/void_sword/void_sword0.png"),
            oreos_spells_addon.id("textures/entity/void_sword/void_sword1.png"),
            oreos_spells_addon.id("textures/entity/void_sword/void_sword2.png"),
            oreos_spells_addon.id("textures/entity/void_sword/void_sword3.png"),
            oreos_spells_addon.id("textures/entity/void_sword/void_sword4.png")
    };

    public VoidSwordRenderer(Context context) {
        super(context);
    }



    @Override
    public void render(VoidSwordProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();



        poseStack.translate(0.0D, 0.2D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-45f)); // Rotates the flat plane

        if (entity.isStuckInGround()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(entity.getRandomXRotation()));
            poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRandomZRotation()));
        }


        renderModel(poseStack, bufferSource, entity.tickCount);

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int animOffset) {
        Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(animOffset)));

        float halfWidth = 1f;
        float halfHeight = 1f;
        float angleCorrection = 55f;

        // Single vertical plane, facing forward
        consumer.vertex(poseMatrix, 0, -halfWidth, -halfHeight).color(255, 255, 255, 255)
                .uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, halfWidth, -halfHeight).color(255, 255, 255, 255)
                .uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, halfWidth, halfHeight).color(255, 255, 255, 255)
                .uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, -halfWidth, halfHeight).color(255, 255, 255, 255)
                .uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.pack(15, 0)).normal(normalMatrix, 0f, 1f, 0f).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(VoidSwordProjectile entity) {
        return getTextureLocation(entity.tickCount);
    }

    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 1f;
        return TEXTURES[(int) (offset / ticksPerFrame) % TEXTURES.length];
    }
}

