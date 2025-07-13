package net.mrqx.slashblade.maidpower.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;

public class NetworkManager {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TruePowerOfMaid.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, MaidRankSyncMessage.class, MaidRankSyncMessage::encode, MaidRankSyncMessage::decode,
                MaidRankSyncMessage::handle);
    }
}
