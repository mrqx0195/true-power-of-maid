package net.mrqx.slashblade.maidpower.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.mrqx.slashblade.maidpower.util.MaidSlashBladeAttackUtils;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MaidRankSyncMessage {
    public long rawPoint;
    public int entityId;

    public MaidRankSyncMessage() {
    }

    public static MaidRankSyncMessage decode(FriendlyByteBuf buf) {
        MaidRankSyncMessage msg = new MaidRankSyncMessage();
        msg.rawPoint = buf.readLong();
        msg.entityId = buf.readInt();
        return msg;
    }

    public static void encode(MaidRankSyncMessage msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.rawPoint);
        buf.writeInt(msg.entityId);
    }

    public static void handle(MaidRankSyncMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);

        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }

        BiConsumer<Long, Integer> handler = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> MaidSlashBladeAttackUtils::setClientRank);

        if (handler != null) {
            ctx.get().enqueueWork(() -> handler.accept(msg.rawPoint, msg.entityId));
        }
    }
}
