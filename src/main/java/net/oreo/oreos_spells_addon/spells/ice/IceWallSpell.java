package net.oreo.oreos_spells_addon.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.oreo.oreos_spells_addon.entity.spells.IceWall.IceWallEntity;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.registries.OreoEntityRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoSpellConfig
public class IceWallSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(oreos_spells_addon.MODID, "ice_wall");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.duration", getDurationText(spellLevel, caster)),
                Component.translatable("ui.irons_spellbooks.hp", Utils.stringTruncation(getWallHP(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(20)
            .build();

    public IceWallSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 5;
        this.castTime = 20;
        this.baseManaCost = 50;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
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
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        Vec3 lookVec = caster.getLookAngle().normalize();
        Vec3 flatLookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 bodyPos = caster.position().add(0, caster.getBbHeight() * 0.0, 0);
        Vec3 wallPos = bodyPos.add(flatLookVec.scale(3.0));

        IceWallEntity wall = new IceWallEntity(OreoEntityRegistry.ICEWALLENTITY.get(), level);
        wall.setPos(wallPos.x, wallPos.y, wallPos.z);
        float yaw = (float) Math.toDegrees(Math.atan2(-flatLookVec.x, flatLookVec.z));
        wall.setYRot(yaw);
        wall.setXRot(0);
        wall.yRotO = yaw; // Previous frame yaw — needed for interpolation
        wall.xRotO = 0;
        //wall.setDirectionalBoundingBox(flatLookVec, 8.0, 2.0, 0.5);
        wall.setHealth(getWallHP(spellLevel, caster));







        // Calculate duration in ticks
        float spellPower = getSpellPower(spellLevel, caster);
        int durationTicks = (int) ((10f * spellLevel + spellPower) * 10f);

        wall.setLifetimeTicks(durationTicks);
        level.addFreshEntity(wall);
    }

    private float getWallHP(int spellLevel, LivingEntity caster) {
        return 10 + getSpellPower(spellLevel, caster);
    }


    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_RAISED_HAND;
    }

    private float getDurationText(int spellLevel, @Nullable LivingEntity caster) {
        float spellPower = getSpellPower(spellLevel, caster);
        return 10f * spellLevel + spellPower; // in seconds
    }
}
