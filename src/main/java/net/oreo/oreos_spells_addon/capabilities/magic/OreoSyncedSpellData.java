package net.oreo.oreos_spells_addon.capabilities.magic;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.LearnedSpellData;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.gui.overlays.SpellSelection;
import io.redspace.ironsspellbooks.network.ClientboundSyncEntityData;
import io.redspace.ironsspellbooks.network.ClientboundSyncPlayerData;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.util.Log;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import net.oreo.oreos_spells_addon.oreos_spells_addon;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;

public class OreoSyncedSpellData {

    public static final long FLOWINGREDSCALES = 1L << 24;
    public static final long THUNDERCLAPDASH = 1L << 25;
    public static final long SANGUINEPOOLEFFECT = 1L << 26;
    public static final long CHILLEDEFFECT = 1L << 27;
    public static final long BUTTERFLYDAMAGE = 1L << 28;
    public static final long BUTTERFLYDEBUFF = 1L << 29;
    public static final long FLAMEAURAEFFECT = 1L << 30;
    public static final long FLAMEAURADEBUFF = 1L << 31;


    private long syncedEffectFlags;
    private long localEffectFlags;
    private final int serverPlayerId;
    private @Nullable LivingEntity livingEntity;

    public OreoSyncedSpellData(int serverPlayerId) {
        this.serverPlayerId = serverPlayerId;
        this.livingEntity = null;
        this.syncedEffectFlags = 0;
        this.localEffectFlags = 0;
    }

    public OreoSyncedSpellData(LivingEntity livingEntity) {
        this(livingEntity == null ? -1 : livingEntity.getId());
        this.livingEntity = livingEntity;
    }

    public void loadNBTData(CompoundTag compound) {
        this.syncedEffectFlags = compound.getLong("effectFlags");
    }

    public void doSync() {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            Messages.sendToPlayer(new ClientboundSyncPlayerData(MagicData.getPlayerMagicData(serverPlayer).getSyncedData()), serverPlayer);
            Messages.sendToPlayersTrackingEntity(new ClientboundSyncPlayerData(MagicData.getPlayerMagicData(serverPlayer).getSyncedData()), serverPlayer);
        } else if (livingEntity instanceof IMagicEntity abstractSpellCastingMob) {
            Messages.sendToPlayersTrackingEntity(
                    new ClientboundSyncEntityData(MagicData.getPlayerMagicData((LivingEntity) abstractSpellCastingMob).getSyncedData(), abstractSpellCastingMob),
                    (LivingEntity) abstractSpellCastingMob
            );
        }
    }

    public int getServerPlayerId() {
        return serverPlayerId;
    }

    public boolean hasEffect(long effectFlags) {
        return (this.syncedEffectFlags & effectFlags) == effectFlags;
    }

    public boolean hasLocalEffect(long effectFlags) {
        return (this.localEffectFlags & effectFlags) == effectFlags;
    }

    public void removeLocalEffect(long effectFlags) {
        this.localEffectFlags &= ~effectFlags;
    }

    public void addEffects(long effectFlags) {
        this.syncedEffectFlags |= effectFlags;
        doSync();
    }

    public void removeEffects(long effectFlags) {
        this.syncedEffectFlags &= ~effectFlags;
        doSync();
    }

}
