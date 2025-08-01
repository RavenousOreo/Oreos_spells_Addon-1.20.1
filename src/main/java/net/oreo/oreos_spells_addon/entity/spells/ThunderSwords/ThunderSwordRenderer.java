package net.oreo.oreos_spells_addon.entity.spells.ThunderSwords;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThunderSwordRenderer extends GeoEntityRenderer<ThunderousBeatEntity> {
    public ThunderSwordRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ThunderousBeatModel()  );
        this.shadowRadius = 0.2f;
    }

    @Override
    public void render(ThunderousBeatEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

}

