package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.inv.SlotDefinition;
import baubles.common.Baubles;
import baubles.common.init.SlotDefinitions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRemoveSlot implements IMessage {

    String slotName;

    int playerId;

    public PacketRemoveSlot() {}

    public PacketRemoveSlot(EntityPlayer player, String SlotName) {
        this.slotName = SlotName;
        this.playerId = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotName = ByteBufUtils.readUTF8String(buf);
        playerId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, slotName);
        buf.writeInt(playerId);
    }

    public static class Handler implements IMessageHandler<PacketRemoveSlot, IMessage> {
        @Override
        public IMessage onMessage(PacketRemoveSlot message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Baubles.proxy.getClientWorld();
                if (world == null) return;
                Entity p = world.getEntityByID(message.playerId);
                if (p instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) p;
                    if (BaublesApi.getBaublesHandler(player) != null) {
                        String slotName = message.slotName;
                        ResourceLocation location;
                        if (!slotName.contains(":")) location = new ResourceLocation(Baubles.MODID, slotName);
                        else location = new ResourceLocation(slotName);
                        SlotDefinition definition = SlotDefinitions.get(location);
                        BaublesApi.getBaublesHandler(player).removeSlot(definition);
                    }
                }
            });
            return null;
        }
    }
}
