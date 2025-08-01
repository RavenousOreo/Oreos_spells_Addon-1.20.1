package net.oreo.oreos_spells_addon.effect.FrozenBloodEffect;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;

import java.util.UUID;

@Mod.EventBusSubscriber
public class FrozenBloodEffectHandler {

    private static final UUID FROZEN_BLOOD_SPEED_MODIFIER_UUID = UUID.fromString("945dccbd-6ed4-49f8-bbc8-ba1b92bd905c");
    private static final UUID FROZEN_BLOOD_KNOCKBACK_MODIFIER_UUID = UUID.fromString("bbd8e7b2-bfa2-4c4f-8de7-f058b91f1241");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player &&
                player.hasEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onSpellCastAttempt(SpellPreCastEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer &&
                serverPlayer.getEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get()) != null) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(
                    Component.literal("You cannot cast while pooled in blood!").withStyle(ChatFormatting.RED),
                    true
            );
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer &&
                serverPlayer.getEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get()) != null) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(
                    Component.literal("You cannot use items in this form!").withStyle(ChatFormatting.RED),
                    true
            );
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer &&
                serverPlayer.getEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get()) != null) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(
                    Component.literal("You cannot attack while in this form!").withStyle(ChatFormatting.RED),
                    true
            );
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer &&
                serverPlayer.getEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get()) != null) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(
                    Component.literal("You cannot attack while in this form!").withStyle(ChatFormatting.RED),
                    true
            );
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        AttributeInstance movementAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackAttr = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (movementAttr == null || knockbackAttr == null) return;

        boolean hasEffect = player.hasEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get());

        if (hasEffect) {
            // Movement freeze
            if (movementAttr.getModifier(FROZEN_BLOOD_SPEED_MODIFIER_UUID) == null) {
                movementAttr.addTransientModifier(new AttributeModifier(
                        FROZEN_BLOOD_SPEED_MODIFIER_UUID,
                        "frozen_blood_speed_debuff",
                        -1.0D,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }

            // Knockback immunity
            if (knockbackAttr.getModifier(FROZEN_BLOOD_KNOCKBACK_MODIFIER_UUID) == null) {
                knockbackAttr.addTransientModifier(new AttributeModifier(
                        FROZEN_BLOOD_KNOCKBACK_MODIFIER_UUID,
                        "frozen_blood_knockback_resist",
                        100.0D,
                        AttributeModifier.Operation.ADDITION
                ));
            }

            // Lock motion
            player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
            player.fallDistance = 0;

        } else {
            // Cleanup
            if (movementAttr.getModifier(FROZEN_BLOOD_SPEED_MODIFIER_UUID) != null) {
                movementAttr.removeModifier(FROZEN_BLOOD_SPEED_MODIFIER_UUID);
            }
            if (knockbackAttr.getModifier(FROZEN_BLOOD_KNOCKBACK_MODIFIER_UUID) != null) {
                knockbackAttr.removeModifier(FROZEN_BLOOD_KNOCKBACK_MODIFIER_UUID);
            }
        }
    }
}
