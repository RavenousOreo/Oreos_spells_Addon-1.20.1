package net.oreo.oreos_spells_addon.effect.SanguinePoolEffect;

import io.redspace.ironsspellbooks.api.events.ChangeManaEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oreo.oreos_spells_addon.Util.StepHeightUtil;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mod.EventBusSubscriber
public class SanguinePoolEffectHandler {
    private static final UUID SANGUINE_SPEED_MODIFIER_UUID = UUID.fromString("2320ab9a-ed6e-46ac-87aa-eb1e658578d3");


    @Mixin(Entity.class)
    public interface EntityAccessor {
        @Accessor("maxUpStep")
        float getMaxUpStep();

        @Accessor("maxUpStep")
        void setMaxUpStep(float value);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.hasEffect(OreoMobEffectRegistry.SANGUINEPOOLEFFECT.get())) {
            event.setCanceled(true); // Negates the damage entirely
        }
    }

    @SubscribeEvent
    public static void onSpellCastAttempt(SpellPreCastEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.level().isClientSide()) return;

            MobEffectInstance effect = serverPlayer.getEffect(OreoMobEffectRegistry.SANGUINEPOOLEFFECT.get());
            if (effect != null) {
                event.setCanceled(true);
                serverPlayer.displayClientMessage(Component.literal("You cannot cast while pooled in blood!").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            MobEffectInstance effect = serverPlayer.getEffect(OreoMobEffectRegistry.SANGUINEPOOLEFFECT.get());
            if (effect != null) {
                event.setCanceled(true);
                serverPlayer.displayClientMessage(Component.literal("You cannot use items in this form!").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            MobEffectInstance effect = serverPlayer.getEffect(OreoMobEffectRegistry.SANGUINEPOOLEFFECT.get());
            if (effect != null) {
                event.setCanceled(true);
                serverPlayer.displayClientMessage(Component.literal("You cannot attack while in this form!").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            MobEffectInstance effect = serverPlayer.getEffect(OreoMobEffectRegistry.SANGUINEPOOLEFFECT.get());
            if (effect != null) {
                event.setCanceled(true);
                serverPlayer.displayClientMessage(Component.literal("You cannot attack while in this form!").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        if (player.hasEffect(OreoMobEffectRegistry.SANGUINEPOOLEFFECT.get())) {
            var movementAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementAttr != null && movementAttr.getModifier(SANGUINE_SPEED_MODIFIER_UUID) == null) {
                AttributeModifier modifier = new AttributeModifier(
                        SANGUINE_SPEED_MODIFIER_UUID,
                        "sanguine_pool_speed_debuff",
                        -0.35D,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                );
                movementAttr.addTransientModifier(modifier);
            }

            StepHeightUtil.setStepHeight(player, 1.0f); // Step over full blocks
        } else {
            var movementAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementAttr != null && movementAttr.getModifier(SANGUINE_SPEED_MODIFIER_UUID) != null) {
                movementAttr.removeModifier(SANGUINE_SPEED_MODIFIER_UUID);
            }

            StepHeightUtil.setStepHeight(player, 0.6f); // Restore default
        }
    }
}
