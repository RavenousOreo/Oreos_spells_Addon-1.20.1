
    package net.oreo.oreos_spells_addon.spells.evocation;

    import io.redspace.ironsspellbooks.api.config.DefaultConfig;
    import io.redspace.ironsspellbooks.api.magic.MagicData;
    import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
    import io.redspace.ironsspellbooks.api.spells.*;
    import io.redspace.ironsspellbooks.api.util.AnimationHolder;
    import io.redspace.ironsspellbooks.api.util.Utils;
    import net.minecraft.core.BlockPos;
    import net.minecraft.network.chat.Component;
    import net.minecraft.network.chat.MutableComponent;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraft.server.level.ServerPlayer;
    import net.minecraft.sounds.SoundEvent;
    import net.minecraft.world.effect.MobEffectInstance;
    import net.minecraft.world.entity.LivingEntity;
    import net.minecraft.world.entity.player.Player;
    import net.minecraft.world.level.Level;
    import net.minecraft.world.phys.AABB;
    import net.minecraft.world.phys.EntityHitResult;
    import net.minecraft.world.phys.HitResult;
    import net.minecraft.world.phys.Vec3;
    import net.oreo.oreos_spells_addon.oreos_spells_addon;
    import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;
    import net.oreo.oreos_spells_addon.registries.OreoSoundRegistry;
    import org.jetbrains.annotations.Nullable;

    import java.util.Comparator;
    import java.util.List;
    import java.util.Optional;

    @AutoSpellConfig
    public class BoogieWoogieSpell extends AbstractSpell {

        private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "boogie_woogie");

        private final DefaultConfig defaultConfig = new DefaultConfig()
                .setMinRarity(SpellRarity.RARE)
                .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds(15)
                .build();

        public BoogieWoogieSpell() {
            this.manaCostPerLevel = 10;
            this.baseSpellPower = 1;
            this.spellPowerPerLevel = 1;
            this.castTime = 5;
            this.baseManaCost = 15;
        }

        private float getRange(int spellLevel, LivingEntity caster) {
            return spellLevel * 10f;
        }

        private @Nullable LivingEntity getLookTarget(LivingEntity caster, double range) {
            Vec3 start = caster.getEyePosition();
            Vec3 end = start.add(caster.getLookAngle().scale(range));
            AABB box = caster.getBoundingBox().expandTowards(caster.getLookAngle().scale(range)).inflate(1.0);

            return caster.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != caster && e.isPickable())
                    .stream()
                    .filter(e -> e.getBoundingBox().clip(start, end).isPresent())
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(caster)))
                    .orElse(null);
        }

        private List<LivingEntity> getLivingEntitiesAround(Level level, Vec3 pos, double radius) {
            AABB area = new AABB(pos, pos).inflate(radius);
            return level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive);
        }

        @Override
        public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
            return List.of(
                    Component.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(getRange(spellLevel, caster), 1))
            );
        }

        @Override
        public Optional<SoundEvent> getCastStartSound() {
            return Optional.of(OreoSoundRegistry.BOOGIE_WOOGIE_CLAP.get());
        }

        @Override
        public Optional<SoundEvent> getCastFinishSound() {
            return Optional.empty();
        }

        @Override
        public CastType getCastType() {
            return CastType.INSTANT;
        }

        @Override
        public DefaultConfig getDefaultConfig() {
            return defaultConfig;
        }

        @Override
        public ResourceLocation getSpellResource() {
            return spellId;
        }

        @Override
        public boolean canBeInterrupted(@Nullable Player player) {
            return false;
        }

        private Vec3 getSafeTeleportPos(Level level, Vec3 position) {
            BlockPos pos = BlockPos.containing(position);
            int maxHeight = level.getMaxBuildHeight();

            while (pos.getY() < maxHeight - 2) {
                boolean feetFree = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
                boolean headFree = level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();

                if (feetFree && headFree) {
                    break;
                }

                pos = pos.above();
            }

            return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }

        @Override
        public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
            double range = getRange(spellLevel, caster);
            double tolerance = 0.975;

            Vec3 origin = caster.getEyePosition();
            Vec3 lookVec = caster.getLookAngle();

            LivingEntity target = level.getEntitiesOfClass(LivingEntity.class, caster.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.5))
                    .stream()
                    .filter(e -> e != caster && e.isAlive() && origin.distanceTo(e.getEyePosition()) <= range)
                    .filter(e -> {
                        Vec3 dirToEntity = e.getEyePosition().subtract(origin).normalize();
                        return lookVec.dot(dirToEntity) > tolerance;
                    })
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(caster)))
                    .orElse(null);

            if (target == null) return;

            boolean isAlly = target.isAlliedTo(caster);
            boolean isShift = caster.isShiftKeyDown();

            if (isAlly) {
                if (isShift) {
                    List<LivingEntity> nearby = getLivingEntitiesAround(level, target.position(), 8);
                    nearby.removeIf(e -> e == caster || e == target);
                    if (!nearby.isEmpty()) {
                        LivingEntity closest = nearby.get(0);
                        Vec3 safe = getSafeTeleportPos(level, closest.position());
                        target.teleportTo(safe.x, safe.y, safe.z);
                    }
                } else {
                    Vec3 behindCaster = caster.position().add(caster.getLookAngle().scale(1.5));
                    Vec3 safe = getSafeTeleportPos(level, behindCaster);
                    target.teleportTo(safe.x, safe.y, safe.z);
                }
            } else {
                if (isShift) {
                    Vec3 from = caster.position();
                    Vec3 to = target.position();
                    Vec3 direction = from.vectorTo(to).normalize();
                    Vec3 backOff = to.subtract(direction.scale(3));
                    Vec3 safe = getSafeTeleportPos(level, backOff);
                    caster.teleportTo(safe.x, safe.y, safe.z);
                } else {
                    Vec3 behindCaster = caster.position().add(caster.getLookAngle().scale(1.5));
                    Vec3 safe = getSafeTeleportPos(level, behindCaster);
                    if (target instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.teleport(safe.x, safe.y, safe.z, serverPlayer.getYRot(), serverPlayer.getXRot());
                    } else {
                        target.teleportTo(safe.x, safe.y, safe.z);
                    }

                    target.setDeltaMovement(0, 0, 0);
                    target.hurtMarked = true;
                    int duration = 5;
                    target.addEffect(new MobEffectInstance(OreoMobEffectRegistry.BOOGIEWOOGIECONFUSION.get(), duration, spellLevel - 1));
                }
            }
        }

        @Override
        public AnimationHolder getCastStartAnimation() {
            return SpellAnimations.ANIMATION_CONTINUOUS_CAST;
        }

        @Override
        public AnimationHolder getCastFinishAnimation() {
            return SpellAnimations.FINISH_ANIMATION;
        }
    }
