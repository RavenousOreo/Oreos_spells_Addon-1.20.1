package net.oreo.oreos_spells_addon.entity.spells.ThunderSwords;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ThunderousBeatModel extends GeoModel<ThunderousBeatEntity> {
    @Override
    public ResourceLocation getModelResource(ThunderousBeatEntity entity) {
        return new ResourceLocation("oreos_spells_addon", "geo/thunderous_beat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ThunderousBeatEntity entity) {
        return new ResourceLocation("oreos_spells_addon", "textures/entity/thundersword.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ThunderousBeatEntity entity) {
        return new ResourceLocation("oreos_spells_addon", "animations/thunderous_beat_animation.json");
    }

}
