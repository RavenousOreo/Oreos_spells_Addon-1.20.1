package net.oreo.oreos_spells_addon.entity.spells.IceWall;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceWallRenderer extends GeoEntityRenderer<IceWallEntity> {
    public IceWallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceWallModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(IceWallEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public void applyRotations(IceWallEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
    }

}
