package net.oreo.oreos_spells_addon.effect.BoogieWoogie;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class BoogieWoogieHandler {

    private static final UUID STUN_SPEED_MODIFIER_UUID = UUID.fromString("5cdd0937-1c3e-4856-9fa3-ec8d305b5dc0");

    public static class StunGoal extends Goal {
        private final Mob mob;

        public StunGoal(Mob mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return mob.hasEffect(OreoMobEffectRegistry.BOOGIEWOOGIECONFUSION.get());
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
            mob.setTarget(null);
        }

        @Override
        public void tick() {
            mob.setDeltaMovement(0, mob.getDeltaMovement().y, 0);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        UUID uuid = entity.getUUID();
        boolean hasEffect = entity.hasEffect(OreoMobEffectRegistry.BOOGIEWOOGIECONFUSION.get());

        if (entity.hasEffect(OreoMobEffectRegistry.BOOGIEWOOGIECONFUSION.get())) {
            // This is the first tick we're applying the effect

            // For mobs
            if (entity instanceof Mob mob) {
                if (mob.goalSelector.getAvailableGoals().stream().noneMatch(goal -> goal.getGoal() instanceof StunGoal)) {
                    mob.goalSelector.addGoal(0, new StunGoal(mob));
                }
                mob.getNavigation().stop();
                mob.setDeltaMovement(0, mob.getDeltaMovement().y, 0);
                mob.setTarget(null);
            }

            // For players
            if (entity instanceof ServerPlayer player) {
                AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attr != null && attr.getModifier(STUN_SPEED_MODIFIER_UUID) == null) {
                    attr.addTransientModifier(new AttributeModifier(
                            STUN_SPEED_MODIFIER_UUID,
                            "stun_speed_modifier",
                            -1.0D,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }
                player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
                player.fallDistance = 0;
            }
        }

        // Effect ended
        if (!hasEffect) {
            if (entity instanceof ServerPlayer player) {
                AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (attr != null && attr.getModifier(STUN_SPEED_MODIFIER_UUID) != null) {
                    attr.removeModifier(STUN_SPEED_MODIFIER_UUID);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onSpellCastAttempt(SpellPreCastEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && player.hasEffect(OreoMobEffectRegistry.BOOGIEWOOGIECONFUSION.get())) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(Component.literal("You are stunned!").withStyle(ChatFormatting.RED), true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        cancelIfStunned(event.getEntity(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        cancelIfStunned(event.getEntity(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        cancelIfStunned(event.getEntity(), event);
    }

    private static void cancelIfStunned(Player player, net.minecraftforge.eventbus.api.Event event) {
        if (player instanceof ServerPlayer serverPlayer && player.hasEffect(OreoMobEffectRegistry.BOOGIEWOOGIECONFUSION.get())) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(Component.literal("You are stunned!").withStyle(ChatFormatting.RED), true);
        }
    }
}
