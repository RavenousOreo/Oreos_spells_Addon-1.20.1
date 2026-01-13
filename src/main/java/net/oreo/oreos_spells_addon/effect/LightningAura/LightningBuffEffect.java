package net.oreo.oreos_spells_addon.effect.LightningAura;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class LightningBuffEffect extends MagicMobEffect implements ISyncedMobEffect {
    public static final float LIGHTNING_SPELL_POWER_PER_LEVEL = 0.25f;
    public static final float LIGHTNING_SPELL_RES_PER_LEVEL = 0.15f;
    public static final float MANA_REGEN_PER_LEVEL = 0.10f;
    public static final float MANA_DEBUFF_PER_LEVEL = -0.17f;
    public static final UUID LIGHTNING_POWER_ID = UUID.fromString("0e0e8ae6-913e-4f35-abd4-a27266fabde1");
    public static final UUID LIGHTNING_RES_ID = UUID.fromString("fd49c378-063b-409b-9bd7-1b74fd377c65");
    public static final UUID MANA_REGEN_ID = UUID.fromString("9bcac3e1-6214-4e55-965e-3914b4888ce2");
    public static final UUID MANA_DEBUFF_ID = UUID.fromString("ab4ec3d3-650a-4e0e-a096-5a671561b0ed");

    public LightningBuffEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);

        this.addAttributeModifier(
                AttributeRegistry.LIGHTNING_SPELL_POWER.get(),
                LIGHTNING_POWER_ID.toString(),
                LightningBuffEffect.LIGHTNING_SPELL_POWER_PER_LEVEL,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                AttributeRegistry.LIGHTNING_MAGIC_RESIST.get(),
                LIGHTNING_RES_ID.toString(),
                LightningBuffEffect.LIGHTNING_SPELL_RES_PER_LEVEL,
                AttributeModifier.Operation.ADDITION
        );
    }
}
