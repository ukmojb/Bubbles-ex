package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.event.EventHandlerEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;

public class PacketChangeOffset implements IMessage {

    private int offsetChange;

    public PacketChangeOffset() {}

    public PacketChangeOffset(int offsetChange) {
        this.offsetChange = offsetChange;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        offsetChange = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(offsetChange);
    }

    public static class Handler implements IMessageHandler<PacketChangeOffset, IMessage> {
        @Override
        public IMessage onMessage(PacketChangeOffset message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(() -> {
                System.out.println(message.offsetChange);
                ContainerPlayerExpanded container = (ContainerPlayerExpanded) ctx.getServerHandler().player.openContainer;
                BaublesContainer baublesHandler = (BaublesContainer) container.baubles;
                baublesHandler.incrOffset(message.offsetChange);


            });
            return null;
        }
    }
}
