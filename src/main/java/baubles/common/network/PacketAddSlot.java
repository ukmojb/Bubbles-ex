package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.inv.SlotDefinition;
import baubles.common.Baubles;
import baubles.common.init.SlotDefinitions;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
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
                String slotName = message.slotName;
                ResourceLocation location;
                if (!slotName.contains(":")) location = new ResourceLocation(Baubles.MODID, slotName);
                else location = new ResourceLocation(slotName);
                SlotDefinition definition = SlotDefinitions.get(location);
                BaublesApi.getBaublesHandler(entityPlayerSP).addSlot(definition);
            }
            return null;
        }
    }
}
