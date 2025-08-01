package net.oreo.oreos_spells_addon.registries;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.oreo.oreos_spells_addon.spells.blood.*;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import net.oreo.oreos_spells_addon.spells.ender.VoidRuptureSpell;
import net.oreo.oreos_spells_addon.spells.evocation.BoogieWoogieSpell;
import net.oreo.oreos_spells_addon.spells.evocation.CleaveSpell;
import net.oreo.oreos_spells_addon.spells.evocation.DismantleSpell;
import net.oreo.oreos_spells_addon.spells.evocation.PhantomChrysalisSpell;
import net.oreo.oreos_spells_addon.spells.fire.FireAuraSpell;
import net.oreo.oreos_spells_addon.spells.ice.IceWallSpell;
import net.oreo.oreos_spells_addon.spells.lightning.ThunderClapDashSpell;
import net.oreo.oreos_spells_addon.spells.lightning.ThunderingSlashSpell;
import net.oreo.oreos_spells_addon.spells.lightning.ThunderousCrownSpell;

public class OreoSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, oreos_spells_addon.MODID);

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    public static RegistryObject<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    // blood spells
    public static final RegistryObject<AbstractSpell> BloodyLanceSpell = registerSpell(new BloodyLanceSpell());
    public static final RegistryObject<AbstractSpell> BloodyCleaveSpell = registerSpell(new BloodyCleaveSpell());
    public static final RegistryObject<AbstractSpell> FlowingRedScalesSpell = registerSpell(new FlowingRedScalesSpell());
    public static final RegistryObject<AbstractSpell> PiercingBloodSpell = registerSpell(new PiercingBloodSpell());
    public static final RegistryObject<AbstractSpell> SacrificialCleanseSpell = registerSpell(new SacrificialCleanseSpell());
    public static final RegistryObject<AbstractSpell> ThunderClapDashSpell = registerSpell(new ThunderClapDashSpell());
    public static final RegistryObject<AbstractSpell> ThunderingSlash = registerSpell(new ThunderingSlashSpell());
    //public static final RegistryObject<AbstractSpell> SanguineAscensionSpell = registerSpell(new SanguineAscensionSpell());
    public static final RegistryObject<AbstractSpell> SanguinePoolSpell = registerSpell(new SanguinePoolSpell());
    public static final RegistryObject<AbstractSpell> FrozenBloodSpell = registerSpell(new FrozenBloodSpell());
    public static final RegistryObject<AbstractSpell> DismantleSpell = registerSpell(new DismantleSpell());
    public static final RegistryObject<AbstractSpell> CleaveSpell = registerSpell(new CleaveSpell());
    public static final RegistryObject<AbstractSpell> BoogieWoogieSpell = registerSpell(new BoogieWoogieSpell());
    public static final RegistryObject<AbstractSpell> VoidRuptureSpell = registerSpell(new VoidRuptureSpell());
    public static final RegistryObject<AbstractSpell> ThunderousCrownSpell = registerSpell(new ThunderousCrownSpell());
    public static final RegistryObject<AbstractSpell> IceWallSpell = registerSpell(new IceWallSpell());
    public static final RegistryObject<AbstractSpell> PhantomChrysalisSpell = registerSpell(new PhantomChrysalisSpell());
    public static final RegistryObject<AbstractSpell> FlameAuraSpell = registerSpell(new FireAuraSpell());

}
