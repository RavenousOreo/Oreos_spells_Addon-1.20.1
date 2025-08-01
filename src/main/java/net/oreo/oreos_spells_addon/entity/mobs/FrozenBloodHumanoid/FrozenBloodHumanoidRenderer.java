package net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoid;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoidRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class FrozenBloodHumanoidRenderer extends LivingEntityRenderer<FrozenBloodHumanoid, HumanoidModel<FrozenBloodHumanoid>> {

    private static final ResourceLocation BLOOD_TEXTURE = new ResourceLocation("oreos_spells_addon", "textures/entity/frozenbloodhumanoid.png");

    public FrozenBloodHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.36f);
    }

    @Override
    public ResourceLocation getTextureLocation(FrozenBloodHumanoid entity) {
        return BLOOD_TEXTURE;
    }

    @Override
    protected float getBob(FrozenBloodHumanoid entity, float partialTick) {
        return 0;
    }

    @Override
    protected float getAttackAnim(FrozenBloodHumanoid entity, float partialTick) {
        return entity.getAttacktime();
    }

    @Override
    public void render(FrozenBloodHumanoid entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<>(entity, this, partialTicks, poseStack, buffer, packedLight)))
            return;

        poseStack.pushPose();
        this.model.attackTime = this.getAttackAnim(entity, partialTicks);

        boolean isSitting = entity.isSitting();
        this.model.riding = isSitting;
        this.model.young = entity.isBaby();
        float bodyYRot = entity.yBodyRot;
        float yHeadRot = entity.yHeadRot;
        float headBodyDiff = yHeadRot - bodyYRot;

        if (isSitting) {
            float clamped = Mth.clamp(headBodyDiff, -85.0F, 85.0F);
            bodyYRot = yHeadRot - clamped;
            if (clamped * clamped > 2500.0F) {
                bodyYRot += clamped * 0.2F;
            }
            headBodyDiff = yHeadRot - bodyYRot;
        }

        float headPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        if (isEntityUpsideDown(entity)) {
            headPitch *= -1.0F;
            headBodyDiff *= -1.0F;
        }

        if (entity.hasPose(Pose.SLEEPING)) {
            Direction bedDirection = entity.getBedOrientation();
            if (bedDirection != null) {
                float bedOffset = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                poseStack.translate(-bedDirection.getStepX() * bedOffset, 0.0D, -bedDirection.getStepZ() * bedOffset);
            }
        }

        float bob = 0;
        this.setupRotations(entity, poseStack, bob, bodyYRot, partialTicks);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, poseStack, partialTicks);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        float limbSwingAmount = entity.getLimbSwingAmount();
        float limbSwing = entity.getLimbSwing();

        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, bob, headBodyDiff, headPitch);

        Minecraft minecraft = Minecraft.getInstance();
        boolean visible = this.isBodyVisible(entity);
        boolean translucent = !visible && !entity.isInvisibleTo(minecraft.player);
        boolean glowing = minecraft.shouldEntityAppearGlowing(entity);

        RenderType renderType = this.getRenderType(entity, visible, translucent, glowing);
        if (renderType != null) {
            VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
            int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, overlay, 1.0F, 1.0F, 1.0F, translucent ? 0.15F : 1.0F);
        }

        poseStack.popPose();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<>(entity, this, partialTicks, poseStack, buffer, packedLight));
    }
}

