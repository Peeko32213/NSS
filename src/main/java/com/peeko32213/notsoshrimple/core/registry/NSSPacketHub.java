package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import com.peeko32213.notsoshrimple.core.network.ClientboundShrimpTargetingDataInAPacket;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

public final class NSSPacketHub {
    private NSSPacketHub() {
    }

    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(NotSoShrimple.MODID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    public static void init() {
        int index = 0;
        //serverbound = to server, from client
        //clientbound = to client, from server
        INSTANCE.messageBuilder(ClientboundShrimpTargetingDataInAPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundShrimpTargetingDataInAPacket::encode)
                .decoder(ClientboundShrimpTargetingDataInAPacket::new)
                .consumerMainThread(ClientboundShrimpTargetingDataInAPacket::handle).add();

    }
}
