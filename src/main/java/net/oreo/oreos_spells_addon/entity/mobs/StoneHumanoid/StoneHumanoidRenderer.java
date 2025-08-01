package net.oreo.oreos_spells_addon.entity.mobs.StoneHumanoid;

import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoid;
import io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoidRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class StoneHumanoidRenderer extends FrozenHumanoidRenderer {
    private static final ResourceLocation BLOOD_TEXTURE = new ResourceLocation(oreos_spells_addon.MODID, "textures/entity/stone_humanoid.png");

    public StoneHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(FrozenHumanoid entity) {
        return BLOOD_TEXTURE;
    }
}

