package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.common.container.ContainerPlayerExpanded;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAddSlot implements IMessage {

    private String slotName;

    public PacketAddSlot() {}

    public PacketAddSlot(String SlotName) {
        this.slotName = SlotName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, slotName);
    }

    public static class Handler implements IMessageHandler<PacketAddSlot, IMessage> {
        @Override
        public IMessage onMessage(PacketAddSlot message, MessageContext ctx) {
            EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().player;
            if (BaublesApi.getBaublesHandler(entityPlayerSP) != null) {
                BaublesApi.getBaublesHandler(entityPlayerSP).addSlot(message.slotName);
            }
            return null;
        }
    }
}
