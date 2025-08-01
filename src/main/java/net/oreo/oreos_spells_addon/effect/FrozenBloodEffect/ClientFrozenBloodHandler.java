package net.oreo.oreos_spells_addon.effect.FrozenBloodEffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oreo.oreos_spells_addon.registries.OreoMobEffectRegistry;
import net.minecraftforge.client.event.MovementInputUpdateEvent;


@Mod.EventBusSubscriber(modid = "oreos_spells_addon", value = Dist.CLIENT)
public class ClientFrozenBloodHandler {

    @SubscribeEvent
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.hasEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get())) {
            event.getInput().forwardImpulse = 0;
            event.getInput().leftImpulse = 0;
            event.getInput().jumping = false;
            event.getInput().shiftKeyDown = false;
        }
    }


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.hasEffect(OreoMobEffectRegistry.FROZENBLOODEFFECT.get())) {
            player.setDeltaMovement(0, 0, 0);
            player.setSprinting(false);
            player.fallDistance = 0;
        }
    }
}
