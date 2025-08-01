package net.oreo.oreos_spells_addon.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.oreo.oreos_spells_addon.entity.HelixEffectEntity;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class HelixEffectRenderer extends EntityRenderer<HelixEffectEntity> {

    public HelixEffectRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(HelixEffectEntity entity) {
        // Not used — entity is particle-only
        return new ResourceLocation(oreos_spells_addon.MODID, "textures/empty.png");
    }

    @Override
    public boolean shouldRender(HelixEffectEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        return false; // Don't render the entity
    }

}
