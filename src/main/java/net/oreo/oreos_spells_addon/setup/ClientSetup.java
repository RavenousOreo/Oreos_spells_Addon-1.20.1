package net.oreo.oreos_spells_addon.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.oreo.oreos_spells_addon.entity.HelixEffectRenderer;
import net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid;
import net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoidRenderer;
import net.oreo.oreos_spells_addon.entity.spells.BloodyCleave.BloodyCleaveRenderer;
import net.oreo.oreos_spells_addon.entity.spells.BloodyLance.BloodyLanceRenderer;
import net.oreo.oreos_spells_addon.entity.spells.Cleave.CleaveRenderer;
import net.oreo.oreos_spells_addon.entity.spells.Dismantle.DismantleProjectile;
import net.oreo.oreos_spells_addon.entity.spells.Dismantle.DismantleRenderer;
import net.oreo.oreos_spells_addon.entity.spells.FlameAuraEntity.FlameAuraEntity;
import net.oreo.oreos_spells_addon.entity.spells.FlameAuraEntity.FlameAuraRenderer;
import net.oreo.oreos_spells_addon.entity.spells.IceWall.IceWallRenderer;
import net.oreo.oreos_spells_addon.entity.spells.PiercingBlood.PiercingBloodRenderer;
import net.oreo.oreos_spells_addon.entity.spells.SanguinePool.SanguinePoolRenderer;
import net.oreo.oreos_spells_addon.entity.spells.ThunderSwords.LargeThunderSwordRenderer;
import net.oreo.oreos_spells_addon.entity.spells.ThunderSwords.ThunderSwordRenderer;
import net.oreo.oreos_spells_addon.entity.spells.Thundering_Slash.ThunderingSlashRenderer;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidPortalRenderer;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidRuptureRenderer;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidSwordRenderer;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.particle.*;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import net.oreo.oreos_spells_addon.registries.OreoParticleRegistry;
import net.oreo.oreos_spells_addon.render.OreoChargeSpellRender;

@Mod.EventBusSubscriber(modid = oreos_spells_addon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OreoEntityRegistry.BLOODY_LANCE_PROJECTILE.get(), BloodyLanceRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.BLOODY_CLEAVE.get(), BloodyCleaveRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.PIERCING_BLOOD_ENTITY.get(), PiercingBloodRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.THUNDERING_SLASH.get(), ThunderingSlashRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.HELIX_EFFECT_ENTITY.get(), HelixEffectRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.SANGUINE_POOL_ENTITY.get(), SanguinePoolRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.FROZEN_BLOOD_HUMANOID.get(), FrozenBloodHumanoidRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.DISMANTLE.get(), DismantleRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.CLEAVE.get(), CleaveRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.VOIDRUPTUREENTITY.get(), VoidRuptureRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.VOIDPORTALENTITY.get(), VoidPortalRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.VOIDSWORDPROJECTILE.get(), VoidSwordRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.THUNDEROUSBEATENTITY.get(), ThunderSwordRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.LARGETHUNDERSWORDENTITY.get(), LargeThunderSwordRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.ICEWALLENTITY.get(), IceWallRenderer::new);
        event.registerEntityRenderer(OreoEntityRegistry.FLAMEAURAENTITY.get(), FlameAuraRenderer::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.AddLayers event) {
        addLayerToPlayerSkin(event, "default");
        addLayerToPlayerSkin(event, "slim");
    }

    private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, String skinName) {
        EntityRenderer<? extends Player> render = event.getSkin(skinName);
        if (render instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new OreoChargeSpellRender.Vanilla<>(livingRenderer));

        }
    }



    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(OreoParticleRegistry.CLEAVE_PARTICLE.get(), CleaveSlashParticle.Provider::new);
        event.registerSpriteSet(OreoParticleRegistry.CLEAVE_PARTICLE_2.get(), CleaveSlashParticle2.Provider::new);
        //event.registerSpriteSet(OreoParticleRegistry.CLEAVE_PARTICLE_3.get(), CleaveParticle3.Provider::new);
        event.registerSpriteSet(OreoParticleRegistry.BLUE_BUTTERFLY_PARTICLE.get(), ButterFlyBlue.Provider::new);

        event.registerSpriteSet(OreoParticleRegistry.CLEAVE_PARTICLE_4.get(), CleaveParticleRendererProvider::new);
    }

    public class ModEntityAttributes {
    @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(OreoEntityRegistry.FROZEN_BLOOD_HUMANOID.get(), FrozenBloodHumanoid.prepareAttributes().build());
        }
    }
}


