package net.oreo.oreos_spells_addon.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.oreo.oreos_spells_addon.effect.BoogieWoogie.BoogieWoogieConfusion;
import net.oreo.oreos_spells_addon.effect.ButterflyDamageBuff.ButterflyBuffEffect;
import net.oreo.oreos_spells_addon.effect.ButterflyDamageBuff.ButterflyDamageBuffEffect;
import net.oreo.oreos_spells_addon.effect.ChilledEffect.ChilledEffect;
import net.oreo.oreos_spells_addon.effect.FlameAura.FlameAuraDebuffEffect;
import net.oreo.oreos_spells_addon.effect.FlameAura.FlameAuraEffect;
import net.oreo.oreos_spells_addon.effect.FlowingRedScalesEffect;
import net.oreo.oreos_spells_addon.effect.FrozenBloodEffect.FrozenBloodEffect;
import net.oreo.oreos_spells_addon.effect.SanguinePoolEffect.SanguinePoolEffect;
import net.oreo.oreos_spells_addon.effect.ThunderClapDashEffect;
import net.oreo.oreos_spells_addon.oreos_spells_addon;

public class OreoMobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, oreos_spells_addon.MODID);

    public static void register(IEventBus eventBus) {
        MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
    }




    //public static final RegistryObject<MobEffect> FLOWINGREDSCALES = MOB_EFFECT_DEFERRED_REGISTER.register("flowing_red_scales", () -> new FlowingRedScalesEffect(MobEffectCategory.BENEFICIAL, 0xff4800));
    public static final RegistryObject<MobEffect> FLOWINGREDSCALES = MOB_EFFECT_DEFERRED_REGISTER.register("flowing_red_scales", () -> new FlowingRedScalesEffect(MobEffectCategory.BENEFICIAL, 0xff4800));
    public static final RegistryObject<MobEffect> THUNDERCLAPDASH = MOB_EFFECT_DEFERRED_REGISTER.register("thunder_clap_dash", () -> new ThunderClapDashEffect(MobEffectCategory.BENEFICIAL, 0xAAAAFF));
    public static final RegistryObject<MobEffect> SANGUINEPOOLEFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("sanguine_pool", () -> new SanguinePoolEffect());
    public static final RegistryObject<MobEffect> FROZENBLOODEFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("frozen_blood", () -> new FrozenBloodEffect());
    public static final RegistryObject<MobEffect> BOOGIEWOOGIECONFUSION = MOB_EFFECT_DEFERRED_REGISTER.register("boogie_woogie_confusion", () -> new BoogieWoogieConfusion());
    public static final RegistryObject<MobEffect> CHILLEDEFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("chilled", () -> new ChilledEffect(MobEffectCategory.HARMFUL, 0x00f0ff));
    public static final RegistryObject<MobEffect> BUTTERFLYBUFF = MOB_EFFECT_DEFERRED_REGISTER.register("butterfly_buff", () -> new ButterflyDamageBuffEffect(MobEffectCategory.BENEFICIAL, 0xAAAAFF));
    public static final RegistryObject<MobEffect> BUTTERFLYDEBUFF = MOB_EFFECT_DEFERRED_REGISTER.register("butterfly_debuff", () -> new ButterflyBuffEffect(MobEffectCategory.HARMFUL, 0x00f00ff));
    public static final RegistryObject<MobEffect> FLAMEAURABUFF = MOB_EFFECT_DEFERRED_REGISTER.register("flame_aura_buff", () -> new FlameAuraEffect(MobEffectCategory.BENEFICIAL, 0xAAAAFF));
    public static final RegistryObject<MobEffect> FLAMEAURADEBUFF = MOB_EFFECT_DEFERRED_REGISTER.register("flame_aura_debuff", () -> new FlameAuraDebuffEffect(MobEffectCategory.HARMFUL, 0x00f00ff));
}
