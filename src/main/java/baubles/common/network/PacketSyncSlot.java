package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import baubles.common.Baubles;
import baubles.common.init.SlotDefinitions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncSlot implements IMessage {

    int playerId;
    byte slot = 0;
    String slotName;

    public PacketSyncSlot() {}

    public PacketSyncSlot(EntityPlayer p, int slot, String slotName) {
        this.slot = (byte) slot;
        this.slotName = slotName;
        this.playerId = p.getEntityId();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(playerId);
        buffer.writeByte(slot);
        new PacketBuffer(buffer).writeString(slotName);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        playerId = buffer.readInt();
        slot = buffer.readByte();
        slotName = new PacketBuffer(buffer).readString(999);
    }

    public static class Handler implements IMessageHandler<PacketSyncSlot, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncSlot message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Baubles.proxy.getClientWorld();
                if (world == null) return;
                Entity p = world.getEntityByID(message.playerId);
                if (p instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) p;
                    IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
                    String slotName = message.slotName;
                    if (message.slotName != "null") {
                        ResourceLocation location;
                        if (!slotName.contains(":")) location = new ResourceLocation(Baubles.MODID, slotName);
                        else location = new ResourceLocation(slotName);
                        SlotDefinition definition = SlotDefinitions.get(location);
                        baubles.setSlot(message.slot, definition);
                    } else {
                        baubles.setSlot(message.slot, null);
                    }
                }
            });
            return null;
        }
    }
}


//public static class Handler implements IMessageHandler<PacketSyncSlot, IMessage> {
//        @Override
//        public IMessage onMessage(PacketSyncSlot message, MessageContext ctx) {
//            return null;
//        }
////        @Override
////        public IMessage onMessage(PacketSyncSlot message, MessageContext ctx) {
////            System.out.println("PacketSyncSlot");
////            Minecraft.getMinecraft().addScheduledTask(() -> {
////                World world = Baubles.proxy.getClientWorld();
////                if (world == null) return;
////                EntityPlayer player = world.getPlayerEntityByName(message.playerName);
////                if (player != null) {
////                    IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
////                    String slotName = message.slotName;
////                    if (message.slotName != "null") {
////                        ResourceLocation location;
////                        if (!slotName.contains(":")) location = new ResourceLocation(Baubles.MODID, slotName);
////                        else location = new ResourceLocation(slotName);
////                        SlotDefinition definition = SlotDefinitions.get(location);
////                        baubles.setSlot(message.slot, definition);
////                    } else {
////                        baubles.setSlot(message.slot, null);
////                    }
////                }
////            });
////            return null;
////        }
//    }

