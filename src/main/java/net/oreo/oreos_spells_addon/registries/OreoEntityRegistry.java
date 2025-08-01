package net.oreo.oreos_spells_addon.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oreo.oreos_spells_addon.entity.HelixEffectEntity;
import net.oreo.oreos_spells_addon.entity.mobs.FrozenBloodHumanoid.FrozenBloodHumanoid;
import net.oreo.oreos_spells_addon.entity.mobs.StoneHumanoid.StoneHumanoid;
import net.oreo.oreos_spells_addon.entity.spells.BloodyCleave.BloodyCleave;
import net.oreo.oreos_spells_addon.entity.spells.Cleave.CleaveEntity;
import net.oreo.oreos_spells_addon.entity.spells.Dismantle.DismantleProjectile;
import net.oreo.oreos_spells_addon.entity.spells.FlameAuraEntity.FlameAuraEntity;
import net.oreo.oreos_spells_addon.entity.spells.IceWall.IceWallEntity;
import net.oreo.oreos_spells_addon.entity.spells.PiercingBlood.PiercingBloodEntity;
import net.oreo.oreos_spells_addon.entity.spells.ThunderSwords.LargeThunderSwordEntity;
import net.oreo.oreos_spells_addon.entity.spells.ThunderSwords.ThunderousBeatEntity;
import net.oreo.oreos_spells_addon.entity.spells.Thundering_Slash.ThunderingSlash;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidPortalEntity;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidRuptureEntity;
import net.oreo.oreos_spells_addon.entity.spells.VoidRupture.VoidSwordProjectile;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.entity.spells.BloodyLance.BloodyLanceProjectile;
import net.oreo.oreos_spells_addon.entity.spells.SanguinePool.SanguinePoolEntity;

public class OreoEntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, oreos_spells_addon.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }


    public static final RegistryObject<EntityType<BloodyLanceProjectile>> BLOODY_LANCE_PROJECTILE =
            ENTITIES.register("bloody_lance", () -> EntityType.Builder.<BloodyLanceProjectile>of(BloodyLanceProjectile::new, MobCategory.MISC)
                    .sized(1.25f, 1.25f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "bloody_lance").toString()));




    public static final RegistryObject<EntityType<BloodyCleave>> BLOODY_CLEAVE =
            ENTITIES.register("bloody_cleave", () -> EntityType.Builder.<BloodyCleave>of(BloodyCleave::new, MobCategory.MISC)
                    .sized(5f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "bloody_cleave").toString()));

    public static final RegistryObject<EntityType<PiercingBloodEntity>> PIERCING_BLOOD_ENTITY =
            ENTITIES.register("piercing_blood", () -> EntityType.Builder.<PiercingBloodEntity>of(PiercingBloodEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "piercing_blood").toString()));

    public static final RegistryObject<EntityType<ThunderingSlash>> THUNDERING_SLASH =
            ENTITIES.register("thundering_slash", () -> EntityType.Builder.<ThunderingSlash>of(ThunderingSlash::new, MobCategory.MISC)
                    .sized(5f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "thundering_slash").toString()));

    public static final RegistryObject<EntityType<HelixEffectEntity>> HELIX_EFFECT_ENTITY =
            ENTITIES.register("helix_effect_entity", () -> EntityType.Builder.<HelixEffectEntity>of(HelixEffectEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "helix_effect_entity").toString()));

    public static final RegistryObject<EntityType<SanguinePoolEntity>> SANGUINE_POOL_ENTITY =
            ENTITIES.register("sanguine_pool_entity", () -> EntityType.Builder.<SanguinePoolEntity>of(SanguinePoolEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "sanguine_pool_entity").toString()));

    public static final RegistryObject<EntityType<StoneHumanoid>> STONE_HUMANOID =
            ENTITIES.register("stone_humanoid", () -> EntityType.Builder.<StoneHumanoid>of(StoneHumanoid::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "stone_humanoid").toString()));

    public static final RegistryObject<EntityType<FrozenBloodHumanoid>> FROZEN_BLOOD_HUMANOID =
            ENTITIES.register("frozen_blood_humanoid", () -> EntityType.Builder.<FrozenBloodHumanoid>of(FrozenBloodHumanoid::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "frozen_blood_humanoid").toString()));

    public static final RegistryObject<EntityType<DismantleProjectile>> DISMANTLE =
            ENTITIES.register("dismantle", () -> EntityType.Builder.<DismantleProjectile>of(DismantleProjectile::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "dismantle").toString()));

    public static final RegistryObject<EntityType<CleaveEntity>> CLEAVE =
            ENTITIES.register("cleave", () -> EntityType.Builder.<CleaveEntity>of(CleaveEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "cleave").toString()));

    public static final RegistryObject<EntityType<VoidRuptureEntity>> VOIDRUPTUREENTITY =
            ENTITIES.register("void_rupture_entity", () -> EntityType.Builder.<VoidRuptureEntity>of(VoidRuptureEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "void_rupture_entity").toString()));

    public static final RegistryObject<EntityType<VoidPortalEntity>> VOIDPORTALENTITY =
            ENTITIES.register("void_portal_entity", () -> EntityType.Builder.<VoidPortalEntity>of(VoidPortalEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "void_portal_entity").toString()));

    public static final RegistryObject<EntityType<VoidSwordProjectile>> VOIDSWORDPROJECTILE =
            ENTITIES.register("void_sword_projectile", () -> EntityType.Builder.<VoidSwordProjectile>of(VoidSwordProjectile::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "void_sword_projectile").toString()));

    public static final RegistryObject<EntityType<ThunderousBeatEntity>> THUNDEROUSBEATENTITY =
            ENTITIES.register("thunderous_beat_entity", () -> EntityType.Builder.<ThunderousBeatEntity>of(ThunderousBeatEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "thunderous_beat_entity").toString()));

    public static final RegistryObject<EntityType<LargeThunderSwordEntity>> LARGETHUNDERSWORDENTITY =
            ENTITIES.register("large_thunder_sword_entity", () -> EntityType.Builder.<LargeThunderSwordEntity>of(LargeThunderSwordEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "large_thunder_sword_entity").toString()));

    public static final RegistryObject<EntityType<IceWallEntity>> ICEWALLENTITY =
            ENTITIES.register("ice_wall_entity", () -> EntityType.Builder.<IceWallEntity>of(IceWallEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "ice_wall_entity").toString()));

    public static final RegistryObject<EntityType<FlameAuraEntity>> FLAMEAURAENTITY =
            ENTITIES.register("flame_aura_entity", () -> EntityType.Builder.<FlameAuraEntity>of(FlameAuraEntity::new, MobCategory.MISC)
                    .sized(2f, 2f)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(oreos_spells_addon.MODID, "flame_aura_entity").toString()));

}
