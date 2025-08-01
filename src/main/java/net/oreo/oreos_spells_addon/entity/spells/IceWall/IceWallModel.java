package net.oreo.oreos_spells_addon.entity.spells.IceWall;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceWallModel extends GeoModel<IceWallEntity> {
    @Override
    public ResourceLocation getModelResource(IceWallEntity entity) {
        return new ResourceLocation("oreos_spells_addon", "geo/icewall1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceWallEntity entity) {
        return new ResourceLocation("oreos_spells_addon", "textures/entity/ice_wall.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceWallEntity entity) {
        return new ResourceLocation("minecraft", "animations/empty.animation.json");
    }
}
