package com.peeko32213.notsoshrimple.core.network;

import com.peeko32213.notsoshrimple.client.ClientAccess;
import com.peeko32213.notsoshrimple.core.registry.NSSParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ClientboundShrimpTargetingDataInAPacket {
    //part of legacy packets infrastructure
    /*public final Vec3 startPos;
    public final Vec3 deltaPos;
    public final int timer;
    public final int pissID;

    public ClientboundShrimpTargetingDataInAPacket(Vec3 startPosFromShrimp, Vec3 deltaPosFromShrimp, int timerFromShrimp, int urineIdentity) {
        this.startPos = startPosFromShrimp;
        this.deltaPos = deltaPosFromShrimp;
        this.timer = timerFromShrimp;
        this.pissID = urineIdentity;
    }

    public ClientboundShrimpTargetingDataInAPacket(FriendlyByteBuf buffer) {
        this.startPos = new Vec3(buffer.readBlockPos().getX(), buffer.readBlockPos().getY(), buffer.readBlockPos().getZ());
        this.deltaPos = new Vec3(buffer.readBlockPos().getX(), buffer.readBlockPos().getY(), buffer.readBlockPos().getZ());
        this.timer = buffer.readInt();
        this.pissID = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(new BlockPos(this.startPos));
        buffer.writeBlockPos(new BlockPos(this.deltaPos));
        buffer.writeInt(this.timer);
    }

    public boolean handle(Supplier<NetworkEvent.Context> context) {
        final var success = new AtomicBoolean(false);
        context.get().enqueueWork(() ->{
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> success.set(ClientAccess.sendDatatoPiss(startPos, deltaPos, timer, pissID)));
        });

        context.get().setPacketHandled(true);
        return success.get();
    }*/
}
